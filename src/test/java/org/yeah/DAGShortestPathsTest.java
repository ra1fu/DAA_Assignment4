package org.yeah;

import org.junit.jupiter.api.Test;
import org.yeah.graph.dagsp.DAGShortestPaths;
import org.yeah.util.Metrics;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DAGShortestPathsTest {

    @Test
    public void testShortestSimpleChain() {
        List<List<int[]>> g = new ArrayList<>();
        for (int i = 0; i < 3; i++) g.add(new ArrayList<>());
        g.get(0).add(new int[]{1,1});
        g.get(1).add(new int[]{2,2});
        g.get(0).add(new int[]{2,4});

        List<Integer> order = List.of(0,1,2);
        Metrics m = new Metrics();
        DAGShortestPaths.Result r = DAGShortestPaths.shortestPathsDAG(g, order, 0, m);

        assertEquals(0, r.dist[0]);
        assertEquals(1, r.dist[1]);
        assertEquals(3, r.dist[2]);
        assertEquals(List.of(0,1,2), r.reconstructPath(2));
        assertTrue(m.relaxations > 0);
    }

    @Test
    public void testLongestPath() {
        List<List<int[]>> g = new ArrayList<>();
        for (int i = 0; i < 3; i++) g.add(new ArrayList<>());
        g.get(0).add(new int[]{1,2});
        g.get(1).add(new int[]{2,3});
        g.get(0).add(new int[]{2,1});

        List<Integer> order = List.of(0,1,2);
        DAGShortestPaths.Result r = DAGShortestPaths.longestPathDAG(g, order, 0, new Metrics());

        assertEquals(5, r.dist[2]);
        assertEquals(List.of(0,1,2), r.reconstructPath(2));
    }

    @Test
    public void testUnreachableNode() {
        List<List<int[]>> g = new ArrayList<>();
        for (int i = 0; i < 3; i++) g.add(new ArrayList<>());
        g.get(0).add(new int[]{1,2});

        List<Integer> order = List.of(0,1,2);
        DAGShortestPaths.Result r = DAGShortestPaths.shortestPathsDAG(g, order, 0, null);

        assertEquals(DAGShortestPaths.INF, r.dist[2]);
        assertTrue(r.reconstructPath(2).isEmpty(), "Unreachable node should return empty path");
    }
}
