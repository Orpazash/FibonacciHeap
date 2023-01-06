package fibHeap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class OurPrintHeap {

    static final int insertNum = 10;
    static final int defaultSeed = 55;

    public static void main(String[] args) {
//        decrease_key_test();
//        test_delete_min();
//        test_meld();
//        rand_delete_test();
        kMinTest();
    }

    public static FibonacciHeap random_insert() {
        Random rand = new Random(defaultSeed);
        ArrayList<Integer> numbers = new ArrayList<>();
        FibonacciHeap fib = new FibonacciHeap();
        for (int i = 0; i < insertNum; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers, rand);
        for (int i = 0; i < insertNum; i++) {
            fib.insert(numbers.get(i));
        }
        return fib;
    }

    public static void decrease_key_test() {
        FibonacciHeap fib = random_insert();
        fib.deleteMin();
        printFibHeap(fib);
        int index = 0;
        while (fib.lastRoot.child != null && fib.lastRoot.child.child!=null) {
            fib.decreaseKey(fib.lastRoot.child.child, fib.lastRoot.child.child.getKey()+index);
            index++;
            printFibHeap(fib);
        }
    }

    public static void test_delete_min() {
        FibonacciHeap fib2 = new FibonacciHeap();
        int n = 40;
        int key = 1;
        for (int i=0; i<n/2; i++ ) {
            fib2.insert(key);
            fib2.insert(n-key);
            key += 1;
        }
        printHeap(fib2, fib2.firstRoot);
        System.out.println("------------------");
        fib2.deleteMin();  // delete 1
        printHeap(fib2, fib2.firstRoot);
        System.out.println("------------------");
        fib2.deleteMin();   // delete 2
        printHeap(fib2, fib2.firstRoot);
        System.out.println("------------------");
        fib2.deleteMin();  // delete 3
        printHeap(fib2, fib2.firstRoot);
        System.out.println("------------------");
        fib2.deleteMin();  // delete 3
        printHeap(fib2, fib2.firstRoot);
        System.out.println("------------------");
    }

    public static void test_meld() {
        FibonacciHeap firstFib = new FibonacciHeap();
        int n = 10;
        int key = 1;
        for (int i = 0; i < n; i++) {
            firstFib.insert(key);
            key += 1;
        }
        FibonacciHeap secondFib = new FibonacciHeap();
        int m = 3;
        int key2 = 20;
        for (int i = 0; i < m; i++) {
            secondFib.insert(key2);
            key2 += 1;
        }
        firstFib.deleteMin();
        secondFib.deleteMin();
        printFibHeap(firstFib);
        printFibHeap(secondFib);
        firstFib.meld(secondFib);
        printFibHeap(firstFib);
    }


    public static void rand_delete_test() {
        FibonacciHeap fib = random_insert();
        for (int i = 0; i < fib.size; i++) {
            if (fib.findMin().getKey() != i) {
                System.out.println("wrong " + i);
                return;
            }
            fib.deleteMin();
            printHeap(fib, fib.firstRoot);
            System.out.println("-------------------------");

        }
    }

    public static void kMinTest(){
        //creating one tree heap
        FibonacciHeap fib = random_insert();
        while(fib.trees >1)
            fib.delete(fib.firstRoot);
        printFibHeap(fib);
        System.out.println(Arrays.toString(FibonacciHeap.kMin(fib,6)));

    }


    /** print methods */
    public static void printFibHeap(FibonacciHeap fib) {
        System.out.println("Heap's details: ");
        System.out.println("• Is empty? - " + fib.isEmpty());
        if (fib.isEmpty())
            System.out.println("• Min,first and last nodes doesn't exists because the heap is empty");
        else {
            System.out.println("• Min node - " + fib.min.getKey());
            System.out.println("• First node - " + fib.firstRoot.getKey());
            System.out.println("• Last node - " + fib.lastRoot.getKey());
        }
        System.out.println("• Heap's size - " + fib.size());
        System.out.println("• Number of trees in the heap - " + fib.trees);
        System.out.println("• Number of marked nodes - " + fib.marked);
        System.out.println("• Number of non-marked nodes - " + fib.nonMarked());
        System.out.println("• Potential function value - " + fib.potential());
        System.out.println("• Counter repeats array - " + Arrays.toString(fib.countersRep()));

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("The heap itself: ");
        FibonacciHeap.HeapNode x = fib.firstRoot;
        printHeap(fib,x);
        System.out.println("");
        System.out.println("The nodes' details: ");
        for (int i = 0; i < fib.trees; i++){
            System.out.println("---------");
            System.out.println("Tree number " + i + ":");
            printTreeDetails(x);
            x = x.next;
        }
    }

    public static void printNode(FibonacciHeap.HeapNode node) {
        System.out.println("• Node's Key = " + node.key +", Rank = " +
                node.rank + ", is marked? - " + node.mark);
    }

    public static void printTreeDetails(FibonacciHeap.HeapNode root){
        System.out.println("The tree's level - 0:");
        printNode(root);
        FibonacciHeap.HeapNode x = root.child;
        int depth = 1;
        // while child isn't null
        while (x != null){
            System.out.println("The tree's level - " + depth + ":");
            FibonacciHeap.HeapNode start = x; // saving the child to move to the next level
            do{
                printNode(x);
                x = x.next;
            } while(x != start);
            x = start.child;
            depth++;
        }
    }
    public static void printHeap(FibonacciHeap fib, FibonacciHeap.HeapNode root){
        printHeapRec(fib, root, root,0);
    }
    private static void printHeapRec(FibonacciHeap fib,FibonacciHeap.HeapNode startNode, FibonacciHeap.HeapNode currentNode, int level){
        if (currentNode == null)
            return;
        for (int i = 0; i < level-1; i++)
            System.out.print("| ");
        if (level !=0)
            System.out.print("|_");
        else
            System.out.println("");
        System.out.print(currentNode.getKey());
        if (currentNode.mark)
            System.out.println("*");
        else
            System.out.println("");
        printHeapRec(fib,currentNode.child,currentNode.child,level+1);
        if (currentNode.next != startNode)
            printHeapRec(fib,startNode,currentNode.next,level);
    }

}
