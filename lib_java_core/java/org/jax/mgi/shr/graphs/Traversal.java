// $HEADER$
// $NAME$

package org.jax.mgi.shr.graphs;


import java.io.*;
import java.util.*;

/** An abstract interface for a graph traversal over a graph.
 *  A traversal is a procedure that visits nodes and crosses edges
 *  in a graph. A specific instances of a traversal will include processing
 *  of nodes and edges to accomplish some goal.
 *  This package separates the traversal logic and
 *  state management from the node and edge processing
 *  needed for a specific application. The former are encapsulated
 *  by the classes in this package, while the latter are implemented
 *  via callbacks issues at defined points during the traversal.
 *  <P>
 *  Traversal execution consis of a top-level loop and a nested reachability
 *  loop.
 *  The top-level loop considers each starting node in turn. (If no
 *  starting nodes have been specified, the top level considers each
 *  node in the graph.) If the
 *  node has not already been visited, then the traversal visits that
 *  node and everything reachable from it. Thus, every iteration of the
 *  top-level loop visits one connected component of the graph.
 *  Different strategies, such
 *  as depth-first and breadth-first, vary in how they perform the
 *  reachability part.
 *  <P>
 *  @is This interface specified basic method for setting the parameters of
 *  a traversal, e.g., the graph and the starting nodes.
 *  @has reference to a graph and a collection of starting nodes.
 *  @does provides basic methods for getting/setting the graph and the
 *  starting node(s), and for initiating a traversal.
 */
public interface Traversal {

    /** Returns the collection of starting nodes.
     *  @return the current collection of starting nodes. If null,
     *  the traversal will visit all nodes. Otherwise, specifies one
     *  or more starting points for traversal.
     */
    public Collection getStartNodes();

    /** Sets the starting nodes for this traversal.
     *  If sns is null, traversal will visit all nodes.
     *  @param sns a collection of the starting nodes. If set to null,
     *  the traversal will visit all nodes.
     *  @return nothing
     *  @effects starting nodes for traversal set to this collection.
     *  References sns.
     */
    public void setStartNodes(Collection sns);

    /** Convenience - when there is a single starting node.
     *  @param sn a node
     *  @return nothing
     *  @effects sets starting nodes for traversal to sn. References sn.
    */
    public void setStartNode(Object sn);

    /** Returns the graph being traversed.
     *  @return a Graph object. Can be null if traversal not
     *  fully initialized.
     */
    public Graph getGraph();

    /** Sets the graph to traverse. Note that starting nodes are NOT
     *  changed by this call.
     *  @param g a Graph to traverse
     *  @return nothing
     *  @effects sets the graph to g. References g.
     */
    public void setGraph(Graph g);

    /** Traverses current graph from current set of starting nodes.
     *  @return nothing
     *  @effects Updates visited set. Other effects depend on specific
     *  traversal.
     */
    public void go();

    /** Traverses graph g, using the current starting nodes.
     *  @param g the Graph to traverse
     *  @return nothing
     *  @effects sets the graph. Then calls go().
     */
    public void go(Graph g);

    /** Traverses g from the specified starting nodes.
     *  @param g the Graph to traverse
     *  @param startNode the node to start from
     *  @return nothing
     *  @effects sets the graph and the start node. Then calls go().
     */
    public void go(Graph g, Object startNode);

    /** Sets the graph and starting nodes, and traverses.
     *  @param g the Graph to traverse
     *  @param startNodes collection of nodes to start from
     *  @return nothing
     *  @effects sets the graph and the start nodes. Then calls go().
     */
    public void go(Graph g, Collection startNodes);

    /** Sets current start node and traverses graph.
     *  @param startNode the node to start from
     *  @return nothing
     *  @effects sets the start node. Then calls go().
     */
    public void go(Object startNode);

    /** Sets the starting nodes and traverses current graph.
     *  @param startNodes collection of nodes to start from
     *  @return nothing
     *  @effects sets the start nodes. Then calls go().
    */
    public void go(Collection startNodes);

}


// $LOG$
