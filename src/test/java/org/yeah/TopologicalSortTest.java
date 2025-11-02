package org.yeah;

import org.junit.jupiter.api.Test;
import org.yeah.graph.topo.TopologicalSort;
import org.yeah.util.Metrics;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TopologicalSortTest {

    @Test
    public void testSimpleDAGOrder() {
        // DAG: 0->1->2->3
        List<List<Integer>> dag = List.of(
                List.of(1),
                List.of(
                        2),
                List.of(3),
                List.of()
        );

        Metrics m = new Metrics();
        List<Integer> order = TopologicalSort.kahn(dag, m);

        assertEquals(List.of(0,1,2,3), order, "Linear DAG should have natural order");
        assertEquals(4, order.size());
        assertTrue(m.kahnPush > 0 && m.kahnPop > 0);
    }

    @Test
    public void testBranchingDAG() {
        List<List<Integer>> dag = new ArrayList<>();
        for (int i = 0; i < 4; i++) dag.add(new ArrayList<>());
        dag.get(0).addAll(List.of(1,2));
        dag.get(1).add(3);
        dag.get(2).add(3);

        List<Integer> order = TopologicalSort.kahn(dag, null);

        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(0) < order.indexOf(2));
        assertTrue(order.indexOf(1) < order.indexOf(3));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }

    @Test
    public void testCycleThrows() {
        List<List<Integer>> bad = List.of(
                List.of(1), List.of(2), List.of(0)
        );
        assertThrows(IllegalStateException.class, () -> TopologicalSort.kahn(bad, null));
    }
}
