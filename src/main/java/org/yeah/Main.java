package org.yeah;

import org.yeah.graph.scc.TarjanSCC;
import org.yeah.graph.topo.TopologicalSort;
import org.yeah.graph.dagsp.DAGShortestPaths;
import org.yeah.model.Graph;
import org.yeah.util.JSONIO;
import org.yeah.util.Metrics;

import java.util.*;


public class Main {
    public static void main(String[] args) {
        try {
            String path = "data/small1.json";
            Graph g = JSONIO.readGraph(path);
            System.out.println("Loaded graph from " + path + " with " + g.n() + " vertices");

            List<List<Integer>> adj = g.adjUnweighted();

            Metrics sccM = new Metrics();
            TarjanSCC scc = new TarjanSCC(adj, sccM);
            scc.run();
            System.out.println("\n--- SCC ---");
            System.out.println("Count: " + scc.getCompCount());
            System.out.println("Components: " + scc.getComponents());
            System.out.println("Metrics: visits=" + sccM.dfsVisits +
                    " edges=" + sccM.dfsEdges +
                    " time(ns)=" + sccM.timeNs);

            int compCount = scc.getCompCount();
            List<int[]> cEdges = scc.getCondensationEdges();
            List<List<Integer>> dag = TopologicalSort.buildCondensationDAG(compCount, cEdges);

            Metrics topoM = new Metrics();
            List<Integer> order = TopologicalSort.kahn(dag, topoM);
            System.out.println("\n--- Topological Sort ---");
            System.out.println("Order: " + order);
            System.out.println("Metrics: push=" + topoM.kahnPush +
                    " pop=" + topoM.kahnPop +
                    " time(ns)=" + topoM.timeNs);

            List<List<int[]>> dagWeighted = DAGShortestPaths.buildWeightedDAG(compCount, cEdges, 1);

            int srcComp = scc.getCompId()[0];
            Metrics spM = new Metrics();
            DAGShortestPaths.Result sp = DAGShortestPaths.shortestPathsDAG(dagWeighted, order, srcComp, spM);
            System.out.println("\n--- Shortest Paths in DAG ---");
            System.out.println("Source component: " + srcComp);
            System.out.println("Distances: " + Arrays.toString(sp.dist));
            System.out.println("Metrics: relax=" + spM.relaxations +
                    " time(ns)=" + spM.timeNs);

            Metrics lpM = new Metrics();
            DAGShortestPaths.Result lp = DAGShortestPaths.longestPathDAG(dagWeighted, order, srcComp, lpM);
            long bestVal = Long.MIN_VALUE;
            int bestTo = -1;
            for (int v = 0; v < compCount; v++) {
                if (lp.dist[v] > bestVal) {
                    bestVal = lp.dist[v];
                    bestTo = v;
                }
            }
            System.out.println("\n--- Longest (Critical) Path ---");
            System.out.println("Max value = " + bestVal);
            System.out.println("Path: " + lp.reconstructPath(bestTo));
            System.out.println("Metrics: relax=" + lpM.relaxations +
                    " time(ns)=" + lpM.timeNs);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
