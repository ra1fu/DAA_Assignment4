package org.yeah;

import org.junit.jupiter.api.Test;
import org.yeah.graph.scc.TarjanSCC;
import org.yeah.util.Metrics;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TarjanSCCTest {

    @Test
    public void testSimpleCycle() {
        List<List<Integer>> g = List.of(
                List.of(1), List.of(2), List.of(0)
        );

        Metrics m = new Metrics();
        TarjanSCC scc = new TarjanSCC(g, m);
        scc.run();

        assertEquals(1, scc.getCompCount(), "Cycle of 3 nodes = 1 SCC");
        assertEquals(3, scc.getComponents().get(0).size());
        assertTrue(m.dfsVisits > 0 && m.dfsEdges > 0);
    }

    @Test
    public void testTwoSeparateSCCs() {
        List<List<Integer>> g = List.of(
                List.of(1), List.of(0),
                List.of(3), List.of(2)
        );

        TarjanSCC scc = new TarjanSCC(g, new Metrics());
        scc.run();

        assertEquals(2, scc.getCompCount(), "Should detect two SCCs");
        assertEquals(2, scc.getComponents().get(0).size());
    }

    @Test
    public void testCondensationEdges() {
        List<List<Integer>> g = new ArrayList<>();
        for (int i = 0; i < 6; i++) g.add(new ArrayList<>());
        g.get(0).add(1); g.get(1).add(2); g.get(2).add(0);
        g.get(2).add(3);
        g.get(3).add(4); g.get(4).add(3);
        g.get(4).add(5);

        TarjanSCC scc = new TarjanSCC(g, new Metrics());
        scc.run();

        int compCount = scc.getCompCount();
        assertEquals(3, compCount);
        List<int[]> edges = scc.getCondensationEdges();
        assertFalse(edges.isEmpty(), "Condensation DAG must have edges");
    }
}
