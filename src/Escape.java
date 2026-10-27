import java.util.Arrays;

/** Mission 3. Iterative depth-first search through the maze built in Mission 1. */
public class Escape {
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells();
        int[][] graph = Graphs.open(w, broken);
        int deadEnds = 0;
        for (int[] neighbours : graph) if (neighbours.length == 1) deadEnds++;
        if (n == 0) return new Result(new int[0], 0, deadEnds);

        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        boolean[] seen = new boolean[n];
        int[] stackCell = new int[n];
        int[] stackNext = new int[n];
        int top = 0, visited = 1;
        stackCell[0] = 0; stackNext[0] = 0; seen[0] = true;
        int exit = n - 1;
        while (top >= 0 && stackCell[top] != exit) {
            int u = stackCell[top];
            int next = nextInOrder(u, stackNext[top], w.cols(), graph[u]);
            if (next == -1) { top--; continue; }
            stackNext[top] = nextDirectionAfter(u, next, w.cols());
            if (!seen[next]) {
                seen[next] = true;
                visited++;
                parent[next] = u;
                top++;
                stackCell[top] = next;
                stackNext[top] = 0;
            }
        }
        if (!seen[exit]) return new Result(new int[0], visited, deadEnds);
        int length = 1;
        for (int x = exit; x != 0; x = parent[x]) length++;
        int[] path = new int[length];
        int x = exit;
        for (int i = length - 1; i >= 0; i--) { path[i] = x; if (i > 0) x = parent[x]; }
        return new Result(path, visited, deadEnds);
    }

    // stackNext stores the direction index to try next: up, right, down, left.
    private static int nextInOrder(int u, int startDirection, int cols, int[] open) {
        for (int dir = startDirection; dir < 4; dir++) {
            int v = switch (dir) { case 0 -> u - cols; case 1 -> u + 1; case 2 -> u + cols; default -> u - 1; };
            if (v < 0 || (dir == 1 && u % cols == cols - 1) || (dir == 3 && u % cols == 0)) continue;
            for (int candidate : open) if (candidate == v) return v;
        }
        return -1;
    }

    private static int nextDirectionAfter(int u, int v, int cols) {
        if (v == u - cols) return 1;
        if (v == u + 1 && u % cols != cols - 1) return 2;
        if (v == u + cols) return 3;
        return 4;
    }
}
