// $HEADER$
// $NAME$

package org.jax.mgi.shr.graphs;

import java.io.*;
import java.util.*;

/** Abstract superclass for traversals, provides shared implemenmtation
 *  of Traversal stuff.
 */
public abstract class AbstractTraversal implements Traversal {

    public AbstractTraversal(){
    }

    public AbstractTraversal(Graph g){
        this.setGraph(g);
    }

    public AbstractTraversal(Graph g, Collection startNodes){
        this.setGraph(g);
        this.setStartNodes(startNodes);
    }

    public AbstractTraversal(Graph g, Object startNode){
        this.setGraph(g);
        this.setStartNode(startNode);
    }

    // ----------------------------------------------

    public Collection getStartNodes(){
        return this.startNodes;
    }

    public void setStartNodes(Collection sns){
        this.startNodes = sns;
    }

    public void setStartNode(Object sn){
        List sns = null;
        if( sn != null ){
            sns = new ArrayList(1);
            sns.add(sn);
        }
        this.setStartNodes(sns);
    }

    // ----------------------------------------------

    /** Returns the graph to be traversed.
    */
    public Graph getGraph(){
        return this.graph;
    }

    /** Sets the graph to be traversed.
     *  (This call does NOT set/change
     *  the starting nodes.)
     */
    public void setGraph(Graph g){
        this.graph = g;
    }

    // ----------------------------------------------

    /** Performs a depth-first traversal of graph g from
     *  the given starting nodes.
     */
    public void go(Graph g, Collection startNodes){
        this.setGraph(g);
        this.setStartNodes( startNodes );
        this.go();
    }

    // ----------------------------------------------

    public void go(Graph g, Object sn){
        this.setGraph(g);
        this.setStartNode(sn);
        this.go( );
    }

    // ----------------------------------------------

    public void go(Graph g){
        this.setGraph(g);
        this.go( );
    }

    // ----------------------------------------------

    public void go(Collection startNodes){
        this.setStartNodes( startNodes );
        this.go( );
    }

    // ----------------------------------------------

    public void go(Object n){
        this.setStartNode( n );
        this.go();
    }

    // ----------------------------------------------

    public void go(){
        if( this.graph == null )
            throw new RuntimeException("No current graph!");
        this.visited = new HashSet();
        Iterator it;
        if(this.startNodes != null)
            it = this.startNodes.iterator();
        else
            it = this.graph.iterNodes();

        this._go(it);
    }

    // ----------------------------------------------

    /** Subclasses must implement this method, which
     *  initiates the top-level traversal over the the
     *  given starting nodes.
     */
    protected abstract void _go(Iterator it);

    // ---------------------------------------------------- INST.VARS.
    protected Graph		graph = null;	// the graph to traverse
    protected Collection	startNodes = null; // where to start traversal
    protected HashSet	visited = null;	// nodes that have been visited
}


// $LOG$
