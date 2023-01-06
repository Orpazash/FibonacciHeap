/**
 * DON'T FORGET TO DELETE IMPORT - ONLY FOR PRINTING!
 */
package fibHeap;
import java.util.*;

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

    public static int links = 0;
    public static int cuts = 0;

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
            node.next = node;
            node.prev = node;
        }
        else{
            // inserting the new node as the first one (to the left)
            node.next = this.firstRoot; // connect the first node as its next
            node.prev = this.firstRoot.prev; // sets the first node's prev to the last node *OR added
            this.firstRoot.prev.next = node; // connect the first node to last's next  *OR added
            this.firstRoot.prev = node; // connect as the prev of the fist node
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
    public void deleteMin() {
        if (this.size == 0) {
            return;
        }
        this.size -= 1;

        if (this.size == 0) {  // if there is only one node in heap
            this.firstRoot = null;
            this.lastRoot = null;
            this.min = null;
            this.trees = 0;
            return;
        }

        if (min.next == min) {  //if the minimal node is the only one in the root list
            this.firstRoot = this.min.child;
            this.lastRoot = this.min.child.prev;
            this.min.child.parent = null;
            this.trees = this.min.rank;  // new trees as number of min's children
        }
        if (this.min.child == null) {  //if min node has no children
            min.next.prev = min.prev;
            min.prev.next = min.next;
            this.trees--;
        }
        else { // inserting min's children in his place
            this.min.child.prev.next = min.next;
            this.min.next.prev = min.child.prev;
            this.min.child.prev = min.prev;
            this.min.prev.next = min.child;
            this.trees += this.min.rank - 1;  // new trees as number of min's children minus the min itself
            HeapNode curr = this.min.child;
            do{
                curr.parent = null;
                curr = curr.next;
                if (curr.mark) {
                    marked--;
                    curr.mark = false;
                }
            } while (curr != this.min.child);
        }

        if (this.min == this.firstRoot) {
            if (this.min.child == null) {
            this.firstRoot = min.next;
            }
            else {
                this.firstRoot = this.min.child;
            }
        }
        if (this.min == this.lastRoot) {
            this.lastRoot = this.min.prev;
        }
        this.min = this.min.next;
        this.successiveLinking();

        this.searchAndUpdateNewMin();
    }
    /**
     * helper func - successive Linking after delete min
     */

    public void successiveLinking() {
        // new list the size of log(amount of trees) + 2, to cover computation, still O(log(n))
        HeapNode[] bucketList = new HeapNode[(int) Math.floor(Math.log((double) this.size)/ Math.log(2)) + 5];
        HeapNode curr = this.firstRoot;
        HeapNode curr_next;
        do {
            curr_next = curr.next;
            while (bucketList[curr.rank] != null) { // as long as there is a tree with same rank in list
                int rank = curr.rank;
                // linking the current tree with the one in the list, and saving the new one as current
                curr = link(curr,bucketList[rank]);
                // emptying the list at the index that was linked
                bucketList[rank] = null;
            }
            bucketList[curr.rank] = curr;
            curr = curr_next;
        } while (bucketList[curr.rank] != curr && curr.parent == null);

        // finding the first (also smallest) root in list, adding it to heap, storing the index of it as i
        int i;
        HeapNode prevNode = null;
        HeapNode currentNode = null;
        trees = 0;
        for ( i = 0; i<bucketList.length; i++) {
            if (bucketList[i] != null) {
                this.firstRoot = bucketList[i];
                prevNode = this.firstRoot;
                break;
            }
        }

        // going over the rest of the list (from i) and adding roots in order, if bucketList[j] is not null
        for (int j = i; j < bucketList.length; j++) {
            if (bucketList[j] != null) {
                currentNode = bucketList[j];
                prevNode.next = currentNode;
                currentNode.prev = prevNode;
                trees++;
                if (currentNode.mark) {
                    currentNode.mark = false;
                    this.marked--;
                }
            }
        prevNode = currentNode;
        }

        // adding the last node we saw as the last
        this.lastRoot = prevNode;
        prevNode.next = this.firstRoot;
        this.firstRoot.prev = prevNode;
    }

    /**
     * helper func - linking to trees, returning the root of the linked tree
     */
    public HeapNode link(HeapNode tree1, HeapNode tree2) {
        // if tree2's key is smaller than tree1's, switch
        if (tree1.getKey() > tree2.getKey()) {
            HeapNode temp = tree1;
            tree1 = tree2;
            tree2 = temp;
        }
        // removing tree2 from 'root list'
        tree2.prev.next = tree2.next;
        tree2.next.prev = tree2.prev;

        // linking tree2 as the new left son of tree1
        if (tree1.child != null) {  // if tree1 has children
            tree2.prev = tree1.child.prev;
            tree1.child.prev.next = tree2;
            tree1.child.prev = tree2;
            tree2.next = tree1.child;
            if(tree1.child.next == tree1.child) {
                tree1.child.next = tree2;
            }

        } else {  // if tree1 has no children
            tree2.next = tree2;
            tree2.prev = tree2;
        }
        tree1.child = tree2;
        tree2.parent = tree1;
        tree1.rank += 1;
        links++;

        return tree1;
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
    public void meld (FibonacciHeap heap2){
        if (heap2.isEmpty()) {
            return;
        }
        if (this.isEmpty()) {
            this.firstRoot = heap2.firstRoot;
            this.min = heap2.min;
        } else {
            this.lastRoot.next = heap2.firstRoot;
            heap2.firstRoot.prev = this.lastRoot;
            this.firstRoot.prev = heap2.lastRoot;
            heap2.lastRoot.next = this.firstRoot;
            if (this.min.getKey() > heap2.min.getKey()) {
                this.min = heap2.min;
            }
        }

        this.trees += heap2.trees;
        this.size += heap2.size;
        this.lastRoot = heap2.lastRoot;
        this.marked += heap2.marked;


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
        }

        int[] helpArr = new int[this.size() + 1];
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


        // creating and returning smaller array
        int[] repCounter = new int[maxRank + 1];
        for (int i = 0; i < repCounter.length; ++i) {
            repCounter[i] = helpArr[i];
        }
        return repCounter;
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
        if (this.min == x) {
            this.deleteMin();
            return;
        }

        int delta = x.getKey()-this.min.getKey()+1;
        if (this.min.getKey() == Integer.MIN_VALUE) {
            delta--;
        }


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
        if(x.key < this.min.key || this.min.getKey() == Integer.MIN_VALUE){
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
            if(!parent.mark) {
                parent.mark = true;
                marked++;
            }
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
        if (nodeToCut.mark){
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
        int[] arr = new int[k];
        FibonacciHeap kMinHeap = new FibonacciHeap();
        kMinHeap.insert(H.min.getKey()).originPlace = H.min;
        int[] index = new int[1];
        kMinRec(H, arr, kMinHeap,index);
        return arr; // should be replaced by student code
    }

    private static void kMinRec(FibonacciHeap H, int[] arr,
                                FibonacciHeap kMinHeap, int[] index){
        //if entered all the k keys to the array
        if (index[0] == arr.length)
            return;
        // value to enter to the list and to delete from the kMinHeap
        HeapNode currentNode = kMinHeap.min;
        int nextVal = currentNode.getKey();
        arr[index[0]] = nextVal;
        index[0]++;
        kMinHeap.deleteMin();
        // inserting its children (if it has children)
        FibonacciHeap.HeapNode child = currentNode.originPlace.child;
        if (child == null)
            return;
        FibonacciHeap.HeapNode start = child;
        do{
            nextVal = child.getKey();
            kMinHeap.insert(nextVal).originPlace = child;
            child = child.next;
        } while(child != start);
        // running recursively on the new kMinHeap and the updated arr and index
        kMinRec(H, arr, kMinHeap , index);
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
        public HeapNode originPlace;

    	public HeapNode(int key) {
            this.key = key;
    		this.rank = 0;
            this.mark = false;
            this.child = null;
            this.next = this;
            this.prev = this;
            this.parent = null;
            this.originPlace = null;
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
