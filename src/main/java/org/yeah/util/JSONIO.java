package org.yeah.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.yeah.model.Graph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONIO {

    public static Graph readGraph(String path) throws IOException {
        String content = Files.readString(Paths.get(path));
        JSONObject obj = new JSONObject(content);

        int n = obj.getInt("n");
        Graph g = new Graph(n);

        JSONArray edges = obj.getJSONArray("edges");
        for (int i = 0; i < edges.length(); i++) {
            JSONObject e = edges.getJSONObject(i);
            int from = e.getInt("from");
            int to = e.getInt("to");
            int w = e.getInt("weight");
            g.addEdge(from, to, w);
        }

        return g;
    }
}
