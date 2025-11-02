package org.yeah.util;

public class Metrics {
    public long timeNs;
    public long dfsVisits;
    public long dfsEdges;
    public long kahnPush;
    public long kahnPop;
    public long relaxations;

    public void reset() {
        timeNs = dfsVisits = dfsEdges = kahnPush = kahnPop = relaxations = 0L;
    }
}
