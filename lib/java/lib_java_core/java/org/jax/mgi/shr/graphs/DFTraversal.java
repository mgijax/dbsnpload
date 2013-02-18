// $HEADER$
// $NAME$

package org.jax.mgi.shr.graphs;

import java.io.*;
import java.util.*;

/** A DFTraversal performs a depth-first traversal of a graph,
 *  invoking defined callbacks at specific points. Customized
 *  traversals are defined by subclassing DFTraversal, by adding
 *  state and overriding desired callbacks.
 *
 *  A DFTraversal begins with a set of starting nodes. The top
 *  level loop takes the next unvisited node from the list,
 *  then traverses the graph as far as it can from that point
 *  in a depth-first manner. Thus, each iteration of the top-level
 *  loop traverses one connected component of the graph.
 *
 *  The callbacks and their invocation points are defined as follows:
 *	- gPre(). Called once at the start of traversal before
 *	visiting any nodes.
 *	- gPost(). Called once, after visiting nodes and crossing edges.
 *	- tPre(Object n). Called at top of top-level loop. n is the
 *	starting node for the next recursive phase, which will traverse
 *	a connected component. n has not yet been visited.
 *	- tPost(Object n). Called at the bottom of top-level loop. n was
 *	the starting point. All nodes/edges in n's connected component
 *	have been visited/crossed.
 *	- nPre(Object n). Called at beginning of visit to node n.
 *	- nPost(Object n). Called at end of visit to node n. All
 *	descendants have been visited.
 *	- ePre(Edge e, Object from). Called at beginning of crossing
 *	edge e. The node, from, indicates which direction we are
 *	going.
 *	- ePost(Edge e, Object from). Called at end of crossing edge e.
 *
 *  The DFTraversal class defines all callbacks as no-ops. Subclasses
 *  override any/all that are needed.
 */
public class DFTraversal extends AbstractTraversal implements Traversal {

    // ----------------------------------------------

    protected void _go(Iterator it){
        this.gPre();
        while(it.hasNext()){
        Object startNode = it.next();
            if( ! this.visited.contains(startNode) ){
          this.tPre(startNode);
          this._visit(startNode, null);
          this.tPost(startNode);
            }
        }
        this.gPost();
    }

    // ----------------------------------------------

    private void _visit(Object n, Object dfParent){
        this.visited.add(n);
        this.nPre(n);
        Graph g = this.getGraph();
        Iterator it = g.iterNeighbors(n);
        while(it.hasNext()){
        Object m = it.next();
        if( m != dfParent ){
            Object lbl = g.getEdgeLabel(n, m);
            this.ePre(n, m, lbl);
            if( ! this.visited.contains(m) )
            this._visit(m, n);
            this.ePost(n, m, lbl);
        }
        }
        this.nPost(n);
    }

    // ---------------------------------------------------- CALLBACKS
    // Subclasses override as needed.

    protected void gPre(){}
    protected void gPost(){}
    protected void tPre(Object n){}
    protected void tPost(Object n){}
    protected void nPre(Object n){}
    protected void nPost(Object n){}
    protected void ePre(Object from, Object to, Object lbl){}
    protected void ePost(Object from, Object to, Object lbl){}
}

// $LOG$
