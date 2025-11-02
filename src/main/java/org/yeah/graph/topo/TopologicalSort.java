package org.yeah.graph.topo;

import org.yeah.util.Metrics;

import java.util.*;

public class TopologicalSort {

    public static List<Integer> kahn(List<List<Integer>> dagAdj, Metrics metrics) {
        Metrics M = metrics == null ? new Metrics() : metrics;
        long t0 = System.nanoTime();

        int k = dagAdj.size();
        int[] indeg = new int[k];
        for (int u = 0; u < k; u++) {
            for (int v : dagAdj.get(u)) indeg[v]++;
        }

        Deque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < k; i++) if (indeg[i] == 0) {
            q.add(i);
            M.kahnPush++;
        }

        List<Integer> order = new ArrayList<>(k);
        while (!q.isEmpty()) {
            int u = q.removeFirst();
            M.kahnPop++;
            order.add(u);
            for (int v : dagAdj.get(u)) {
                if (--indeg[v] == 0) {
                    q.addLast(v);
                    M.kahnPush++;
                }
            }
        }

        M.timeNs = System.nanoTime() - t0;

        if (order.size() != k) {
            throw new IllegalStateException("Graph is not a DAG (cycle detected)");
        }
        return order;
    }

    public static List<List<Integer>> buildCondensationDAG(int compCount, List<int[]> edges) {
        List<List<Integer>> dag = new ArrayList<>(compCount);
        for (int i = 0; i < compCount; i++) dag.add(new ArrayList<>());
        Set<Long> seen = new HashSet<>();
        for (int[] e : edges) {
            long p = (((long)e[0]) << 32) ^ (e[1] & 0xffffffffL);
            if (seen.add(p)) dag.get(e[0]).add(e[1]);
        }
        return dag;
    }
}
