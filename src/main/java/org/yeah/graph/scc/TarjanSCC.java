package org.yeah.graph.scc;

import org.yeah.util.Metrics;

import java.util.*;

public class TarjanSCC {
    public final int n;
    public final List<List<Integer>> g;

    public int time = 0, compCount = 0;
    public int[] disc, low, compId;
    public boolean[] inStack;
    public Deque<Integer> st = new ArrayDeque<>();
    public List<List<Integer>> components = new ArrayList<>();
    public Set<Long> condensationEdges = new HashSet<>();

    private final Metrics M;

    public TarjanSCC(List<List<Integer>> g, Metrics metrics) {
        this.n = g.size();
        this.g = g;
        this.M = metrics == null ? new Metrics() : metrics;
        disc = new int[n];
        low = new int[n];
        compId = new int[n];
        inStack = new boolean[n];
        Arrays.fill(disc, -1);
        Arrays.fill(low, -1);
        Arrays.fill(compId, -1);
    }

    public void run() {
        long t0 = System.nanoTime();
        for (int v = 0; v < n; v++) {
            if (disc[v] == -1) dfs(v);
        }
        for (int u = 0; u < n; u++) {
            for (int v : g.get(u)) {
                int cu = compId[u], cv = compId[v];
                if (cu != cv) condensationEdges.add(pack(cu, cv));
            }
        }
        M.timeNs = System.nanoTime() - t0;
    }

    private void dfs(int u) {
        disc[u] = low[u] = time++;
        st.push(u);
        inStack[u] = true;
        M.dfsVisits++;

        for (int v : g.get(u)) {
            M.dfsEdges++;
            if (disc[v] == -1) {
                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (inStack[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        if (low[u] == disc[u]) {
            List<Integer> comp = new ArrayList<>();
            while (true) {
                int w = st.pop();
                inStack[w] = false;
                compId[w] = compCount;
                comp.add(w);
                if (w == u) break;
            }
            components.add(comp);
            compCount++;
        }
    }

    private static long pack(int a, int b) { return (((long) a) << 32) ^ (b & 0xffffffffL); }

    public List<List<Integer>> getComponents() { return components; }

    public int[] getCompId() { return compId; }

    public int getCompCount() { return compCount; }

    public Set<Long> getCondensationEdgesPacked() { return condensationEdges; }

    public List<int[]> getCondensationEdges() {
        List<int[]> res = new ArrayList<>();
        for (long p : condensationEdges) {
            int a = (int)(p >> 32);
            int b = (int)p;
            res.add(new int[]{a, b});
        }
        return res;
    }
}
