// $HEADER$
// $NAME$

package org.jax.mgi.shr.graphs;

import java.util.*;
import java.io.*;

/** a container of graph utilities, which are static methods.
 *  @has nothing
 *  @does nothing
 */
public class Graphs {

    /** Prints a graph to standard output.
     *  @param g a Graph
     *  @return nothing
     *  @effects writes to standard out
     */
    public static void print(Graph g){
    Graphs.print(g, System.out);
    }

    /** Prints a graph to a PrintStream.
     *  @param g a Graph
     *  @param ps a PrintStream
     *  @return nothing
     *  @effects writes to the PrintStream
     */
    public static void print(Graph g, PrintStream ps){
    (new Traversals.SimplePrinter(ps)).go(g);
    }

    /** Returns an iterator over the connected components of a graph.
     *  @param g a Graph
     *  @return an iterator. Each call to next() returns a Graph
     *  that is connected and is a subgraph of g.
     */
    public static Iterator iterConnectedComponents(Graph g){
    return (new Traversals.CCIterator(g));
    }

    /** Creates and returns the graph example used in
     *  Sedgewick's book. See: Algorithms, by
     *  Robert Sedgewick, Addison-Wesley, NY, 1983.
     *  Example is on page 374.
     *  This graph has 13 nodes, 13 edges, and 3 connected components.
     *  Here's a picture:
     *  <PRE>
     *		D--E--G     H--I
     *		| /   |
     *		F-----A        J--K
     *		     / \       | \
     *		    B   C      L--M
     *  </PRE>
     *  @return the Graph described above. Nodes are Strings.
     *  All edge labels are null.
     */
    public static Graph makeTestGraph(){

    SimpleGraph g = new SimpleGraph();

    g.addNode("A");
    g.addNode("B");
    g.addNode("C");
    g.addNode("D");
    g.addNode("E");
    g.addNode("F");
    g.addNode("G");
    g.addNode("H");
    g.addNode("I");
    g.addNode("J");
    g.addNode("K");
    g.addNode("L");
    g.addNode("M");

    g.addEdge("A", "B");
    g.addEdge("A", "C");
    g.addEdge("A", "F");
    g.addEdge("A", "G");
    g.addEdge("E", "D");
    g.addEdge("F", "D");
    g.addEdge("F", "E");
    g.addEdge("G", "E");

    g.addEdge("J", "K");
    g.addEdge("J", "L");
    g.addEdge("J", "M");
    g.addEdge("L", "M");

    g.addEdge("H", "I");

    return g;
    }

} // END class Graphs

// $LOG$
