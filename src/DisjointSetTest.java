/** Checks of Mission 0. Run: java -cp out DisjointSetTest */
public class DisjointSetTest {
    static int failed = 0;
    static void check(boolean ok, String what) {
        System.out.println((ok ? "ok    " : "FAIL  ") + what);
        if (!ok) failed++;
    }

    public static void main(String[] args) {
        DisjointSet d = new DisjointSet(6);
        check(d.count() == 6, "6 sets at the start");
        check(!d.connected(0, 1), "0 and 1 are apart at the start");
        check(d.union(0, 1), "union(0, 1) joins two sets");
        check(d.connected(0, 1), "0 and 1 are together");
        check(!d.union(1, 0), "union(1, 0) again returns false");
        check(d.count() == 5, "5 sets after one union");
        d.union(2, 3); d.union(1, 3);
        check(d.connected(0, 2), "0 and 2 are together through 1 and 3");
        check(d.count() == 3, "3 sets: {0,1,2,3} {4} {5}");
        check(d.find(4) == 4 && d.find(5) == 5, "a single cell is its own root");

        DisjointSet self = new DisjointSet(3);
        check(!self.union(2, 2), "union of an element with itself returns false");
        check(self.count() == 3, "count stays the same after self-union");

        DisjointSet many = new DisjointSet(100);
        for (int i = 0; i < 50; i++) many.union(i, i + 1);
        check(many.count() == 50, "count after many unions");

        DisjointSet chain = new DisjointSet(10000);
        for (int i = 0; i < 9999; i++) chain.union(i, i + 1);
        check(chain.connected(0, 9999), "large chain is connected");
        check(chain.count() == 1, "large chain has one set");

        System.out.println(failed == 0 ? "All checks passed." : failed + " checks failed.");
        if (failed != 0) System.exit(1);
    }
}
