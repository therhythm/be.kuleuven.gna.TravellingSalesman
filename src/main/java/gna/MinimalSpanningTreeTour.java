package gna;

import java.util.*;

/**
 * A tour constructed using the minimal spanning tree heuristic.
 */
public class MinimalSpanningTreeTour extends Tour {

    public class MSTEdge {
        public final Point p1, p2;

        public MSTEdge(Point p1, Point p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public boolean hasPoint(Point point) {
            if (point == p1 || point == p2) {
                return true;
            } else {
                return false;
            }
        }

        public Point getOtherPoint(Point point) {
            if (point == p1) {
                return p2;
            } else if (point == p2) {
                return p1;
            } else {
                return null;
            }
        }

        public Point getOtherPointVisited() {
            if (points.contains(p1)) {
                return p2;
            } else if (points.contains(p2)) {
                return p1;
            } else {
                return null;
            }
        }

        public boolean bothPointsVisited() {
            if (points.contains(p1) && points.contains(p2)) {
                return true;
            } else {
                return false;
            }
        }

        private double distance;
    }

    public MinimalSpanningTreeTour(World world) {
        super(world);
        constructMST(world);
    }

    @Override
    public double getTotalDistance() {
        double totalDistance = 0.0;
        List<Point> tour = getVisitSequence();
        Iterator<Point> iterator1 = tour.iterator();
        Iterator<Point> iterator2 = tour.subList(1, tour.size()).iterator();
        Point point1, point2;
        while (iterator2.hasNext()) {
            point1 = iterator1.next();
            point2 = iterator2.next();
            totalDistance += point1.distanceTo(point2);
        }
        point1 = iterator1.next();
        totalDistance += tour.get(0).distanceTo(point1);
        return totalDistance;
    }

    /**
     * Return the root of the MST used to construct the visit sequence.
     * <p/>
     * This method returns null if and only if <code>getWorld().getPoints()</code> is empty.
     */
    public Point getMSTRoot() {
        return getWorld().getPoints().get(0);
    }

    /**
     * Return the edges on the MST used to construct the visit sequence.
     * <p/>
     * The result of this method is never null.
     */
    public List<MSTEdge> getMST() {
        return usedEdges;
    }

    @Override
    /**
     * The visit sequence is a PRE-ORDER traversal of the MST
     * starting at a root (e.g. the first point of the world).
     *
     * Traverse the children of each node in increasing order of distance.
     *
     * Return the empty list if world is empty.
     */
    public List<Point> getVisitSequence() {
        visitedPoints = new ArrayList<Point>();
        Point first = getMSTRoot();
        HashSet<MSTEdge> visitedEdges = new HashSet<MSTEdge>();
        visitedPoints.add(first);
        constructTour(first, visitedEdges);
        return visitedPoints;
    }

    private void constructMST(World world) {
        edges = new HashSet<MSTEdge>();
        points = new HashSet<Point>();
        double edgeLength;
        Point point = world.getPoints().get(0);
        points.add(point);
        MSTEdge edge;
        PriorityQueue<MSTEdge> queue = new PriorityQueue<MSTEdge>(10, new EdgeComparator());
        int i = 0;
        while (i != world.getNbPoints() - 1) {
            for (Point point2 : world.getPoints()) {
                if (point != point2 && !points.contains(point2)) {
                    edgeLength = point.distanceTo(point2);
                    edge = new MSTEdge(point, point2);
                    edge.setDistance(edgeLength);
                    queue.add(edge);
                }
            }
            do {
                edge = queue.remove();
            } while (edge.bothPointsVisited());
            edges.add(edge);
            point = edge.getOtherPointVisited();
            points.add(point);
            i++;
        }
    }

    private void constructTour(Point point, HashSet<MSTEdge> visitedEdges) {
        usedEdges = new ArrayList<MSTEdge>();
        PriorityQueue<MSTEdge> queue = new PriorityQueue<MSTEdge>(10, new EdgeComparator());
        for (MSTEdge edge : getAllEdgesFromPoint(point, visitedEdges)) {
            queue.add(edge);
        }
        int count = 0;
        while (!queue.isEmpty()) {
            MSTEdge edge = queue.remove();
            Point next = edge.getOtherPoint(point);
            visitedPoints.add(next);
            visitedEdges.add(edge);
            if (count == 0) {
                usedEdges.add(edge);
            }
            constructTour(next, visitedEdges);
            count++;
        }
    }

    private List<MSTEdge> getAllEdgesFromPoint(Point point, HashSet<MSTEdge> visitedEdges) {
        List<MSTEdge> edgeList = new ArrayList<MSTEdge>();
        for (MSTEdge edge : edges) {
            if (edge.hasPoint(point) && !visitedEdges.contains(edge)) {
                edgeList.add(edge);
            }
        }
        return edgeList;
    }

    private class EdgeComparator implements Comparator<MSTEdge> {
        @Override
        public int compare(MSTEdge edge1, MSTEdge edge2) {
            if (edge1.getDistance() < edge2.getDistance()) {
                return -1;
            }
            if (edge1.getDistance() > edge2.getDistance()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private HashSet<MSTEdge> edges;
    private HashSet<Point> points;
    private List<MSTEdge> usedEdges;
    private List<Point> visitedPoints;

}
