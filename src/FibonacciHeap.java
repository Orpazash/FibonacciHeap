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
    public HeapNode min;
    public HeapNode firstRoot;
    public HeapNode lastRoot;
    public int size;
    public int marked;
    public int trees;

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
            this.firstRoot.prev = node; // connect as the prev of the first node
            node.next = this.firstRoot; // connect the first node as its next
            node.prev = this.lastRoot; // sets the first node's prev to the last node
            this.lastRoot.next = node; // connect the first node to last's next
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
     * helper func - search and update new min
     */
    private void searchAndUpdateNewMin() {
        HeapNode x = this.firstRoot;
        HeapNode minNode = x;
        if (!this.isEmpty()) {
            int minKey = x.getKey();
            // searching the min between the trees' roots
            do {
                if (x.getKey() < minKey) {
                    minNode = x;
                    minKey = x.getKey();
                }
            x = x.next;
            } while (x != this.firstRoot);
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
            int[] helpArr = new int[this.size()]; //will work when size will be updated in delete min!
            int maxRank = -1;
            HeapNode x = this.firstRoot;
            do {
                int rank = x.rank;
                helpArr[rank]++;
                // updating the maxRank - affect the length of the array that need to be returned
                if (rank > maxRank) {
                    maxRank = rank;
                }
                x = x.next;
            } while (x != this.firstRoot);
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
    	int delta = x.getKey()-this.min.getKey()+1;
        this.decreaseKey(x,delta);
        this.deleteMin();
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

        // if nodeToCut == parent.child
        if (nodeToCut == parent.child)
            // check if it has other children
            if (nodeToCut.next == nodeToCut)
                parent.child = null;
            else
                parent.child = nodeToCut.next;

        if (nodeToCut.next == nodeToCut)
            nodeToCut.prev.next = nodeToCut.prev;
        else {
            nodeToCut.prev.next = nodeToCut.next;
            nodeToCut.next.prev = nodeToCut.prev;
        }
        // inserting the cut node to the start of the tree sequence
        //connection as prev to first root
        nodeToCut.next = this.firstRoot;
        this.firstRoot.prev = nodeToCut;
        //connecting as the next the last root
        this.lastRoot.next = nodeToCut;
        nodeToCut.prev = this.lastRoot;
        //updating as the first
        this.firstRoot = nodeToCut;

        // updating trees counter and cuts counter
        trees++;
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
            this.next = this;
            this.prev = this;
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

}
