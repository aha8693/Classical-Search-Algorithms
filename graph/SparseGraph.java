package hw8.graph;

import exceptions.InsertionException;
import exceptions.PositionException;
import exceptions.RemovalException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * An implementation of Graph ADT using incidence lists
 * for sparse graphs where most nodes aren't connected.
 *
 * @param <V> Vertex element type.
 * @param <E> Edge element type.
 */
public class SparseGraph<V, E> implements Graph<V, E> {

  // TODO You may need to add fields/constructor here!
  private HashSet<Vertex<V>> vertices;
  private List<Edge<E>> totalEdges;

  /**
 * Constructor of SparseGraph.
 */
  public SparseGraph() {
    vertices = new HashSet<>();
    totalEdges = new LinkedList<>();
  }


  // Converts the vertex back to a VertexNode to use internally
  private VertexNode<V> convert(Vertex<V> v) throws PositionException {
    try {
      VertexNode<V> gv = (VertexNode<V>) v;
      if (gv.owner != this) {
        throw new PositionException();
      }
      return gv;
    } catch (NullPointerException | ClassCastException ex) {
      throw new PositionException();
    }
  }

  // Converts and edge back to a EdgeNode to use internally
  private EdgeNode<E> convert(Edge<E> e) throws PositionException {
    try {
      EdgeNode<E> ge = (EdgeNode<E>) e;
      if (ge.owner != this) {
        throw new PositionException();
      }
      return ge;
    } catch (NullPointerException | ClassCastException ex) {
      throw new PositionException();
    }
  }

  @Override
  public Vertex<V> insert(V v) throws InsertionException {
    if (v == null) {
      throw new InsertionException();
    }
    VertexNode<V> vertexNode = new VertexNode<>(v, this);
    if (!vertices.add(vertexNode)) {
      throw new InsertionException();
    }
    return vertexNode;
  }



  @Override
  public Edge<E> insert(Vertex<V> from, Vertex<V> to, E e)
      throws PositionException, InsertionException {
    if  (isInvalidVertexPosition(from) || isInvalidVertexPosition(to)
           || !vertices.contains(from) || !vertices.contains(to)) {
      throw new PositionException();
    }

    if (from.equals(to)) {
      throw new InsertionException(); // self-loop
    }

    VertexNode<V> fromNode = convert(from);
    VertexNode<V> toNode = convert(to);
    if (hasDuplicateEdge(fromNode, toNode)) { // duplicated edge
      throw new InsertionException();
    }
    EdgeNode<E> edgeNode = new EdgeNode<E>(fromNode, toNode, e, this);
    fromNode.outgoings.add(edgeNode);
    toNode.incomings.add(edgeNode);

    return edgeNode;
  }

  private boolean hasDuplicateEdge(VertexNode<V> from, VertexNode<V> to) {
    for (Edge<E> edgeNode : from.outgoings) {
      if (convert(edgeNode).to.equals(to)) {
        return true;
      }
    }
    return false;
  }

  private boolean isInvalidVertexPosition(Vertex<V> position) {
    try {
      convert(position);
    } catch (NullPointerException | ClassCastException ex) {
      return true;
    }
    return false;
  }

  private boolean isInvalidEdgePosition(Edge<E> position) {
    try {
      convert(position);
    } catch (NullPointerException | ClassCastException ex) {
      return true;
    }
    return false;
  }

  @Override
  public V remove(Vertex<V> v) throws PositionException, RemovalException {
    if (isInvalidVertexPosition(v)) {
      throw new PositionException();
    }
    VertexNode<V> vertexNode = convert(v);

    if (vertexNode.incomings.size() != 0 || vertexNode.outgoings.size() != 0) { // incident edges exist
      throw new RemovalException();
    }

    if (!vertices.remove(vertexNode)) { // already removed case
      throw new PositionException();
    }
    VertexNode<V> temp = vertexNode;
    vertexNode = null;
    return temp.data;
  }

  @Override
  public E remove(Edge<E> e) throws PositionException {
    if (isInvalidEdgePosition(e)) {
      throw new PositionException();
    }
    EdgeNode<E> edgeNode = convert(e);
    VertexNode<V> to = edgeNode.to;
    VertexNode<V> from = edgeNode.from;

    if (!to.incomings.remove(edgeNode)) { // already removed case
      throw new PositionException();
    }
    if (!from.outgoings.remove(edgeNode)) {
      throw new PositionException();
    }

    EdgeNode<E> temp = edgeNode;
    edgeNode = null;
    return temp.data;
  }

  @Override
  public Iterable<Vertex<V>> vertices() {
    return Collections.unmodifiableSet(vertices);
  }

  @Override
  public Iterable<Edge<E>> edges() {
    for (Vertex<V> element: vertices) {
      totalEdges.addAll(convert(element).outgoings);
    }
    return Collections.unmodifiableList(totalEdges);
  }

  @Override
  public Iterable<Edge<E>> outgoing(Vertex<V> v) throws PositionException {
    if (!vertices.contains(v)) { // if vertex is already removed
      throw new PositionException();
    }
    return convert(v).outgoings;
  }

  @Override
  public Iterable<Edge<E>> incoming(Vertex<V> v) throws PositionException {
    if (!vertices.contains(v)) { // if vertex is already removed
      throw new PositionException();
    }
    return convert(v).incomings;
  }

  @Override
  public Vertex<V> from(Edge<E> e) throws PositionException {
    if (isInvalidEdgePosition(e)) {
      throw new PositionException();
    }
    EdgeNode<E> edgeNode = convert(e);
    if (!edgeNode.from.outgoings.contains(edgeNode)) { // if edge is not present in the graph
      throw new PositionException();
    }
    return edgeNode.from;
  }

  @Override
  public Vertex<V> to(Edge<E> e) throws PositionException {
    if (isInvalidEdgePosition(e)) {
      throw new PositionException();
    }
    EdgeNode<E> edgeNode = convert(e);
    if (!edgeNode.to.incomings.contains(edgeNode)) { // if edge is not present in the graph
      throw new PositionException();
    }

    return edgeNode.to;
  }

  @Override
  public void label(Vertex<V> v, Object l) throws PositionException {
    // TODO Implement me!
    if (isInvalidVertexPosition(v)) {
      throw new PositionException();
    }
    VertexNode<V> vertexNode = convert(v);
    if (!vertices.contains(vertexNode)) {
      throw new PositionException();
    }
    vertexNode.label = l;
  }

  @Override
  public void label(Edge<E> e, Object l) throws PositionException {
    if (isInvalidEdgePosition(e)) {
      throw new PositionException();
    }
    EdgeNode<E> edgeNode = convert(e);
    VertexNode<V> from = edgeNode.from;
    VertexNode<V> to = edgeNode.to;
    if (!from.outgoings.contains(edgeNode) || !to.incomings.contains(edgeNode)) {
      throw new PositionException();
    }
    edgeNode.label = l;
  }

  @Override
  public Object label(Vertex<V> v) throws PositionException {
    if (isInvalidVertexPosition(v)) {
      throw new PositionException();
    }
    VertexNode<V> vertexNode = convert(v);
    if (!vertices.contains(vertexNode)) {
      throw new PositionException();
    }
    return vertexNode.label;
  }

  @Override
  public Object label(Edge<E> e) throws PositionException {
    if (isInvalidEdgePosition(e)) {
      throw new PositionException();
    }
    EdgeNode<E> edgeNode = convert(e);
    VertexNode<V> from = edgeNode.from;
    VertexNode<V> to = edgeNode.to;
    if (!from.outgoings.contains(edgeNode) || !to.incomings.contains(edgeNode)) {
      throw new PositionException();
    }
    return edgeNode.label;
  }

  @Override
  public void clearLabels() {
    for (Vertex<V> vertex : vertices()) {
      VertexNode<V> vertexNode = convert(vertex);
      vertexNode.label = null;
      for (Edge<E> edge : vertexNode.outgoings) {
        convert(edge).label = null;
      }
      for (Edge<E> edge : vertexNode.incomings) {
        convert(edge).label = null;
      }
    }
  }

  @Override
  public String toString() {
    GraphPrinter<V, E> gp = new GraphPrinter<>(this);
    return gp.toString();
  }

  // Class for a vertex of type V
  private final class VertexNode<V> implements Vertex<V> {
    V data;
    Graph<V, E> owner;
    Object label;
    LinkedList<Edge<E>> outgoings;
    LinkedList<Edge<E>> incomings;
    // TODO You may need to add fields/methods here!

    VertexNode(V v) {
      this.data = v;
      this.label = null;
      outgoings = new LinkedList<>();
      incomings = new LinkedList<>();
    }

    VertexNode(V v, Graph<V, E> owner) {
      this.data = v;
      this.label = null;
      outgoings = new LinkedList<>();
      incomings = new LinkedList<>();
      this.owner = owner;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }

      if (obj == null || obj.getClass() != this.getClass()) {
        return false;
      }

      VertexNode<V> node = (VertexNode<V>) (obj);

      return (node.data.equals(this.data));
    }

    @Override
    public int hashCode() {
      return this.data.hashCode();
    }

    @Override
    public V get() {
      return this.data;
    }
  }

  //Class for an edge of type E
  private final class EdgeNode<E> implements Edge<E> {
    E data;
    Graph<V, E> owner;
    VertexNode<V> from;
    VertexNode<V> to;
    Object label;
    // TODO You may need to add fields/methods here!

    // Constructor for a new edge
    EdgeNode(VertexNode<V> f, VertexNode<V> t, E e) {
      this.from = f;
      this.to = t;
      this.data = e;
      this.label = null;
    }

    EdgeNode(VertexNode<V> f, VertexNode<V> t, E e, Graph<V, E> owner) {
      this.from = f;
      this.to = t;
      this.data = e;
      this.label = null;
      this.owner = owner;
    }

    @Override
    public E get() {
      return this.data;
    }
  }
}
