package org.yeah.model;

import java.util.*;

public class Graph {
    private final int n;
    private final List<List<Edge>> adj;

    public Graph(int n) {
        this.n = n;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    }

    public int n() { return n; }

    public List<List<Edge>> adj() { return adj; }

    public void addEdge(int u, int v, int w) {
        if (u < 0 || v < 0 || u >= n || v >= n) {
            throw new IllegalArgumentException("Vertex out of range");
        }
        adj.get(u).add(new Edge(v, w));
    }

    public List<List<Integer>> adjUnweighted() {
        List<List<Integer>> g = new ArrayList<>(n);
        for (int i = 0; i < n; i++) g.add(new ArrayList<>());
        for (int u = 0; u < n; u++) {
            for (Edge e : adj.get(u)) g.get(u).add(e.to);
        }
        return g;
    }
}
