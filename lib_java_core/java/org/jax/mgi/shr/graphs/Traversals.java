// $HEADER$
// $NAME$

package org.jax.mgi.shr.graphs;

import java.util.*;
import java.io.*;

/** This class is a containiner for a numer of useful
 *  graph traversals, incluing one to find connected components
 *  and a simple printer.
 */
public class Traversals {


/* --------------------------------------------------------------------- */

  /** A composition of a list of other traversals. The graph
   *  is traversed once; each member traversal's nPre, nPost,
   *  ePre, etc., is called in succession at the appropriate
   *  point.
   */
  public static class DFComposed extends DFTraversal {

    public DFComposed(){ }

    public DFComposed(DFTraversal t1, DFTraversal t2){
        this.add(t1);
        this.add(t2);
    }

    public DFComposed(Collection ts){
        this.add(ts);
    }

    public void add(DFTraversal t){
        this.members.add(t);
    }

    public void add( Collection ts ) {
        Iterator it = ts.iterator();
        while(it.hasNext()){
            this.add( (DFTraversal) it.next() );
        }
    }

    public List getMembers(){
        return new Vector(this.members);
    }

    /* ------------------------------------------ */
    protected final void gPre(){
        Iterator it = this.members.iterator();
        while( it.hasNext() )
            ((DFTraversal) it.next()).gPre();
    }
    protected final void gPost(){
        Iterator it = this.members.iterator();
        while( it.hasNext() )
            ((DFTraversal) it.next()).gPost();
    }

    protected final void tPre(Object n){
        Iterator it = this.members.iterator();
        while( it.hasNext() )
            ((DFTraversal) it.next()).tPre(n);
    }

    protected final void tPost(Object n){
        Iterator it = this.members.iterator();
        while( it.hasNext() )
            ((DFTraversal) it.next()).tPost(n);
    }

    protected final void nPre(Object n){
        Iterator it = this.members.iterator();
        while( it.hasNext() )
            ((DFTraversal) it.next()).nPre(n);
    }

    protected final void nPost(Object n){
        Iterator it = this.members.iterator();
        while( it.hasNext() )
            ((DFTraversal) it.next()).nPost(n);
    }

    protected final void ePre(Object n, Object m, Object lbl){
        Iterator it = this.members.iterator();
        while( it.hasNext() )
            ((DFTraversal) it.next()).ePre(n,m,lbl);
    }

    protected final void ePost(Object n, Object m, Object lbl){
        Iterator it = this.members.iterator();
        while( it.hasNext() )
            ((DFTraversal) it.next()).ePost(n,m,lbl);
    }

    /* ---------------------------------------- */

    private List members	= new Vector();
}

/* --------------------------------------------------------------------- */

/** A CCIterator object is an Iterator over the connected components
 *  of a graph. Each call to next() returns one connected component
 *  of the input. The connected component is itself a graph.
 *
 *  A CCIterator is a depth-first traversal that
 *  builds up the subgraph of one connected component. Each call
 *  to next() performs a traversal, and returns the component.
 *  The top-level maintains state so that each call to next()
 *  begins from a unvisited node.
 */
public static class CCIterator extends DFTraversal implements Iterator {

    CCIterator(Graph g){
        this.setGraph(g);
        this.unvisited = g.getNodes();
        this.current = null;
    }

    public void remove(){
        throw new UnsupportedOperationException(
           "Cannot remove graph component during iteration.");
    }

    public boolean hasNext(){
        return (! this.unvisited.isEmpty() );
    }

    public Object next(){
        if(this.unvisited.isEmpty())
            return null;

        /* start a new graph to hold the component */
        this.current = new SimpleGraph();

        /* extract next unvisited node */
        Iterator it = this.unvisited.iterator();
        Object n = it.next();

        /* do traversal for next component starting
         * from the unvisited node.
         */
        HashSet startNodes = new HashSet();
        startNodes.add(n);
        this.go(n);

        /* Retrieve the component and return it to
         * the caller. Set my pointer to null to avoid
         * needlessly continuing to reference the thing.
         */
        Object component= this.current;
        this.current = null;
        return component;
    }

    /* During the traversal, we just add the nodes and
     * edges we see to the current component.
     */
    protected void nPre(Object n){
        this.current.addNode(n);
        this.unvisited.remove(n);
    }
    protected void ePre(Object n, Object m, Object lbl){
        this.current.addEdge(n,m,lbl);
    }

    /* ----------------------------------------------------- */

    private Graph	graph	= null;
    private Set	unvisited = null;
    private Graph	current = null;
    private int	nVisited = 0;

} // END class CCIterator

/* --------------------------------------------------------------------- */

/** A simple graph printer. It traverses the graph and
 *  prints (toString()) each node and edge as it is encountered.
 */
public static class SimplePrinter extends DFTraversal {
    public SimplePrinter(){
        this(System.out);
    }

    public SimplePrinter(PrintStream p){
        if(p == null)
            throw new RuntimeException("PrintStream cannot be null.");
        this.ps = p;
    }

    protected void gPre(){
        this.level = 0;
    }

    protected void nPre(Object n){
       this.indent();
       this.ps.println("NODE: " + n.toString());
       this.level++;
    }

    protected void ePre(Object n, Object m, Object lbl){
       this.indent();
       this.ps.println("EDGE: (" + n.toString() + ", "
           + m.toString() + ")" );
    }

    protected void nPost(Object n){
        this.level--;
    }

    private void indent(){
      for(int i = 0; i < level; i++)
        this.ps.print(" ");
    }

    private PrintStream ps	= null;
    private int	level = 0;
}

/* --------------------------------------------------------------------- */

} // END class Traversals

// $LOG$
