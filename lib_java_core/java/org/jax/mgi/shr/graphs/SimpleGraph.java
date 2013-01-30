// $HEADER$
// $NAME$

package org.jax.mgi.shr.graphs;

import java.io.*;
import java.util.*;

/** An implementation of graphs, as defined by the Graph interface.
 *  For information and method documentation, see the Graph
 *  interface documentation.
 *  @is an approximation of the mathematical notion of graphs, that is
 *  sets of nodes and edges.
 *  @does provides basic graph manipulations (see interface Graph).
 *  See classes Traversals and Graphs for additional graph algorithms
 *  and utilities.
 *  @has a map from nodes to their neighbors. The neighbors of a
 *  node are themselves a map: each neighbor of n maps to the label of
 *  the edge between them. Also has a default edge label value, and
 *  maintains counters for the number of nodes and edges.
 */
public
class SimpleGraph implements Graph {

    // -----------------------------------------------NODE METHODS

    public boolean hasNode(Object n){
    return this.neighborMap.containsKey(n);
    }

    public boolean addNode(Object n){
    if( ! this.hasNode(n) ){
        this.neighborMap.put( n, new HashMap() );
        this.numNodes++;
        return true;
    }
    else
        return false;
    }

    public Iterator iterNeighbors(Object n){
    Map map2 = (Map) this.neighborMap.get(n);
    return (map2 == null ? null : map2.keySet().iterator());
    }

    // -----------------------------------------------EDGE METHODS

    public boolean hasEdge(Object n1, Object n2){
    Map map2 = (Map) this.neighborMap.get(n1);
    return (map2 != null && map2.containsKey(n2));
    }

    public boolean addEdge(Object n1, Object n2){
    if( ! this.hasEdge(n1, n2) ){
        this.addNode(n1);
        this.addNode(n2);
        ((Map)this.neighborMap.get(n1)).put(n2, this.defaultLabel);
        ((Map)this.neighborMap.get(n2)).put(n1, this.defaultLabel);
        this.numEdges++;
        return true;
    }
    else
        return false;
    }

    public boolean addEdge(Object n1, Object n2, Object lbl){
    boolean rval = false;
    if( ! this.hasEdge(n1, n2) ){
        this.addNode(n1);
        this.addNode(n2);
        this.numEdges++;
        rval = true;
    }
    ((Map)this.neighborMap.get(n1)).put(n2, lbl);
    ((Map)this.neighborMap.get(n2)).put(n1, lbl);

    return rval;
    }

    public Object getEdgeLabel(Object n1, Object n2){
    Map map2 = (Map) this.neighborMap.get(n1);
    return (map2 == null ? null : map2.get(n2));
    }

    // -----------------------------------------------GRAPH METHODS

    public Set getNodes(){
        return new HashSet( this.neighborMap.keySet() );
    }

    public Iterator iterNodes(){
        return this.neighborMap.keySet().iterator();
    }

    public int nNodes(){
    return this.numNodes;
    }

    public int nEdges(){
        return this.numEdges;
    }

    public boolean isEmpty(){
    return (this.nNodes() == 0);
    }

    public void setDefaultEdgeLabel(Object lbl){
        this.defaultLabel = lbl;
    }

    public Object getDefaultEdgeLabel(){
    return this.defaultLabel;
    }

    // -----------------------------------------------REPRESENTATION

    /* A graph is represented as a 2-level map. The first level maps
     * each node to its neighbors. The neighbors are also a in a map;
     * each neighbor of n maps to the label of the edge between them.
     * With this representation, the total number of maps (HashMap objects)
     * is #nodes+1; there is one first level map, and
     * for every node, there is a 2nd level map. The number of map entries
     * is #nodes + 2*(#edges); the top level map contains one entry for
     * each node, and each edge results in two entries in the second level,
     * one in each of the two incident nodes' maps. The number of edge
     * label objects is controlled by the application, but is between 0
     * (all labels are null) and #edges (each edge gets a distinct label).
     */
    private Map		neighborMap	= new HashMap(); // two level map
    private Object	defaultLabel	= null; // default label for edges
    private int		numNodes	= 0; // number of nodes
    private int		numEdges	= 0; // number of edges

} // END class SimpleGraph


// $LOG$
