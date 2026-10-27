import java.util.*;

/** Missions 4 and 5: strongly connected components and routes in the zone DAG. */
public class Zones {
    /** Kosaraju's algorithm, implemented iteratively to avoid stack overflow. */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] from = Graphs.slidesFrom(s);
        int[][] into = Graphs.slidesInto(s);
        boolean[] seen = new boolean[n];
        int[] finish = new int[n];
        int finishCount = 0;
        int[] stackCell = new int[n];
        int[] stackNext = new int[n];

        // First pass on the original graph: record DFS finishing order.
        for (int start = 0; start < n; start++) {
            if (seen[start]) continue;
            int top = 0;
            stackCell[0] = start; stackNext[0] = 0; seen[start] = true;
            while (top >= 0) {
                int u = stackCell[top];
                if (stackNext[top] < from[u].length) {
                    int edge = from[u][stackNext[top]++];
                    int v = s.to()[edge];
                    if (!seen[v]) {
                        seen[v] = true;
                        stackCell[++top] = v;
                        stackNext[top] = 0;
                    }
                } else {
                    finish[finishCount++] = u;
                    top--;
                }
            }
        }

        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);
        int zone = 0;
        // Second pass on the reversed graph, in reverse finishing order.
        for (int i = finishCount - 1; i >= 0; i--) {
            int start = finish[i];
            if (zoneOf[start] != -1) continue;
            int top = 0;
            stackCell[0] = start; zoneOf[start] = zone;
            while (top >= 0) {
                int u = stackCell[top--];
                for (int edge : into[u]) {
                    int v = s.from()[edge];
                    if (zoneOf[v] == -1) {
                        zoneOf[v] = zone;
                        stackCell[++top] = v;
                    }
                }
            }
            zone++;
        }
        return zoneOf;
    }

    /** Returns [longest number of zones in a chain, cheapest travel time from start zone to exit zone]. */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        if (zoneCount == 0) return new long[]{0, 0};
        @SuppressWarnings("unchecked")
        Map<Integer, Long>[] outgoing = new Map[zoneCount];
        for (int z = 0; z < zoneCount; z++) outgoing[z] = new HashMap<>();

        for (int i = 0; i < s.count(); i++) {
            int a = zoneOf[s.from()[i]], b = zoneOf[s.to()[i]];
            if (a == b) continue;
            long cost = s.time()[i];
            outgoing[a].merge(b, cost, Math::min);
        }

        // Kosaraju numbers components in topological order: every edge goes from low to high.
        long[] stages = new long[zoneCount];
        Arrays.fill(stages, 1);
        long[] distance = new long[zoneCount];
        Arrays.fill(distance, Long.MAX_VALUE);
        distance[zoneOf[0]] = 0;
        long longest = 1;
        for (int z = 0; z < zoneCount; z++) {
            longest = Math.max(longest, stages[z]);
            for (Map.Entry<Integer, Long> edge : outgoing[z].entrySet()) {
                int to = edge.getKey();
                stages[to] = Math.max(stages[to], stages[z] + 1);
                if (distance[z] != Long.MAX_VALUE) {
                    long candidate = distance[z] + edge.getValue();
                    if (candidate < distance[to]) distance[to] = candidate;
                }
            }
        }
        int exitZone = zoneOf[s.n() - 1];
        long routeCost = distance[exitZone] == Long.MAX_VALUE ? 0 : distance[exitZone];
        return new long[]{longest, routeCost};
    }
}
