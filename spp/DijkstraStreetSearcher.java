package hw8.spp;

import hw8.graph.Edge;
import hw8.graph.Graph;
import hw8.graph.Vertex;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class DijkstraStreetSearcher extends StreetSearcher {
  PriorityQueue<PriorDistance> distance;

  /**
   * Creates a StreetSearcher object.
   *
   * @param graph an implementation of Graph ADT.
   */
  public DijkstraStreetSearcher(Graph<String, String> graph) {
    super(graph);
  }

  private static class PriorDistance {
    double distance;
    Vertex<String> from; // previous
    Vertex<String> to; // vertex
    boolean explored;



    PriorDistance(Vertex<String> from, Vertex<String> to) {
      distance = 1.0 / 0.0;
      explored = false;
      this.from = from;
      this.to = to;
    }

    PriorDistance(double distance, Vertex<String> from, Vertex<String> to, boolean explored) {
      this.from = from;
      this.to = to;
      this.distance = distance;
      this.explored = explored;
    }

    private void setDistance(double distance) {
      if (this.distance > distance) {
        this.distance = distance;
      }
    }

    @Override
    public int hashCode() {
      return this.to.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }

      if (obj == null || obj.getClass() != this.getClass()) {
        return false;
      }

      PriorDistance edge = (PriorDistance) (obj);

      // Returning - Are two objects referring to the same edge?
      return edge.to.equals(this.to);
    }
  }

  private static class DistanceComparator implements Comparator<PriorDistance> {
    public int compare(PriorDistance o1, PriorDistance o2) {
      if (o1.distance > o2.distance) {
        return 1;
      } else if (o1.distance < o2.distance) {
        return -1;
      }
      return 0;
    }
  }



  @Override
  public void findShortestPath(String startName, String endName) {
    try {
      checkValidEndpoint(startName);
      checkValidEndpoint(endName);
    } catch (IllegalArgumentException ex) {
      System.out.println(ex.getMessage());
      return;
    }
    Vertex<String> start = vertices.get(startName);
    Vertex<String> end = vertices.get(endName);

    distance = new PriorityQueue<>(new DistanceComparator());
    distance.add(new PriorDistance(0, null, start, true));

    double totalDist = -1;
    findShortestPathHelper(start, end);

    totalDist = getDistance(end);
    // These method calls will create and print the path for you
    List<Edge<String>> path = getPath(end, start);
    if (VERBOSE) {
      printPath(path, totalDist);
    }
  }

  private void findShortestPathHelper(Vertex<String> start, Vertex<String> end) {
    for (Vertex<String> vertex : graph.vertices()) { // repeat N times
      PriorDistance smallestDist = distance.peek();

      // update smallestDist until unexplored edge is found
      // let v be unexplored vertex with smallest distance
      for (PriorDistance edge : distance) {
        if (!edge.explored) {
          edge.explored = true;
          smallestDist = edge;
          break;
        }
      }

      for (Edge<String> neighbor : graph.outgoing(smallestDist.to)) {
        PriorDistance edge = new PriorDistance(smallestDist.to, graph.to(neighbor));
        if (!distance.contains(edge)) { //for every u: unexplored neighbor(v)
          distance.add(edge);
          graph.label(graph.to(neighbor), neighbor);
          edge.setDistance(smallestDist.distance + (double) graph.label(neighbor));
        }
      }
    }
  }

  private double getDistance(Vertex<String> vertex) {
    if (vertex == null) {
      return (int) Double.POSITIVE_INFINITY;
    }
    for (PriorDistance element : distance) {
      if (element.to.equals(vertex)) {
        return element.distance;
      }
    }
    return (int) Double.POSITIVE_INFINITY;
  }
}
