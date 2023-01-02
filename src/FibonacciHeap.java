/**
 * DON'T FORGET TO DELETE IMPORT - ONLY FOR PRINTING!
 */
import java.util.Arrays;

/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
    private HeapNode min;
    private HeapNode firstRoot;
    private HeapNode lastRoot;
    private int size;
    private int marked;
    private int trees;

    static int links = 0;
    static int cuts = 0;

    /**
     * constractor - delete before submitting
     */
    public FibonacciHeap(){
        this.min = null;
        this.firstRoot = null;
        this.lastRoot = null;
        this.size = 0;
        this.marked = 0;
        this.trees = 0;
    }

    /**
     * print methods - delete before submitting
     */
    public void printFibHeap(){
        System.out.println("Heap's details: ");
        System.out.println("• Is empty? - " + this.isEmpty());
        if (this.isEmpty())
            System.out.println("• Min,first and last nodes doesn't exists because the heap is empty");
        else {
            System.out.println("• Min node - " + this.min.getKey());
            System.out.println("• First node - " + this.firstRoot.getKey());
            System.out.println("• Last node - " + this.lastRoot.getKey());
        }
        System.out.println("• Heap's size - " + this.size());
        System.out.println("• Number of trees in the heap - " + this.trees);
        System.out.println("• Number of marked nodes - " + this.marked);
        System.out.println("• Number of non-marked nodes - " + this.nonMarked());
        System.out.println("• Potential function value - " + this.potential());
        System.out.println("• Counter repeats array - " + Arrays.toString(this.countersRep()));

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("The heap itself: ");
        HeapNode x = this.firstRoot;
        this.printHeap(x);
        System.out.println("");
        System.out.println("The nodes' details: ");
        int treeNum = 0;
        while (x != null) {
            System.out.println("---------");
            System.out.println("Tree number "+ treeNum + ":");
            this.printTreeDetails(x);
            x = x.next;
            treeNum++;
        }
    }

    public void printTreeDetails(HeapNode root){
        System.out.println("The tree's level - 0:");
        root.printNode();
        HeapNode x = root.child;
        int depth = 1;
        while (x != null){
            System.out.println("The tree's level - " + depth + ":");
            x.printNode();
            HeapNode start = x;
            while(x.next != null){
                x = x.next;
                x.printNode();
            }
            x = start.child;
            depth++;
        }
    }
    public void printHeap(HeapNode root){
        this.printHeapRec(root,0);
    }
    private void printHeapRec(HeapNode heapNode, int level){
        if (heapNode == null)
            return;
        for (int i = 0; i < level-1; i++)
            System.out.print("| ");
        if (level !=0)
            System.out.print("|_");
        System.out.print(heapNode.getKey());
        if (heapNode.mark)
            System.out.println("*");
        else
            System.out.println("");
        this.printHeapRec(heapNode.child,level+1);
        this.printHeapRec(heapNode.next,level);
    }


    /**
     * public boolean isEmpty()
     *
     * Returns true if and only if the heap is empty.
     *
     */
    public boolean isEmpty(){
        return this.size == 0;
    }

    /**
     * public HeapNode insert(int key)
     *
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     * The added key is assumed not to already belong to the heap.
     *
     * Returns the newly created node.
     */
    public HeapNode insert(int key)
    {
        HeapNode node = new HeapNode(key);
        if (this.isEmpty()){
            this.firstRoot = node;
            this.lastRoot = node;
            this.min = node;
        }
        else{
            // inserting the new node as the first one (to the left)
            this.firstRoot.prev = node; // connect as the prev of the fist node
            node.next = this.firstRoot; // connect the first node as its next
            this.firstRoot = node; // updating first
            // updating the min node if needed
            if(key < this.min.getKey())
                this.min = node;
        }
        // adding 1 to the size and to the number of trees
        this.trees++;
        this.size++;
    	return node;
    }


    /**
     * helper func - search and update new min and update it
     */
    private void searchAndUpdateNewMin() {
        HeapNode x = this.firstRoot;
        HeapNode minNode = x;
        if (!this.isEmpty()) {
            int minKey = x.getKey();
            // searching the min between the trees' roots
            while (x.next != null) {
                x = x.next;
                if (x.getKey() < minKey) {
                    minNode = x;
                    minKey = x.getKey();
                }
            }
        }
        this.min = minNode; //update the min node
    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
     	return; // should be replaced by student code
     	
    }

   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    *
    */
    public HeapNode findMin()
    {
    	return this.min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	  return; // should be replaced by student code   		
    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    *   
    */
    public int size()
    {
    	return this.size;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * (Note: The size of the array depends on the maximum order of a tree.)
    * 
    */
    public int[] countersRep() {
        // if the heap is empty - return empty array
        if (this.isEmpty()) {
            return new int[0];
        } else {
            int[] helpArr = new int[this.size()];
            int maxRank = -1;
            HeapNode x = this.firstRoot;
            while (x != null) {
                int rank = x.rank;
                helpArr[rank]++;
                // updating the maxRank - affect the length of the array that need to be returned
                if (rank > maxRank) {
                    maxRank = rank;
                }
                x = x.next;
            }
            // if the current length of the array is okay - return it
            if (maxRank == this.trees) {
                return helpArr;
            } else //else, creating and returning smaller array
            {
                int[] repCounter = new int[maxRank + 1];
                for (int i = 0; i < repCounter.length; ++i) {
                    repCounter[i] = helpArr[i];
                }
                return repCounter;
            }
        }
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    */
    public void delete(HeapNode x) 
    {    
    	return; // should be replaced by student code
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {
        x.key = x.key-delta;
        // if x is not the root (if it is the root - there wasn't violation
        // ot if there was a violation of the heap rule - doing cascading cut
        if(x.parent!= null && x.key < x.parent.key)
            cascadingCut(x.parent, x);
        // updating min node if needed
        if(x.key < this.min.key){
            this.min = x;
        }
    }

    /**
     * helper func - doing the cascading cut process
     */
    private void cascadingCut(HeapNode parent, HeapNode nodeToCut)
    {
        this.cut(parent,nodeToCut);
        // if parent isn't the root
        if (parent.parent != null){
            // if wasn't marked - mark it
            if(parent.mark == false)
                parent.mark = true;
            // if was marked - cut it from its parent
            else{
                cascadingCut(parent.parent, parent);
            }
        }
    }

    /**
    * helper func - doing the cut itself of nodeToCut from its parent
     */
    private void cut(HeapNode parent, HeapNode nodeToCut)
    {
        // disconnect from the parent
        nodeToCut.parent = null;
        // the node is not marked anymore (if it was)
        if (nodeToCut.mark == true){
            nodeToCut.mark = false;
            marked--;
        }
        // the parent lose one child - it is marked now and is rank is one down
        parent.rank--;

        // if nodeToCut == parent.child, its child is now nodeToCut.next
        if (nodeToCut == parent.child)
            parent.child = nodeToCut.next;
        // else, it means it as prev and next and we connect them
        else {
            nodeToCut.prev.next = nodeToCut.next;
            if(nodeToCut.next !=null)
                nodeToCut.next.prev = nodeToCut.prev;
            nodeToCut.prev = null;
        }
        // inserting the cut node to the start of the tree sequence
        nodeToCut.next = this.firstRoot;
        this.firstRoot.prev = nodeToCut;
        this.firstRoot = nodeToCut;
        trees++;

        // updating cut counter
        cuts++;
    }


   /**
    * public int nonMarked() 
    *
    * This function returns the current number of non-marked items in the heap
    */
    public int nonMarked() 
    {    
        return this.size - this.marked;
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
        return this.trees + 2 * this.marked;
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    */
    public static int totalLinks()
    {    
    	return links;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return cuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H. 
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {    
        int[] arr = new int[100];
        return arr; // should be replaced by student code
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{

    	public int key;
        public int rank;
        public boolean mark;
        public HeapNode child;
        public HeapNode next;
        public HeapNode prev;
        public HeapNode parent;

    	public HeapNode(int key) {
            this.key = key;
    		this.rank = 0;
            this.mark = false;
            this.child = null;
            this.next = null;
            this.prev = null;
            this.parent = null;
    	}

    	public int getKey() {
            return this.key;
    	}

        // delete before submitting
        public void printNode() {
            System.out.println("• Node's Key = " + this.key +", Rank = " +
                    this.rank + ", is marked? - " + this.mark);
        }
    }

    // delete before submitting
    public static void main(String[] args) {
        FibonacciHeap fib = new FibonacciHeap();
        HeapNode node1 = new HeapNode(4);
        HeapNode node2 = new HeapNode(5);
        HeapNode node3 = new HeapNode(7);
        fib.insert(2);
        fib.insert(3);
        fib.printFibHeap();
    }
}
