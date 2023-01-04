package tests;
import fibHeap.FibonacciHeap;

import java.util.TreeSet;

class Tester2HEAP {
    private TreeSet<Integer> set;

    Tester2HEAP() {
        this.set = new TreeSet<Integer>();
    }

    public int size() {
        return this.set.size();
    }

    public boolean empty() {
        return this.set.isEmpty();
    }

    public void insert(int v) {
        this.set.add((Integer)v);
    }



    public int deleteMin() {
        int min = this.set.first();
        this.set.remove(this.set.first());
        return min;
    }

    public int findMin() {
        if (this.empty())
            return -1;
        return (int)(this.set.first());
    }

    public void delete(int i) {
        this.set.remove((Integer)i);

    }
}
