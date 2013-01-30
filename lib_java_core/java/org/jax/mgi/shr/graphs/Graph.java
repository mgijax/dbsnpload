// $HEADER$
// $NAME$

package org.jax.mgi.shr.graphs;

import java.io.*;
import java.util.*;

/** Approximates mathematical graphs, i.e., sets of nodes and edges.
 *
 *  @is a set of nodes and a set of undirected edges between the nodes.
 *  <B>Nodes</B>. A node can be anything of type Object. Nodes must honor the
 *  general object contract, to wit: if a.equals(b), then a.hashCode() ==
 *  b.hashCode().
 *  (NOTE: if you don't understand this, it's probably not a problem. For more
 *  information, see the Java documentation, particularly, class Object and
 *  the java.util.Collection framework.)
 *  <B>Edges</B>. An edge is a non-directional (or bi-directional - take your
 *  pick) association between two nodes. When denoting an edge, we are forced
 *  to pick an order because text is linear, the edge itself is unordered.
 *  Thus, if nodes a and b are connected by an edge, then both (a,b) and (b,a)
 *  denote that edge. In concrete terms, methods like hasEdge do not care
 *  which order the nodes arguments are given. Edges are not directly visible
 *  to the user; there is no Edge class/interface. Instead, the user specifies
 *  edges by the nodes they join. <B>Edge labels</B>. Each edge may
 *  optionally carry an object of the user's choosing. We generically refer
 *  to these objects as 'labels' because they often are Strings; however, an
 *  edge label can be any object, such as a weight, or annotation record, or
 *  whatever.
 * <P>
 *  For now, Graphs do not allow self-edges (an edge between
 *  a node and itself) nor multi-edges (multiple edges between
 *  the same pair of nodes). We may reexamine this,
 *  if there is time later.
 *
 *  @does provides basic structuring methods over nodes and
 *  edges: methods to add them to the graph, test for their presence,
 *  and return them (or iterators over them).
 *  Does <b>NOT</B>: no "graph algorithm" methods are provided.
 *  (But see: class Traversals and class Graphs in this package.)
 *
 *  @has Although this is only an interface definition, it requires
 *  any implementation to at least give the appearance of having certain
 *  attributes, namely, a set of nodes, edges with edge labels, and
 *  a default value to use for edge labels.
 */
public interface Graph {

    /** Tests whether the graph contains n as a node.
     *  @param n an object
     *  @return true iff the graph contains n
     */
    public boolean	hasNode(Object n);

    /** Adds n as a node in the graph. Has no effect if graph
     *  already contains n.
     *  @param n an object to add as a node
     *  @return true if n was added, false if n was already a node
     *  @effects graph will contain a reference to n
     */
    public boolean		addNode(Object n);

    /** Returns an iterator over the neighbors of n. A neighbor is a node
     *  connected to n by an edge.
     *  @param n a node
     *  @return iterator that returns the neighbor nodes of n.
     *  Returns null if n is not a node.
     */
    public Iterator iterNeighbors(Object n);

    /** Tests whether the graph contains an edge between n1 and n2.
     *  @param n1 an object
     *  @param n2 another object
     *  @return true iff the graph has an edge between n1 and n2
     */
    public boolean	hasEdge(Object n1, Object n2);

    /** Adds an edge between nodes n1 and n2 with label lbl.
     *  n1 and n2 are added as nodes, if necessary.
     *  If the edge already exists, its label is set to lbl.
     *  @param n1 an object
     *  @param n2 another object
     *  @param lbl would you believe, a third object
     *  @return true if a new edge was created, false if the edge already
     *  existed.
     *  @effects May add n1 and n2 as nodes. May create new edge. Sets the
     *  edge's label.
     */
    public boolean	addEdge(Object n1, Object n2, Object lbl);

    /** If an edge exists bewteen n1 and n2, has no effect.
     *  Otherwise, creates an edge between n1 and n2, with the default edge
     *  label.
     *  n1 and n2 are added as nodes, if necessary.
     *  @param n1 an object
     *  @param n2 another object
     *  @return true if a new edge was created, false if the edge already
     *  existed.
     *  @effects None if there is already an edge between n1 and n2.
     *  Otherwise, creates an edge. May add n1 and n2 as nodes.
     */
    public boolean	addEdge(Object n1, Object n2);

    /** If there is an edge connecting n1 and n2, returns the edge label.
     *  Otherwise returns null.
     * @param n1 an object
     * @param n2 another object
     * @return If there is an edge between n1 and n2, its edge label;
     * null if the graph contains no such edge. Note that an edge
     * can have a null label, so be careful.
     */
    public Object	getEdgeLabel(Object n1, Object n2);

    /** Returns an iterator over the nodes of the graph.
     *  @return an Iterator that returns the nodes of the graph.
     */
    public Iterator	iterNodes();

    /** Returns a copy of the graph's set of nodes.
     *  @return a java.util.Set containing the nodes of the graph
     */
    public Set		getNodes();

    /** Returns the number of nodes in the graph.
     *  @return the number of nodes currently in the graph.
     */
    public int nNodes();

    /** Returns the number of edges in the graph.
     *  @return the number of edges currently in the graph.
     */
    public int nEdges();

    /** Returns true iff the graph has no nodes or edges.
     *  @return true if the graph is empty, false otherwise
     */
    public boolean isEmpty();

    /** Returns the default edge label. New edges created
     *  by addEdge(n1, n2) are given this label.
     *  @return the current default edge label
     */
    public Object getDefaultEdgeLabel();

    /** Sets the default edge label. New edges created
     *  by addEdge(n1, n2) will be given this label.
     *  @param lbl an object (or null) to use as the default edge label
     *  @return nothing
     */
    public void setDefaultEdgeLabel(Object lbl);

}; // END interface Graph


// $LOG$
