import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/** Missions 1 and 2. Building the maze out of the walls. */
public class Maze {
    /** Mission 1: take walls in file order and break a wall if it joins two sets. */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] chosen = new int[Math.max(0, w.cells() - 1)];
        int count = 0;
        for (int k = 0; k < w.count() && count < chosen.length; k++) {
            if (dsu.union(w.a()[k], w.b()[k])) chosen[count++] = k;
        }
        return Arrays.copyOf(chosen, count);
    }

    /** Mission 2: Kruskal's minimum spanning tree. */
    public static int[] cheapestMaze(Input.Walls w) {
        Integer[] order = new Integer[w.count()];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, Comparator.comparingInt(k -> w.cost()[k]));
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] chosen = new int[Math.max(0, w.cells() - 1)];
        int count = 0;
        for (int k : order) {
            if (dsu.union(w.a()[k], w.b()[k])) {
                chosen[count++] = k;
                if (count == chosen.length) break;
            }
        }
        return Arrays.copyOf(chosen, count);
    }

    /** Mission 2: Prim's algorithm using a priority queue of wall indexes. */
    public static long primCost(Input.Walls w) {
        int n = w.cells();
        if (n <= 1) return 0;
        int m = w.count();
        int[] degree = new int[n];
        for (int i = 0; i < m; i++) { degree[w.a()[i]]++; degree[w.b()[i]]++; }
        int[][] incident = new int[n][];
        for (int i = 0; i < n; i++) incident[i] = new int[degree[i]];
        Arrays.fill(degree, 0);
        for (int i = 0; i < m; i++) {
            incident[w.a()[i]][degree[w.a()[i]]++] = i;
            incident[w.b()[i]][degree[w.b()[i]]++] = i;
        }
        boolean[] visited = new boolean[n];
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(k -> w.cost()[k]));
        visited[0] = true;
        for (int k : incident[0]) pq.add(k);
        int visitedCount = 1;
        long total = 0;
        while (visitedCount < n && !pq.isEmpty()) {
            int k = pq.poll();
            int a = w.a()[k], b = w.b()[k];
            if (visited[a] && visited[b]) continue;
            int next = visited[a] ? b : a;
            visited[next] = true;
            visitedCount++;
            total += w.cost()[k];
            for (int edge : incident[next]) {
                int x = w.a()[edge], y = w.b()[edge];
                if (!(visited[x] && visited[y])) pq.add(edge);
            }
        }
        if (visitedCount != n) throw new IllegalArgumentException("The wall graph is disconnected");
        return total;
    }
}
