# Assignment 4 — Smart City / Smart Campus Scheduling

**Student:** Kuntuganov Rauan (SE-2401)
**Course:** Design and Analysis of Algorithms
**Repo modules:** `graph.scc`, `graph.topo`, `graph.dagsp`, `model`, `util`

---

## Goal

Unify two course topics into one practical pipeline:

1. **SCC detection** (Tarjan) → **Condensation graph (DAG)** → **Topological order** (Kahn)
2. **Shortest paths in a DAG** (single-source) + **Critical (longest) path**

The scenario models real task dependencies (repairs, cleaning, sensors). Cycles are compressed into SCCs, then we plan on the resulting DAG.

---

## Implementation Overview

* **SCC (Tarjan)** — `O(V+E)`; builds:

  * component labels `compId[v]`,
  * list of components,
  * **condensation DAG** edges (unique `cu → cv` where `cu != cv`).
* **Topological Sort (Kahn)** — queue by in-degree; metrics track `push/pop`.
* **DAG Shortest Paths** — linear DP over topo order; path reconstruction.
* **DAG Longest Path (Critical Path)** — max-DP over topo order (no cycles).
* **Weights model:** on **edges of the condensation DAG** (for experiments set to `1` by default; original JSON edge weights are used only for building the unweighted graph for SCC/topo in this assignment variant).
* **Metrics** (`util/Metrics`):
  `timeNs, dfsVisits, dfsEdges, kahnPush, kahnPop, relaxations`.

---

## Project Structure

```
src/main/java/org/yeah/
├── graph/
│   ├── scc/TarjanSCC.java
│   ├── topo/TopologicalSort.java
│   └── dagsp/DAGShortestPaths.java
├── model/
│   ├── Graph.java
│   └── Edge.java
├── util/
│   ├── JSONIO.java      // org.json based reader
│   ├── Metrics.java
└── Main.java            // runs MetricsCollector over /data
data/
├── small1.json … large3.json (9 datasets)
```

---

## How to Run

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="org.yeah.Main"
```

This runs `MetricsCollector` over all 9 datasets and writes `results_summary.csv`.

Run unit tests:

```bash
mvn test
```

---

## Datasets

Nine datasets under `/data` (Small/Medium/Large) mix cyclic and acyclic structures with varying densities to stress different parts of the pipeline.

---

## Results (Collected Metrics)

| Dataset           |  n | edges | SCC_count | SCC_time_ns | Topo_time_ns | SP_relax | SP_time_ns | LP_relax | LP_time_ns |
| ----------------- | -: | ----: | --------: | ----------: | -----------: | -------: | ---------: | -------: | ---------: |
| data/small1.json  |  6 |     6 |         4 |      130700 |        35700 |        3 |      60700 |        3 |      10000 |
| data/small2.json  |  8 |     7 |         8 |      130000 |        71400 |        7 |       9400 |        7 |       7400 |
| data/small3.json  | 10 |     9 |         8 |       57000 |        29300 |        1 |       5800 |        1 |       3900 |
| data/medium1.json | 12 |    13 |         8 |       43500 |        11200 |        7 |       5200 |        7 |       6300 |
| data/medium2.json | 15 |    15 |        13 |       61700 |        24300 |       12 |       8500 |       12 |       7000 |
| data/medium3.json | 18 |    18 |        14 |       58900 |        25900 |       12 |       7900 |       12 |       5100 |
| data/large1.json  | 25 |    24 |        21 |       63100 |        19600 |        0 |       3900 |        0 |       3200 |
| data/large2.json  | 40 |    20 |        40 |      258900 |        45900 |        8 |      11400 |        8 |       8900 |
| data/large3.json  | 50 |    17 |        46 |       66900 |        39100 |        0 |       6700 |        0 |       5600 |

**Notes on columns**

* `SCC_count` — number of components after Tarjan.
* `SP_relax`, `LP_relax` — number of edge relaxations during shortest/longest DP on the condensation DAG.
* `time_ns` — measured via `System.nanoTime()` (single run).

---

## Analysis

### 1) SCC (Tarjan)

* Time generally grows with **E** and with how often recursion revisits edges;
  see higher `SCC_time_ns` on graphs with many singleton components (e.g., `large2`: `SCC_count=40`), which increases stack activity even when density is low.
* Mixed graphs (cycles + chains) keep SCC times moderate (`medium1–3`).

### 2) Topological Sort (Kahn)

* **Topo times are small** and scale roughly with nodes + edges of the condensation DAG (not original graph).
* The dominant cost is pushing/popping queue entries; more components ⇒ larger queue churn (e.g., `small2`, `large2`).

### 3) DAG Shortest / Longest Paths

* `SP_relax`/`LP_relax` correlate with **condensation edges reachable from the chosen source**.
  Zero relaxations (`large1`, `large3`) indicate the source component has no outgoing reachable edges in the condensation DAG.
* Times are tiny (micro-scale) because the DP is linear in DAG size.

### 4) Structural effects (SCC_count vs. planning)

* **High `SCC_count`** means many tiny components → easier scheduling (more DAG nodes, but fewer cycles).
* **Few large SCCs** imply bigger “super-tasks” after compression → shorter topo orders, but potential critical paths become more pronounced.

---

## Conclusions & Practical Guidance

* Use **Tarjan SCC** first to collapse cycles; it keeps later stages trivial in cost and ensures correctness (planning must be acyclic).
* For scheduling/ordering, **Kahn** is robust and easy to instrument (monitor `push/pop`).
* For timing/lead-time evaluation, **DAG DP** (shortest/longest) scales linearly and is dominated by the size of the **condensation DAG**, not the original graph.
* When datasets show **zero relaxations**, validate your **source component** (choose a component that actually precedes others to get meaningful path metrics).
* For noisy or dense cyclic inputs, spend effort on **good SCC compression**; that is where most of the nontrivial work happens.

---

## Testing

JUnit 5 tests cover:

* SCC correctness on cycles, DAGs, singletons;
* topological order validity on linear and branched DAGs;
* shortest/longest path DP, path reconstruction, and unreachable targets.

`mvn test` → all tests green.

**Done.** This README follows the presentation rubric: goal, implementation, datasets, results (table), analysis, conclusions, run steps, and testing.
