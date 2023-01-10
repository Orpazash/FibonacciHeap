package tests;
import fibHeap.FibonacciHeap;

import static org.junit.jupiter.api.Assertions.assertSame;
        import static org.junit.jupiter.api.Assertions.assertEquals;
        import static org.junit.jupiter.api.Assertions.assertFalse;
        import static org.junit.jupiter.api.Assertions.assertNotNull;
        import static org.junit.jupiter.api.Assertions.assertNotSame;
        import static org.junit.jupiter.api.Assertions.assertNull;
        import static org.junit.jupiter.api.Assertions.assertTrue;

        import java.io.File;
        import java.io.IOException;
        import java.io.PrintStream;
        import java.nio.charset.StandardCharsets;
        import java.nio.file.Files;
        import java.nio.file.Paths;
        import java.util.*;
        import java.util.function.BiConsumer;
        import java.util.function.Function;
        import java.util.function.Supplier;
        import java.util.stream.IntStream;


        import org.junit.jupiter.api.AfterEach;
        import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.Disabled;
        import org.junit.jupiter.api.Order;
        import org.junit.jupiter.api.Tag;
        import org.junit.jupiter.api.Test;
        import org.junit.jupiter.api.TestInfo;
        import org.junit.jupiter.api.TestMethodOrder;
        import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

//FibonacciHeap Tester

        import java.util.ArrayList;
        import java.util.TreeSet;
        import java.util.Map.Entry;

class Heap {
    private TreeSet<Integer> set;

    Heap() {
        this.set = new TreeSet<>();
    }

    public int size() {
        return this.set.size();
    }

    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    public void insert(int v) {
        this.set.add(v);
    }

    public int deleteMin() {
        int min = this.set.first();
        this.set.remove(this.set.first());
        return min;
    }

    public int findMin() {
        if (this.isEmpty())
            return -1;
        return this.set.first();
    }

    public void delete(int i) {
        this.set.remove(i);

    }
}

class LabeledPair extends Pair<Integer, String> {
    public LabeledPair(int k, String label) {
        super(k, label);
    }

    public int k() {
        return this.first;
    }

    public String label() {
        return this.second;
    }
}

class Pair<T, S> {
    public final T first;
    public final S second;

    public Pair(T first, S second) {
        this.first = first;
        this.second = second;
    }
}

class HeapPrinter {
    static final String NULL = "(null)";
    final PrintStream stream;

    public HeapPrinter(PrintStream stream) {
        this.stream = stream;
    }

    void printIndentPrefix(ArrayList<Boolean> hasNexts) {
        int size = hasNexts.size();
        for (int i = 0; i < size - 1; ++i) {
            this.stream.format("%c   ", hasNexts.get(i).booleanValue() ? '│' : ' ');
        }
    }

    void printIndent(FibonacciHeap.HeapNode heapNode, ArrayList<Boolean> hasNexts) {
        int size = hasNexts.size();
        printIndentPrefix(hasNexts);

        this.stream.format("%c── %s\n",
                hasNexts.get(size - 1).booleanValue() ? '├' : '╰',
                heapNode == null ? NULL : String.valueOf(heapNode.getKey()));
    }

    static String repeatString(String s, int count) {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < count; i++) {
            r.append(s);
        }
        return r.toString();
    }

    void printIndentVerbose(FibonacciHeap.HeapNode heapNode, ArrayList<Boolean> hasNexts) {
        int size = hasNexts.size();
        if (heapNode == null) {
            printIndentPrefix(hasNexts);
            this.stream.format("%c── %s\n", hasNexts.get(size - 1).booleanValue() ? '├' : '╰', NULL);
            return;
        }

        Function<Supplier<FibonacciHeap.HeapNode>, String> keyify = f -> {
            FibonacciHeap.HeapNode node = f.get();
            return node == null ? NULL : String.valueOf(node.getKey());
        };
        String title = String.format(" Key: %d ", heapNode.getKey());
        List<String> content = Arrays.asList(
                String.format(" Rank: %d ", heapNode.rank),
                String.format(" Marked: %b ", heapNode.mark),
                String.format(" Parent: %s ", heapNode.parent),
                String.format(" Next: %s ", heapNode.next),
                String.format(" Prev: %s ", heapNode.prev),
                String.format(" Child: %s", heapNode.child));

        /* Print details in box */
        int length = Math.max(
                title.length(),
                content.stream().map(String::length).max(Integer::compareTo).get());
        String line = repeatString("─", length);
        String padded = String.format("%%-%ds", length);
        boolean hasNext = hasNexts.get(size - 1);

        // print header row
        printIndentPrefix(hasNexts);
        this.stream.format("%c── ╭%s╮\n", hasNext ? '├' : '╰', line);

        // print title row
        printIndentPrefix(hasNexts);
        this.stream.format("%c   │" + padded + "│\n", hasNext ? '│' : ' ', title);

        // print separator
        printIndentPrefix(hasNexts);
        this.stream.format("%c   ├%s┤\n", hasNext ? '│' : ' ', line);

        // print content
        for (String data : content) {
            printIndentPrefix(hasNexts);
            this.stream.format("%c   │" + padded + "│\n", hasNext ? '│' : ' ', data);
        }

        // print footer
        printIndentPrefix(hasNexts);
        this.stream.format("%c   ╰%s╯\n", hasNext ? '│' : ' ', line);
    }

    void printHeapNode(FibonacciHeap.HeapNode heapNode, boolean verbose) {
        BiConsumer<FibonacciHeap.HeapNode, ArrayList<Boolean>> function =
                verbose ?  this::printIndentVerbose : this::printIndent;

        Stack<Pair<FibonacciHeap.HeapNode, Integer>> stack = new Stack<>();
        Set<FibonacciHeap.HeapNode> visited = new HashSet<>();
        visited.add(null);

        ArrayList<Boolean> nexts = new ArrayList<>();

        nexts.add(false);
        int depth = 1;
        while (!visited.contains(heapNode) || !stack.empty()) {
            if (visited.contains(heapNode)) {
                Pair<FibonacciHeap.HeapNode, Integer> pair = stack.pop();
                heapNode = pair.first;
                depth = pair.second;
                while (nexts.size() > depth) {
                    nexts.remove(nexts.size() - 1);
                }
                continue;
            }

            visited.add(heapNode);
            nexts.set(nexts.size() - 1, !visited.contains(heapNode.next));
            stack.push(new Pair<>(heapNode.next, depth));

            function.accept(heapNode, nexts);

            heapNode = heapNode.child;
            if (heapNode != null) {
                nexts.add(false);
            }
            depth++;
        }
    }

    public void print(FibonacciHeap heap, boolean verbose) {
        if (heap == null) {
            this.stream.print(NULL + "\n");
            return;
        } else if (heap.isEmpty()) {
            this.stream.print("(empty)\n");
            return;
        }

        this.stream.print("╮\n");
        ArrayList<Boolean> list = new ArrayList<>();
        list.add(false);
        printHeapNode(heap.firstRoot, verbose);
    }
}

@TestMethodOrder(OrderAnnotation.class)
public class HeapTester1 {
    static HeapPrinter heapPrinter = new HeapPrinter(System.out);

    FibonacciHeap heap = new FibonacciHeap();
    Heap heapModel;
    boolean uniqueValues = false;


    static void print(FibonacciHeap heap) {
        boolean verbose = true;
        heapPrinter.print(heap, verbose);
    }

    int assertValidHeapRoots(FibonacciHeap heap, boolean checkSingularMin) {
        int numberOfTrees = 0;
        Map<Integer, Integer> actualRanks = new HashMap<>();
        FibonacciHeap.HeapNode node = heap.firstRoot;
        FibonacciHeap.HeapNode actualMin = node;
        int[] ranks = heap.countersRep();
        FibonacciHeap.HeapNode min = heap.findMin();
        int potential = heap.potential();

        /* Check roots */
        do {
            numberOfTrees++;
            assertNull(node.parent);
            assertFalse(node.mark);
            if (node.getKey() < actualMin.getKey()) {
                actualMin = node;
            }

            if (node.next != null) {
                assertSame(node, node.next.prev);
            }
            if (node.prev != null) {
                assertSame(node, node.prev.next);
            }

            actualRanks.merge(node.rank, 1, Integer::sum); // increase value by 1 or put 1 if absent

            node = node.next;
        } while (node != null && node != heap.firstRoot);

        for (int i = 0; i < ranks.length; ++i) {
            assertEquals(
                    ranks[i], actualRanks.getOrDefault(i, 0),
                    String.format(
                            "Expected %d roots of rank %d but found %d",
                            ranks[i], i, actualRanks.getOrDefault(i, 0)));
        }

        String details = min.getKey() < actualMin.getKey() ? "findMin() node is NOT a sibling of getFirst() node" : "";
        if (checkSingularMin) {
            assertSame(
                    min, actualMin,
                    String.format(
                            "received key %d from findMin but found min root key %d in heap. %s",
                            min.getKey(), actualMin.getKey(), details));
        } else {
            assertEquals(
                    min.getKey(), actualMin.getKey(),
                    String.format(
                            "received key %d from findMin but found min root key %d in heap. %s",
                            min.getKey(), actualMin.getKey(), details));
        }


        assertTrue(potential >= numberOfTrees);
        assertTrue((potential - numberOfTrees) % 2 == 0);

        return numberOfTrees;
    }


    void assertValidHeapNodeChildren(FibonacciHeap.HeapNode node) {
        // Check its relations to its children
        if (node.child == null) {
            assertEquals(0, node.rank);
            return;
        }

        int childrenCount = 0;
        FibonacciHeap.HeapNode currentChild = node.child;
        assertNotSame(node, currentChild);

        do {
            childrenCount++;
            assertSame(node, currentChild.parent);
            // Check heap property
            if (this.uniqueValues) {
                assertTrue(currentChild.getKey() > node.getKey());
            } else {
                assertTrue(currentChild.getKey() >= node.getKey());
            }
            currentChild = currentChild.next;
        } while (currentChild != null && currentChild != node.child);

        if (childrenCount != node.rank) {
            assertEquals(
                    childrenCount, node.rank,
                    String.format(
                            "Node with key %d has rank %d but only %d %s",
                            node.getKey(), node.rank, childrenCount,
                            childrenCount == 1 ? "child" : "children"));
        }
    }


    int assertValidHeapNodes(FibonacciHeap heap) {
        /* Check all nodes */
        FibonacciHeap.HeapNode node = heap.firstRoot;
        Stack<FibonacciHeap.HeapNode> stack = new Stack<>();
        Set<FibonacciHeap.HeapNode> visited = new HashSet<>();
        int actualSize = 0;
        int numberOfMarked = 0;

        visited.add(null);
        stack.add(node);

        /* Traverse the heap using 'pre-order' DFS */
        while (!visited.contains(node) || !stack.empty()) {
            if (visited.contains(node)) {
                node = stack.pop();
                continue;
            }
            visited.add(node);

            // Check current node
            actualSize++;
            stack.push(node.next);
            numberOfMarked += node.mark ? 1 : 0;

            assertValidHeapNodeChildren(node);

            node = node.child;
        }

        assertEquals(heap.size(), actualSize);
        assertTrue(heap.potential() >= numberOfMarked * 2);

        return numberOfMarked;
    }

    void assertValidHeap(FibonacciHeap heap) {
        int size = heap.size();

        FibonacciHeap.HeapNode node = heap.firstRoot;
        FibonacciHeap.HeapNode min = heap.findMin();

        /* Handle empty heap */
        assertTrue(heap.isEmpty() == (size == 0));
        if (heap.isEmpty()) {
            assertNull(min);
            assertNull(node);
            assertEquals(0, heap.countersRep().length);
            assertEquals(0, heap.potential());
            return;
        }

        assertNotNull(node);
        assertNotNull(min);
        assertNull(min.parent);

        int numberOfTrees = assertValidHeapRoots(heap, this.uniqueValues);
        int numberOfMarked = assertValidHeapNodes(heap);

        assertEquals(numberOfTrees + numberOfMarked * 2, heap.potential());
    }

    Map<Integer, FibonacciHeap.HeapNode> testInsertion(FibonacciHeap heap, Iterable<Integer> keys) {
        Map<Integer, FibonacciHeap.HeapNode> nodes = new HashMap<>();
        FibonacciHeap.HeapNode minNode = heap.findMin();
        int startPotential = heap.potential();
        int startSize = heap.size();
        int length = 0;

        for (int key : keys) {
            length++;
            FibonacciHeap.HeapNode current = heap.insert(key);
            assertValidHeap(heap);
            minNode = minNode == null || key < minNode.getKey() ? current : minNode;
            nodes.put(key, current);
        }

        if (this.uniqueValues) {
            assertSame(minNode, heap.findMin());
        } else {
            assertEquals(minNode.getKey(), heap.findMin().getKey());
        }

        assertEquals(length, heap.potential() - startPotential);
        assertEquals(length, heap.size() - startSize);

        return nodes;
    }

    static Iterable<Integer> toArray(int[] array) {
        return () -> Arrays.stream(array).iterator();
    }

    Map<Integer, FibonacciHeap.HeapNode> testInsertion(FibonacciHeap heap, int... keys) {
        return this.testInsertion(heap, toArray(keys));
    }

    Map<Integer, FibonacciHeap.HeapNode> testInsertionReverse(FibonacciHeap heap, int lower, int upper) {
        return this.testInsertion(heap, IntStream.range(lower, upper + 1).map(i -> upper - i + lower)::iterator);
    }

    Map<Integer, FibonacciHeap.HeapNode> testInsertionReverse(FibonacciHeap heap, int lower) {
        return this.testInsertionReverse(heap, lower, lower + 999);
    }

    void testDeletion(FibonacciHeap heap, FibonacciHeap.HeapNode... nodes) {
        int size = heap.size();

        for (FibonacciHeap.HeapNode node : nodes) {
            heap.delete(node);
            size--;
            assertValidHeap(heap);
            assertEquals(size, heap.size());
        }
    }

    void testDeletion(FibonacciHeap heap, List<FibonacciHeap.HeapNode> nodes) {
        this.testDeletion(heap, nodes.toArray(new FibonacciHeap.HeapNode[nodes.size()]));
    }


    @BeforeEach
    void beforeEachTest(TestInfo testInfo) {
        this.heap = new FibonacciHeap();
        this.heapModel = new Heap();
        this.uniqueValues = !testInfo.getTags().contains("DuplicateValues");
    }

    @Tag("NoCompare")
    @Test
    @Order(0)
    public void testNodeSanity() {
        FibonacciHeap.HeapNode node = new FibonacciHeap.HeapNode(5);
        assertEquals(5, node.getKey());
    }

    @Test
    @Order(2)
    public void testConstructorSanity() {
        assertValidHeap(heap);
        assertTrue(heap.isEmpty());
    }

    @Test
    @Order(2)
    public void testInsertDeleteSanity() {
        // case 2
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap, 2);
        testDeletion(heap, nodes.get(2));
        assertTrue(heap.isEmpty());
    }

    @Test
    @Order(2)
    public void testkMinSanity() {
        // case 14
        testInsertion(heap, 1);
        int[] keys = FibonacciHeap.kMin(heap, 1);
        assertEquals(1, keys.length);
        assertEquals(1, keys[0]);
        assertEquals(1, heap.size());
        assertEquals(1, heap.findMin().getKey());
        assertSame(heap.findMin(), heap.firstRoot);
        assertEquals(1, heap.potential());
    }

    @Test
    @Order(2)
    public void testCountersRepSanity() {
        // case 12
        testInsertion(heap, 1);
        int[] arr = heap.countersRep();
        assertValidHeap(heap);
        assertEquals(1, heap.size());
        assertEquals(1, heap.findMin().getKey());
        assertTrue(Arrays.equals(new int[] { 1 }, arr));
    }

    @Test
    @Order(6)
    public void testInsertionDeletion1() {
        // case 1
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(
                heap, 2, 1, 3, 7, 4, 8, 6, 5, 9, 10, 11);
        heap.deleteMin();
        assertValidHeap(heap);
        testDeletion(heap, nodes.get(9));
    }

    @Test
    @Order(3)
    public void testInsertionDeletion2() {
        // case 3
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(
                heap, 2, 1, 3);
        testDeletion(heap, nodes.get(2));
    }

    @Test
    @Order(4)
    public void testInsertionDeletion3() {
        // case 4
        testInsertion(heap, 20, 8, 3, 100, 15, 18, 1);
        heap.deleteMin();
        assertValidHeap(heap);
        assertEquals(3, heap.findMin().getKey());
    }

    @Test
    @Order(7)
    public void testInsertionDeletion4() {
        // case 5
        testInsertion(heap, 7, 2, 1, 18, 15, 100, 3, 8, 20);
        heap.deleteMin();
        assertValidHeap(heap);
        assertEquals(2, heap.findMin().getKey());
        testInsertion(heap, 500);
    }

    @Test
    @Order(42)
    public void testCut() {
        // case 8
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(
                heap, 2, 1, 3, 7, 4, 8, 6, 5, 9, 10, 11);
        heap.deleteMin();
        assertValidHeap(heap);
        testDeletion(heap, nodes.get(5));
    }

    @Test
    @Order(7)
    public void testCutDirectIndirectChild() {
        // case 13
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(
                heap, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        heap.deleteMin();
        assertValidHeap(heap);
        assertSame(nodes.get(1), heap.findMin());
        heap.decreaseKey(nodes.get(5), 9);
        assertValidHeap(heap);
        heap.decreaseKey(nodes.get(6), 20);
        assertValidHeap(heap);
    }

    @Test
    @Order(61)
    public void testCascadingCuts() {
        // case 9
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(
                heap, 2, 1, 3, 7, 4, 8, 6, 5, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        assertEquals(17, heap.potential());
        heap.deleteMin();
        assertValidHeap(heap);
        assertEquals(1, heap.potential());
        heap.decreaseKey(nodes.get(16), 17);
        assertValidHeap(heap);
        assertEquals(4, heap.potential());
        heap.decreaseKey(nodes.get(12), 14);
        assertValidHeap(heap);
        assertEquals(7, heap.potential());
        testDeletion(heap, nodes.get(15));
        assertEquals(4, heap.potential());
    }

    @Test
    @Order(1)
    public void testkMinEmpty() {
        int[] keys = FibonacciHeap.kMin(heap, 0);
        assertEquals(0, keys.length);
        assertTrue(heap.isEmpty());
        assertValidHeap(heap);
    }

    @Test
    @Order(7)
    public void testkMinBinomial() {
        // case 6
        testInsertion(heap, 7, 2, 1, 18, 15, 100, 3, 8, 20);
        heap.deleteMin();
        assertValidHeap(heap);
        int[] arr = FibonacciHeap.kMin(heap, 8);
        assertValidHeap(heap);
        assertEquals(8, heap.size());
        assertTrue(Arrays.equals(new int[] { 2, 3, 7, 8, 15, 18, 20, 100 }, arr));
    }

    @Test
    @Order(3)
    public void testkMinSingle() {
        // case 7
        testInsertion(heap, 7, 6);
        heap.deleteMin();
        assertValidHeap(heap);
        int[] arr = FibonacciHeap.kMin(heap, 1);
        assertTrue(Arrays.equals(new int[] { 7 }, arr));
    }

    Map<Integer, FibonacciHeap.HeapNode> addKeys(int start) {
        Map<Integer, FibonacciHeap.HeapNode> nodes = this.testInsertion(heap, IntStream.rangeClosed(start, start + 999)::iterator);
        for (int i = 0; i < 1000; i++) {//@@@@@@@ i<1000 @@@@@
            heapModel.insert(start + i);
        }
        return nodes;
    }

    Map<Integer, FibonacciHeap.HeapNode> addKeysReverse(int start) {
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertionReverse(heap, start);
        for (int i = 999; i >= 0; i--) {
            heapModel.insert(start + i);
        }
        return nodes;
    }

    @Test
    @Order(2)
    void testInsertionSanity2() {
        // test0
        ArrayList<Integer> numbers = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            numbers.add(i);
        }

        Collections.shuffle(numbers);

        testInsertion(heap, numbers);

        for (int i = 0; i < 5; i++) {
            assertEquals(i, heap.findMin().getKey());
            heap.deleteMin();
            assertValidHeap(heap);
        }
    }

    @Test
    @Order(525)
    void testInOrderInsert() {
        // test1
        addKeys(0);
        while (!heapModel.isEmpty()) {
            assertEquals(heapModel.findMin(),  heap.findMin().getKey());
            assertEquals(heapModel.size(), heap.size());
            heapModel.deleteMin();
            heap.deleteMin();
            assertValidHeap(heap);
        }

        assertTrue(heap.isEmpty());
    }

    @Test
    @Order(199)
    void testReverseOrderInsert() {
        // test2
        addKeysReverse(0);
        while (!heapModel.isEmpty()) {
            assertEquals(heapModel.findMin(),  heap.findMin().getKey());
            assertEquals(heapModel.size(), heap.size());
            heapModel.deleteMin();
            heap.deleteMin();
            assertValidHeap(heap);
        }

        assertTrue(heap.isEmpty());
    }


    @Test
    @Order(1300)
    void testMixedOrderInsert() {
        // test3
        addKeys(0);
        addKeysReverse(4000);
        addKeys(2000);
        while (!heapModel.isEmpty()) {
            assertEquals(heapModel.findMin(),  heap.findMin().getKey());
            assertEquals(heapModel.size(), heap.size());
            heapModel.deleteMin();
            heap.deleteMin();
            assertValidHeap(heap);
        }

        assertTrue(heap.isEmpty());
    }

    @Test
    @Order(3900)
    void testMixedOrderTwoStepsInsert() {
        // test4
        addKeys(0);
        addKeysReverse(4000);
        addKeys(2000);

        for (int i = 0; i < 1000; i++) {
            assertEquals(heapModel.findMin(),  heap.findMin().getKey());
            assertEquals(heapModel.size(), heap.size());
            heapModel.deleteMin();
            heap.deleteMin();
        }

        addKeys(6000);
        addKeysReverse(8000);
        addKeys(10000);

        while (!heapModel.isEmpty()) {
            assertEquals(heapModel.findMin(),  heap.findMin().getKey());
            heapModel.deleteMin();
            heap.deleteMin();
            assertValidHeap(heap);
        }

        assertTrue(heap.isEmpty());
    }

    //@Disabled // Same value insertion is not supported in our version
    @Tag("DuplicateValues")
    @Test
    @Order(1200)
    void testSameValueInsert() {
        // test5
        addKeys(0);
        addKeys(0);
        addKeys(0);

        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(i,  heap.findMin().getKey());
                heap.deleteMin();
                assertValidHeap(heap);
            }
        }

        assertTrue(heap.isEmpty());
    }


    //@Disabled // Same value insertion is not supported in our version
    @Tag("DuplicateValues")
    @Test
    @Order(9000)
    void testSameValueMixedOrderInsert() {
        // test6
        addKeysReverse(1000);
        addKeysReverse(1000);
        addKeys(0);
        addKeys(0);
        addKeys(1000);
        addKeys(1000);
        addKeysReverse(0);
        addKeysReverse(0);

        for (int i = 0; i < 2000; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(i,  heap.findMin().getKey());
                heap.deleteMin();
                assertValidHeap(heap);
            }
        }

        assertTrue(heap.isEmpty());
    }

    @Test
    @Order(1600)
    void testMixedOrderInsertDelete() {
        // test7
        addKeys(1000);
        addKeysReverse(3000);

        Map<Integer, FibonacciHeap.HeapNode> nodes = addKeys(2000);

        for (int i = 2000; i < 2500; i++) {
            assertEquals(heapModel.findMin(),  heap.findMin().getKey());
            assertEquals(heapModel.size(), heap.size());
            heapModel.delete(i);
            heap.delete(nodes.get(i));
            assertValidHeap(heap);
        }

        while (!heapModel.isEmpty()) {
            assertEquals(heapModel.findMin(),  heap.findMin().getKey());
            assertEquals(heapModel.size(), heap.size());
            heapModel.deleteMin();
            heap.deleteMin();
            assertValidHeap(heap);
        }

        assertTrue(heap.isEmpty());
    }

    @Test
    @Order(1400)
    void testMixedOrderInsertDelete2() {
        // test8
        addKeys(7000);
        addKeysReverse(9000);

        Map<Integer, FibonacciHeap.HeapNode> nodes = addKeys(2000);

        for (int i = 2000; i < 2500; i++) {
            assertEquals(heapModel.findMin(),  heap.findMin().getKey());
            assertEquals(heapModel.size(), heap.size());
            heapModel.delete(i);
            heap.delete(nodes.get(i));
            assertValidHeap(heap);
        }

        while (!heapModel.isEmpty()) {
            assertEquals(heapModel.findMin(),  heap.findMin().getKey());
            assertEquals(heapModel.size(), heap.size());
            heapModel.deleteMin();
            heap.deleteMin();
            assertValidHeap(heap);
        }

        assertTrue(heap.isEmpty());
    }

    @Test
    @Order(1600)
    void testMixedOrderInsertDelete3() {
        // test9
        addKeys(7000);
        addKeysReverse(9000);

        Map<Integer, FibonacciHeap.HeapNode> nodes = addKeys(2000);

        for (int i = 2700; i > 2200; i--) {
            assertEquals(heapModel.findMin(),  heap.findMin().getKey());
            assertEquals(heapModel.size(), heap.size());
            heapModel.delete(i);
            heap.delete(nodes.get(i));
            assertValidHeap(heap);
        }

        while (!heapModel.isEmpty()) {
            assertEquals(heapModel.findMin(),  heap.findMin().getKey());
            assertEquals(heapModel.size(), heap.size());
            heapModel.deleteMin();
            heap.deleteMin();
            assertValidHeap(heap);
        }

        assertTrue(heap.isEmpty());
    }

    @Test
    @Order(1300)
    void testConsolidatedMixedOrderInsertDelete() {
        // test10
        addKeys(7000);
        addKeysReverse(9000);

        Map<Integer, FibonacciHeap.HeapNode> nodes = addKeys(2000);

        heapModel.deleteMin();
        heap.deleteMin();

        assertValidHeap(heap);

        for (int i = 2700; i > 2200; i--) {
            assertEquals(heapModel.findMin(),  heap.findMin().getKey());
            assertEquals(heapModel.size(), heap.size());
            heapModel.delete(i);
            heap.delete(nodes.get(i));
            assertValidHeap(heap);
        }

        while (!heapModel.isEmpty()) {
            assertEquals(heapModel.findMin(),  heap.findMin().getKey());
            assertEquals(heapModel.size(), heap.size());
            heapModel.deleteMin();
            heap.deleteMin();
            assertValidHeap(heap);
        }

        assertTrue(heap.isEmpty());
    }

    @Test
    @Order(587)
    void testDecreaseKey() {
        // test11
        addKeys(1000);
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap, 9999);
        heap.decreaseKey(nodes.get(9999), 9999);
        assertValidHeap(heap);
        assertEquals(0, heap.findMin().getKey());

        heap.deleteMin();
        assertValidHeap(heap);

        for (int i = 1000; i < 2000; i++) {
            assertEquals(i,  heap.findMin().getKey());
            heap.deleteMin();
            assertValidHeap(heap);
        }

        assertTrue(heap.isEmpty());
    }

    //@Disabled // Same value insertion is not supported in our version
    @Tag("DuplicateValues")
    @Test
    @Order(235)
    void testMultipleMinDecreaseKey() {
        // test12
        addKeys(1000);
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap, 5000);
        heap.decreaseKey(nodes.get(5000), 4000);
        assertValidHeap(heap);

        for (int i = 0; i < 2; i++) {
            assertEquals(1000,  heap.findMin().getKey());
            heap.deleteMin();
            assertValidHeap(heap);
        }

        for (int i = 1001; i < 2000; i++) {
            assertEquals(i, heap.findMin().getKey());
            heap.deleteMin();
            assertValidHeap(heap);
        }

        assertTrue(heap.isEmpty());
    }

    @Test
    @Order(162)
    void testDecreaseKey2() {
        // test13
        addKeys(1000);
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap, 9000);
        heap.decreaseKey(nodes.get(9000), 4000);
        assertValidHeap(heap);

        for (int i = 1000; i < 2000; i++) {
            assertEquals(i,  heap.findMin().getKey());
            heap.deleteMin();
            assertValidHeap(heap);
        }

        assertEquals(5000, heap.findMin().getKey());
        heap.deleteMin();
        assertValidHeap(heap);

        assertTrue(heap.isEmpty());
    }

    @Test
    @Order(602)
    void testMixedOrderDecreaseKey() {
        // test14
        addKeys(1000);
        addKeysReverse(7000);

        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap, 9000);
        heap.decreaseKey(nodes.get(9000), 4000);
        assertValidHeap(heap);

        for (int i = 1000; i < 2000; i++) {
            assertEquals(i,  heap.findMin().getKey());
            heap.deleteMin();
            assertValidHeap(heap);
        }
        assertEquals(5000, heap.findMin().getKey());
        heap.deleteMin();
        assertValidHeap(heap);

        for (int i = 7000; i < 8000; i++) {
            assertEquals(i,  heap.findMin().getKey());
            heap.deleteMin();
            assertValidHeap(heap);
        }

        assertTrue(heap.isEmpty());
    }

    @Test
    @Order(10300)
    void testConsolidatedDecreaseKey() {
        // test15

        for (int i = 1000; i < 10000; i += 1000) {
            addKeys(i);
        }

        heap.deleteMin();
        assertValidHeap(heap);

        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap, 99999);
        heap.decreaseKey(nodes.get(99999), 99999);

        assertEquals(0, heap.findMin().getKey());
        heap.deleteMin();
        assertValidHeap(heap);

        for (int i = 1001; i < 10000; i++) {
            assertEquals(i, heap.findMin().getKey());
            heap.deleteMin();
            assertValidHeap(heap);
        }

        assertTrue(heap.isEmpty());
    }

    @Test
    @Order(1)
    void testInsertionAuxVariables() {
        // test16
        int cuts = FibonacciHeap.totalCuts();
        int links = FibonacciHeap.totalLinks();

        testInsertion(heap, 1, 2, 3);

        assertEquals(3, heap.potential());
        assertEquals(0, FibonacciHeap.totalCuts() - cuts);
        assertEquals(0, FibonacciHeap.totalLinks() - links);
        assertTrue(Arrays.equals(new int[] { 3 }, heap.countersRep()));
    }

    @Test
    @Order(1)
    void testInsertionDeleteMinAuxVariables() {
        // test17
        int cuts = FibonacciHeap.totalCuts();
        int links = FibonacciHeap.totalLinks();

        testInsertion(heap, 1, 2, 3);
        heap.deleteMin();
        assertValidHeap(heap);

        assertEquals(1, heap.potential());
        assertEquals(0, FibonacciHeap.totalCuts() - cuts);
        assertEquals(1, FibonacciHeap.totalLinks() - links);
        assertTrue(Arrays.equals(new int[] { 0, 1 }, heap.countersRep()));
    }

    @Test
    @Order(1)
    void testInsertionDeleteMinAuxVariables2() {
        // test18

        int cuts = FibonacciHeap.totalCuts();
        int links = FibonacciHeap.totalLinks();

        testInsertion(heap, 4, 5, 6);
        heap.deleteMin();
        assertValidHeap(heap);

        testInsertion(heap, 1, 2, 3);
        heap.deleteMin();
        assertValidHeap(heap);

        testInsertion(heap, 1);
        heap.deleteMin();
        assertValidHeap(heap);

        assertEquals(1, heap.potential());
        assertEquals(0, FibonacciHeap.totalCuts() - cuts);
        assertEquals(3, FibonacciHeap.totalLinks() - links);
        assertTrue(Arrays.equals(new int[] { 0, 0, 1 }, heap.countersRep()));
    }

    @Test
    @Order(1)
    void testInsertionDeleteMinDecreaseKeyAuxVariables() {
        // test19

        int cuts = FibonacciHeap.totalCuts();
        int links = FibonacciHeap.totalLinks();

        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap, 4, 5, 6);
        heap.deleteMin();
        assertValidHeap(heap);

        testInsertion(heap, 1, 2, 3);
        heap.deleteMin();
        assertValidHeap(heap);

        testInsertion(heap, 1);
        heap.deleteMin();
        assertValidHeap(heap);

        heap.decreaseKey(nodes.get(6), 2);
        assertValidHeap(heap);

        assertEquals(4, heap.potential());
        assertEquals(1, FibonacciHeap.totalCuts() - cuts);
        assertEquals(3, FibonacciHeap.totalLinks() - links);
        assertTrue(Arrays.equals(new int[] { 1, 0, 1 }, heap.countersRep()));
    }

    //@Disabled // Same value insertion is not supported in our version
    @Tag("DuplicateValues")
    @Test
    @Order(2)
    void testDuplicateKeysDecreaseKeyAuxVariables() {
        // test20

        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap, 4, 5, 6);
        heap.deleteMin();
        assertValidHeap(heap);

        testInsertion(heap, 1, 2, 3);
        heap.deleteMin();
        assertValidHeap(heap);

        testInsertion(heap, 1);
        heap.deleteMin();
        assertValidHeap(heap);

        int cuts = FibonacciHeap.totalCuts();
        int links = FibonacciHeap.totalLinks();

        heap.decreaseKey(nodes.get(6), 2);
        assertValidHeap(heap);
        heap.decreaseKey(nodes.get(5), 1);
        assertValidHeap(heap);

        assertEquals(4, heap.potential());
        assertEquals(1, FibonacciHeap.totalCuts() - cuts);
        assertEquals(0, FibonacciHeap.totalLinks() - links);
    }

    @Test
    @Order(94900)
    void testLargeTreeInsertDeleteMinPotential() {
        // test21

        int treeSize = 32768;
        int sizeToDelete = 1000;

        testInsertion(heap, IntStream.rangeClosed(treeSize, treeSize * 2 - 1)::iterator);
        testInsertion(heap, IntStream.rangeClosed(0, sizeToDelete - 1)::iterator);

        for (int i = 0; i < sizeToDelete; i++) {
            heap.deleteMin();
            assertValidHeap(heap);
        }

        assertEquals(1, heap.potential());
    }

    //@Disabled // Same value insertion is not supported in our version
    @Tag("DuplicateValues")
    @Tag("NoCompare") // Test uses Collections.shuffle
    @Test
    @Order(202300)
    void testLargeTreeInsertDeleteMinDecreaseKeyAuxVariables() {
        // test22

        List<FibonacciHeap.HeapNode> nodes = new ArrayList<>();
        int treeSize = 32768;
        int sizeToDelete = 1000;

        for (int i = treeSize; i < treeSize * 2; i++) {
            nodes.add(heap.insert(i)); // don't check assertValidHeap each time in order to reduce runtime
        }
        for (int i = 0; i < 1000; i++) {
            heap.insert(i); // don't check assertValidHeap each time in order to reduce runtime
        }
        assertValidHeap(heap);

        for (int i = 0; i < sizeToDelete; i++) {
            heap.deleteMin(); // don't check assertValidHeap each time in order to reduce runtime
        }
        assertValidHeap(heap);
        assertEquals(1, heap.potential());

        int totalCuts = FibonacciHeap.totalCuts();
        int links = FibonacciHeap.totalLinks();

        boolean noCascading = true;
        int iterationCuts;

        Collections.shuffle(nodes);

        for (int i = 0; i < treeSize; i++) {
            iterationCuts = FibonacciHeap.totalCuts();

            heap.decreaseKey(nodes.get(i), nodes.get(i).getKey() - (treeSize - i));
            assertValidHeap(heap);

            if (FibonacciHeap.totalCuts() - iterationCuts > 1)
                noCascading = false;
        }

        assertValidHeap(heap);

        assertEquals(treeSize, heap.potential());
        assertEquals(treeSize - 1, FibonacciHeap.totalCuts() - totalCuts);
        assertEquals(0, FibonacciHeap.totalLinks() - links);
        assertTrue(Arrays.equals(new int[] {treeSize}, heap.countersRep()));
        assertFalse(noCascading);
    }

    @Test
    @Order(346)
    void testReverseOrderInsertionAuxVariables() {
        // test23

        int size = 1000;
        int totalCuts = FibonacciHeap.totalCuts();
        int links = FibonacciHeap.totalLinks();

        addKeysReverse(size - 999);

        assertEquals(size, heap.potential());
        assertEquals(0, FibonacciHeap.totalCuts() - totalCuts);
        assertEquals(0, FibonacciHeap.totalLinks() - links);
    }

    @Test
    @Order(715)
    void testReverseOrderInsertionAuxVariables2() {
        // test24

        int size = 2000;
        int totalCuts = FibonacciHeap.totalCuts();
        int links = FibonacciHeap.totalLinks();

        int remaining = size;
        while (remaining > 0) {
            addKeysReverse(remaining - 999);
            remaining -= 1000;
        }

        assertEquals(size, heap.potential());
        assertEquals(0, FibonacciHeap.totalCuts() - totalCuts);
        assertEquals(0, FibonacciHeap.totalLinks() - links);
    }

    @Test
    @Order(1100)
    void testReverseOrderInsertionAuxVariables3() {
        // test25

        int size = 3000;
        int totalCuts = FibonacciHeap.totalCuts();
        int links = FibonacciHeap.totalLinks();

        int remaining = size;
        while (remaining > 0) {
            addKeysReverse(remaining - 999);
            remaining -= 1000;
        }

        assertEquals(size, heap.potential());
        assertEquals(0, FibonacciHeap.totalCuts() - totalCuts);
        assertEquals(0, FibonacciHeap.totalLinks() - links);
    }

    @Test
    @Order(457)
    void testInsertionPartialDeleteMinAuxVariables() {
        // test26

        int size = 1000;
        int totalCuts = FibonacciHeap.totalCuts();
        int links = FibonacciHeap.totalLinks();

        addKeysReverse(size - 999);

        for (int i = 0; i < size / 2; i++) {
            assertEquals(i + 1,  heap.findMin().getKey());
            heap.deleteMin();
            assertValidHeap(heap);
        }

        assertTrue(heap.potential() <= 100);
        assertEquals(0, FibonacciHeap.totalCuts() - totalCuts);
        assertTrue(FibonacciHeap.totalLinks() - links >= size - 100);
    }

    @Test
    @Order(1100)
    void testInsertionPartialDeleteMinAuxVariables2() {
        // test27

        int size = 2000;
        int totalCuts = FibonacciHeap.totalCuts();
        int links = FibonacciHeap.totalLinks();

        int remaining = size;
        while (remaining > 0) {
            addKeysReverse(remaining - 999);
            remaining -= 1000;
        }

        for (int i = 0; i < size / 2; i++) {
            assertEquals(i + 1,  heap.findMin().getKey());
            heap.deleteMin();
            assertValidHeap(heap);
        }

        assertTrue(heap.potential() <= 100);
        assertEquals(0, FibonacciHeap.totalCuts() - totalCuts);
        assertTrue(FibonacciHeap.totalLinks() - links >= size - 100);
    }

    @Test
    @Order(2000)
    void testInsertionPartialDeleteMinAuxVariables3() {
        // test28

        int size = 3000;
        int totalCuts = FibonacciHeap.totalCuts();
        int links = FibonacciHeap.totalLinks();

        int remaining = size;
        while (remaining > 0) {
            addKeysReverse(remaining - 999);
            remaining -= 1000;
        }

        for (int i = 0; i < size / 2; i++) {
            assertEquals(i + 1,  heap.findMin().getKey());
            heap.deleteMin();
            assertValidHeap(heap);
        }

        assertTrue(heap.potential() <= 100);
        assertEquals(0, FibonacciHeap.totalCuts() - totalCuts);
        assertTrue(FibonacciHeap.totalLinks() - links >= size - 100);
    }

    @Test
    @Order(55)
    void testkMinSanity2() {
        // test29
        // kMin
        testInsertion(heap, IntStream.rangeClosed(0, 32)::iterator);

        heap.deleteMin();
        assertValidHeap(heap);

        int[] kmin = FibonacciHeap.kMin(heap, 10);
        assertTrue(Arrays.equals(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, kmin));
    }

    @Test
    @Order(98)
    void testMeldSanity() {
        // test30
        // insert and meld
        FibonacciHeap firstFibonacciHeap = new FibonacciHeap();
        FibonacciHeap secondFibonacciHeap = new FibonacciHeap();
        testInsertion(firstFibonacciHeap, IntStream.rangeClosed(0, 99)::iterator);
        testInsertion(secondFibonacciHeap, IntStream.rangeClosed(100, 199)::iterator);

        firstFibonacciHeap.meld(secondFibonacciHeap);
        assertValidHeap(heap);

        for (int i = 0; i < 200; i++) {
            FibonacciHeap.HeapNode min = firstFibonacciHeap.findMin();
            assertValidHeap(heap);
            assertNotNull(min);
            assertEquals(i, min.getKey());
            firstFibonacciHeap.deleteMin();
            assertValidHeap(heap);
        }
    }

    Random rand = new Random();
    private void insertKRandomKeys(int k, Map<Integer, FibonacciHeap.HeapNode> nodes,
                                   Map<Integer, FibonacciHeap.HeapNode> otherNodes, FibonacciHeap heap, int lowBound,
                                   int upperBound) {
        if (lowBound >= upperBound) {
            return;
        }

        for (int i = 0; i < k; i++) {
            int toIns = rand.nextInt(upperBound - lowBound) + lowBound;
            inserNode(nodes, otherNodes, heap, toIns);
        }
    }

    private void inserNode(Map<Integer, FibonacciHeap.HeapNode> nodes, Map<Integer,
            FibonacciHeap.HeapNode> otherNodes, FibonacciHeap heap, int num) {
        if (!nodes.containsKey(num) && !otherNodes.containsKey(num)) {
            nodes.put(num, heap.insert(num));
        }
    }

    private void deleteOrDecreaseOrMin(Map<Integer, FibonacciHeap.HeapNode> nodes,
                                       Map<Integer, FibonacciHeap.HeapNode> otherNodes, FibonacciHeap heap) {
        if (heap.isEmpty()) {
            return;
        }

        int indicator = rand.nextInt(3);
        if (indicator == 2) {
            nodes.remove(heap.findMin().getKey());
            heap.deleteMin();
            return;
        }

        List<Entry<Integer, FibonacciHeap.HeapNode>> keys = new LinkedList<>(nodes.entrySet());
        int index = rand.nextInt(keys.size());

        if (indicator == 0) {
            // delete
            heap.delete(keys.get(index).getValue());
            nodes.remove(keys.get(index).getKey());
        } else {
            // decreaseKey
            int decreaseVal = rand.nextInt(2000);
            int key = keys.get(index).getKey();
            if (!(nodes.containsKey(key - decreaseVal)) && !(otherNodes.containsKey(key - decreaseVal))) {
                heap.decreaseKey(keys.get(index).getValue(), decreaseVal);
                nodes.remove(keys.get(index).getKey());
                nodes.put(keys.get(index).getKey() - decreaseVal, keys.get(index).getValue());
            }
        }
    }

    private void meld(Map<Integer, FibonacciHeap.HeapNode> nodes, FibonacciHeap heap,
                      Map<Integer, FibonacciHeap.HeapNode> nodes2, FibonacciHeap heap2) {
        heap.meld(heap2);
        nodes.putAll(nodes2);
    }

    @Tag("NoCompare") // Test uses Collections.shuffle and Random
    @Test
    @Order(864400)
    public void stressTest() {
        final int changes = 35;
        final int meldings = 5;
        final int iterations = 10000;
        FibonacciHeap stressHeap = null;
        FibonacciHeap stressHeap2 = null;
        Map<Integer, FibonacciHeap.HeapNode> nodes = null;
        Map<Integer, FibonacciHeap.HeapNode> nodes2 = null;
        for (int j = 0; j < iterations; j++) {
            System.out.println("Iteration #" + j);
            stressHeap = new FibonacciHeap();
            nodes = new HashMap<>();
            for (int i = 0; i < meldings; i++) {
                nodes2 = new HashMap<>();
                stressHeap2 = new FibonacciHeap();
                insertKRandomKeys(1000, nodes, nodes, stressHeap, 0, (i+1) * 1000);
                insertKRandomKeys(1000, nodes2, nodes2, stressHeap2, (i + 1) * 1000 + 1, (i + 2) * 1000);
                assertValidHeap(stressHeap);
                assertValidHeap(stressHeap2);
                for (int k = 0; k < changes; k++) {
                    deleteOrDecreaseOrMin(nodes, nodes2, stressHeap);
                    deleteOrDecreaseOrMin(nodes2, nodes, stressHeap2);
                    assertValidHeap(stressHeap);
                    assertValidHeap(stressHeap2);

                }
                meld(nodes, stressHeap, nodes2, stressHeap2);
                assertValidHeap(stressHeap);
            }
        }
    }

    @Test
    @Order(64)
    public void testInsertionOrder() {
        testInsertion(heap, 5, 8, 2, 0);

        FibonacciHeap.HeapNode first = heap.firstRoot;
        assertEquals(first.getKey(), 0);
        assertEquals(first.next.getKey(), 2);
        assertEquals(first.next.next.getKey(), 8);
        assertEquals(first.next.next.next.getKey(), 5);
    }

    @Test
    @Order(84)
    public void testConsolidationOrder() {
        testInsertion(heap, IntStream.rangeClosed(0, 7)::iterator);
        heap.deleteMin();
        assertValidHeap(heap);

        FibonacciHeap.HeapNode first = heap.firstRoot;
        assertEquals(0, first.rank);
        assertEquals(1, first.getKey());
        assertEquals(first.next.rank, 1);
        assertEquals(2, first.next.getKey());
        assertEquals(first.next.next.rank, 2);
        assertEquals(4, first.next.next.getKey());
    }

    @Test
    @Order(72)
    public void testDecreaseKeyOrder() {
        Map<Integer, FibonacciHeap.HeapNode> nodes =
                testInsertion(heap, IntStream.rangeClosed(0, 7)::iterator);
        heap.deleteMin();
        assertValidHeap(heap);

        heap.decreaseKey(nodes.get(6), 6);
        assertValidHeap(heap);
        assertSame(nodes.get(6), heap.firstRoot);
    }

    @Test
    @Order(48)
    public void testDeleteMinEdge1() {
        // Delete min when min = first, it no children and no siblings
        testInsertion(heap, 0);
        heap.deleteMin();
        assertValidHeap(heap);
        assertTrue(heap.isEmpty());
    }

    @Test
    @Order(56)
    public void testDeleteMinEdge2() {
        // Delete min when min = first, it has has no children and has a sibling
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap, 2, 0);
        heap.deleteMin();
        assertValidHeap(heap);
        assertSame(nodes.get(2), heap.findMin());
    }

    @Test
    @Order(55)
    public void testDeleteMinEdge3() {
        // Delete min when min = first, it has a child but has no siblings
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap, 2, 1, 0);
        heap.deleteMin();
        assertValidHeap(heap);
        heap.deleteMin();
        assertValidHeap(heap);
        assertSame(nodes.get(2), heap.findMin());
    }

    @Test
    @Order(63)
    public void testDeleteMinEdge4() {
        // Delete min when min = first, it has multiple children but has no siblings
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap, 4, 3, 2, 1, 0);
        heap.deleteMin();
        assertValidHeap(heap);
        heap.deleteMin();
        assertValidHeap(heap);
        assertSame(nodes.get(2), heap.findMin());
        assertSame(nodes.get(2), heap.firstRoot);
        assertSame(nodes.get(3), heap.firstRoot.next);
        assertSame(nodes.get(4), heap.firstRoot.next.child);
    }

    @Test
    @Order(62)
    public void testDeleteMinEdge5() {
        // Delete min when min = first, it has a child and has a sibling
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap, 3, 1, 2, 0);
        heap.deleteMin();
        assertValidHeap(heap);
        heap.deleteMin();
        assertValidHeap(heap);
        assertSame(nodes.get(2), heap.findMin());
    }

    @Test
    @Order(72)
    public void testDeleteMinEdge6() {
        // Delete min when min = first, it has multiple children and has a sibling
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap, 4, 1, 2, 3, 0);
        heap.deleteMin();
        assertValidHeap(heap);

        FibonacciHeap heap2 = new FibonacciHeap();
        nodes.putAll(testInsertion(heap2, 5));
        heap.meld(heap2);
        assertValidHeap(heap);

        heap.deleteMin();
        assertValidHeap(heap);
        assertEquals(4, heap.size());
        assertSame(nodes.get(2), heap.findMin());
        assertSame(nodes.get(2), heap.firstRoot);
        assertSame(nodes.get(4), heap.firstRoot.child);
        assertSame(nodes.get(3), heap.firstRoot.child.next);
        assertSame(nodes.get(5), heap.firstRoot.child.child);
    }

    @Test
    @Order(58)
    public void testDeleteMinEdge7() {
        // Delete min when min != first, it has no child and has a siblings
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap, 0, 2);
        heap.deleteMin();
        assertValidHeap(heap);
        assertSame(nodes.get(2), heap.findMin());
    }

    @Test
    @Order(72)
    public void testDeleteMinEdge8() {
        // Delete min when min != first, it has no child and has multiple siblings
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap, 3, 0, 2);
        heap.deleteMin();
        assertValidHeap(heap);
        assertSame(nodes.get(2), heap.findMin());
    }

    @Test
    @Order(77)
    public void testDeleteMinEdge9() {
        // Delete min when min != first, it has a child and has a sibling
        testInsertionReverse(heap, 5, 6);
        testInsertion(heap, 0);

        FibonacciHeap heap2 = new FibonacciHeap();
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap2, 0, 1, 2, 3, 4);

        heap.deleteMin();
        assertValidHeap(heap);
        heap2.deleteMin();
        assertValidHeap(heap2);

        heap.meld(heap2);
        assertValidHeap(heap);

        heap.deleteMin();
        assertValidHeap(heap);

        assertSame(nodes.get(2), heap.firstRoot);
        assertSame(nodes.get(3), heap.firstRoot.next);
    }

    @Test
    @Order(86)
    public void testDeleteMinEdge10() {
        // Delete min when min != first, it has multiple children and has a sibling
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap, -20);

        FibonacciHeap heap2 = new FibonacciHeap();
        nodes.putAll(testInsertion(heap2, -100, -30, -25, -15, -10));
        heap2.deleteMin();
        assertValidHeap(heap2);

        heap.meld(heap2);
        assertValidHeap(heap);

        heap.deleteMin();
        assertValidHeap(heap);
        assertEquals(4, heap.size());
        assertSame(nodes.get(-25), heap.findMin());
        assertSame(nodes.get(-25), heap.firstRoot);
        assertSame(nodes.get(-15), heap.firstRoot.child);
        assertSame(nodes.get(-20), heap.firstRoot.child.next);
        assertSame(nodes.get(-10), heap.firstRoot.child.child);
    }

    @Test
    @Order(82)
    public void testDeleteMinEdge11() {
        // Delete min when min != first, it has a child and has multiple siblings
        testInsertionReverse(heap, 5, 6);
        testInsertion(heap, 0);
        FibonacciHeap heap2 = new FibonacciHeap();
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap2, 0, 1, 2, 3, 4);
        FibonacciHeap heap3 = new FibonacciHeap();
        testInsertion(heap3, 100);
        heap.deleteMin();
        assertValidHeap(heap);
        heap2.deleteMin();
        assertValidHeap(heap2);
        heap.meld(heap2);
        heap.meld(heap3);
        assertValidHeap(heap);
        heap.deleteMin();
        assertSame(nodes.get(2), heap.firstRoot);
        assertSame(nodes.get(3), heap.firstRoot.next);
    }

    @Test
    @Order(132)
    public void testDeleteMinEdge12() {
        // Delete min when min != first, it has multiple children and has multiple siblings
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap, IntStream.rangeClosed(9, 16)::iterator);

        FibonacciHeap heap2 = new FibonacciHeap();
        nodes.putAll(testInsertion(heap2, IntStream.rangeClosed(1, 8)::iterator));

        FibonacciHeap heap3 = new FibonacciHeap();
        nodes.putAll(testInsertion(heap3, IntStream.rangeClosed(17, 32)::iterator));

        for (FibonacciHeap h : Arrays.asList(heap, heap2, heap3)) {
            testInsertion(h, Integer.MIN_VALUE);
            h.deleteMin();
            assertValidHeap(h);
        }

        heap.meld(heap2);
        assertValidHeap(heap);

        heap.meld(heap3);
        assertValidHeap(heap);

        heap.deleteMin();
        assertValidHeap(heap);

        assertSame(nodes.get(2), heap.findMin());

        FibonacciHeap.HeapNode node = heap.firstRoot;
        assertSame(nodes.get(2), heap.firstRoot);
        node = node.next;
        assertSame(nodes.get(3), node);
        node = node.next;
        assertSame(nodes.get(5), node);
        node = node.next;
        assertSame(nodes.get(9), node);
        node = node.next;
        assertSame(nodes.get(17), node);
    }

    @Test
    @Order(51)
    public void testEmptyMeld() {
        heap.meld(new FibonacciHeap());
        assertValidHeap(heap);
        assertTrue(heap.isEmpty());

        FibonacciHeap.HeapNode node = testInsertion(heap, 0).get(0);
        heap.meld(new FibonacciHeap());
        assertValidHeap(heap);
        assertEquals(1, heap.size());
        assertSame(node, heap.firstRoot);
        assertSame(node, heap.findMin());

        FibonacciHeap heap2 = heap;
        heap = new FibonacciHeap();
        heap.meld(heap2);
        assertEquals(1, heap.size());
        assertSame(node, heap.firstRoot);
        assertSame(node, heap.findMin());
    }

    @Test
    @Order(57)
    public void testDeleteMinValue() {
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap, Integer.MIN_VALUE, 0);
        testDeletion(heap, nodes.get(0));
        assertSame(nodes.get(Integer.MIN_VALUE), heap.findMin());
        assertSame(nodes.get(Integer.MIN_VALUE), heap.firstRoot);
    }

    @Test
    @Order(84)
    public void testDeleteFirst1() {
        // delete first when it has no children
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertion(heap, 10);

        FibonacciHeap heap2 = new FibonacciHeap();
        nodes.putAll(testInsertionReverse(heap2, 0, 8));

        heap2.deleteMin();
        assertValidHeap(heap2);

        testDeletion(heap2, nodes.get(6), nodes.get(4)); // delete few nodes to make it non binomial

        heap.meld(heap2);
        assertValidHeap(heap);
        assertSame(nodes.get(10), heap.firstRoot);
        assertEquals(0, heap.firstRoot.rank);

        testDeletion(heap, heap.firstRoot); // delete first

        assertSame(nodes.get(1), heap.firstRoot);
        assertSame(nodes.get(1), heap.findMin());
    }

    @Test
    @Order(85)
    public void testDeleteFirst2() {
        // delete first when it has multiple children
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertionReverse(heap, 0, 8);

        FibonacciHeap heap2 = new FibonacciHeap();
        nodes.putAll(testInsertion(heap2, 10));

        heap.deleteMin();
        assertValidHeap(heap);

        testDeletion(heap, nodes.get(6), nodes.get(4)); // delete few nodes to make it non binomial

        heap.meld(heap2);
        assertValidHeap(heap);
        assertSame(nodes.get(1), heap.firstRoot);
        assertEquals(3, heap.firstRoot.rank);

        testDeletion(heap, heap.firstRoot); // delete first

        assertSame(nodes.get(10), heap.firstRoot);
        assertSame(nodes.get(2), heap.firstRoot.next);
        assertSame(nodes.get(2), heap.findMin());
    }

    @Tag("NoCompare")
    @Test
    @Order(4900)
    public void testSpecialMarkedChainTree() {
        int depth = 10000;
        // case 10
        int n = depth * 5; // must divide by 5

        // base
        Map<Integer, FibonacciHeap.HeapNode> nodes = testInsertionReverse(heap, n - 5, n - 1);
        heap.deleteMin();
        assertValidHeap(heap);
        testDeletion(heap, nodes.get(n - 1));

        // loop
        Map<Integer, FibonacciHeap.HeapNode> currentNodes = null;
        FibonacciHeap.HeapNode middle, first, second;
        int i = 1;
        for (;i < Math.min(10, n / 5); i++) { // test the first 10 iterations
            middle = heap.firstRoot.child.next;
            currentNodes = testInsertionReverse(heap, n - ((i + 1) * 5), n - ((i + 1) * 5) + 4);
            heap.deleteMin();
            assertValidHeap(heap);
            testDeletion(
                    heap,
                    currentNodes.get(n - ((i + 1) * 5) + 4),
                    currentNodes.get(n - ((i + 1) * 5) + 3),
                    middle);
        }

        for (; i < n / 5; i++) { // complete all iterations (unchecked)
            middle = heap.firstRoot.child.next;
            first = heap.insert(n - ((i + 1) * 5) + 4);
            second = heap.insert(n - ((i + 1) * 5) + 3);
            heap.insert(n - ((i + 1) * 5) + 2);
            heap.insert(n - ((i + 1) * 5) + 1);
            heap.insert(n - ((i + 1) * 5));
            heap.deleteMin();

            heap.delete(first);
            heap.delete(second);
            heap.delete(middle);
        }

        assertValidHeap(heap);


        testDeletion(heap, heap.firstRoot.child.next);
        FibonacciHeap.HeapNode node = heap.firstRoot;
        while (node != null) {
            assertTrue(node.child != null ? node.rank == 1 : node.rank == 0);
            assertEquals(node.parent != null, node.mark);
            node = node.child;
        }


        heap.decreaseKey(nodes.get(n - 2), n);

        assertValidHeap(heap);

        node = heap.firstRoot;
        while (node != null && node != heap.firstRoot) {
            assertNull(node.child);
            node = node.next;
        }
    }
}