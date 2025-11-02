package org.yeah.graph.dagsp;

import org.yeah.util.Metrics;

import java.util.*;

public class DAGShortestPaths {

    public static class Result {
        public final int src;
        public final long[] dist;
        public final int[] parent;

        public Result(int src, long[] dist, int[] parent) {
            this.src = src;
            this.dist = dist;
            this.parent = parent;
        }

        public List<Integer> reconstructPath(int to) {
            if (dist[to] == INF) return Collections.emptyList();
            List<Integer> path = new ArrayList<>();
            int cur = to;
            while (cur != -1) {
                path.add(cur);
                cur = parent[cur];
            }
            Collections.reverse(path);
            return path;
        }
    }

    public static final long INF = (long) 4e18;

    public static Result shortestPathsDAG(List<List<int[]>> dagWeightedAdj,
                                          List<Integer> topoOrder,
                                          int src,
                                          Metrics metrics) {
        Metrics M = metrics == null ? new Metrics() : metrics;
        long t0 = System.nanoTime();

        int k = dagWeightedAdj.size();
        long[] dist = new long[k];
        int[] parent = new int[k];
        Arrays.fill(dist, INF);
        Arrays.fill(parent, -1);
        dist[src] = 0;

        for (int u : topoOrder) {
            if (dist[u] == INF) continue;
            for (int[] e : dagWeightedAdj.get(u)) {
                int v = e[0]; int w = e[1];
                long nd = dist[u] + w;
                if (nd < dist[v]) {
                    dist[v] = nd;
                    parent[v] = u;
                    M.relaxations++;
                }
            }
        }

        M.timeNs = System.nanoTime() - t0;
        return new Result(src, dist, parent);
    }

    public static Result longestPathDAG(List<List<int[]>> dagWeightedAdj,
                                        List<Integer> topoOrder,
                                        int src,
                                        Metrics metrics) {
        Metrics M = metrics == null ? new Metrics() : metrics;
        long t0 = System.nanoTime();

        int k = dagWeightedAdj.size();
        long NEG_INF = (long) -4e18;
        long[] best = new long[k];
        int[] parent = new int[k];
        Arrays.fill(best, NEG_INF);
        Arrays.fill(parent, -1);
        best[src] = 0;

        for (int u : topoOrder) {
            if (best[u] == NEG_INF) continue;
            for (int[] e : dagWeightedAdj.get(u)) {
                int v = e[0]; int w = e[1];
                long nd = best[u] + w;
                if (nd > best[v]) {
                    best[v] = nd;
                    parent[v] = u;
                    M.relaxations++;
                }
            }
        }

        M.timeNs = System.nanoTime() - t0;
        return new Result(src, best, parent);
    }

    public static List<List<int[]>> buildWeightedDAG(int k, List<int[]> edges, int defaultW) {
        List<List<int[]>> dag = new ArrayList<>(k);
        for (int i = 0; i < k; i++) dag.add(new ArrayList<>());
        for (int[] e : edges) {
            dag.get(e[0]).add(new int[]{e[1], defaultW});
        }
        return dag;
    }
}
