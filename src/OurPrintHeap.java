import java.util.Arrays;

public class OurPrintHeap {

    public static void main(String[] args) {
        FibonacciHeap fib = new FibonacciHeap();
        FibonacciHeap.HeapNode node1 = new FibonacciHeap.HeapNode(4);
        FibonacciHeap.HeapNode node2 = new FibonacciHeap.HeapNode(5);
        FibonacciHeap.HeapNode node3 = new FibonacciHeap.HeapNode(7);
        fib.insert(2);
        fib.insert(3);
        fib.firstRoot.child = node1;
        node1.parent = fib.firstRoot;
        node2.prev = node1;
        node2.next = node1;
        node1.next = node2;
        node1.prev = node2;
        node2.parent = fib.firstRoot;
        node3.parent = node1;
        node1.child = node3;
        fib.firstRoot.rank = 2;
        node1.rank=1;
        printFibHeap(fib);
        //FiboHeapPrinter.printHeap(fib);
    }

    /**
     * print methods - delete before submitting
     */
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
