package tests;
import fibHeap.FibonacciHeap;

public class FiboMeasurements {

    public static double log2(int n) {
        return Math.log(n) / Math.log(2);
    }

    public static double geomSum(double q, int maxI) {
        double sum = 0;
        for (int k = 1; k <= maxI; k++) {
            sum += Math.pow(q, k);
        }
        return sum;
    }

    public static void firstMeasurement() throws Exception {
        System.out.println("######################## Measurement 1 ########################");
        FibonacciHeap.HeapNode node;
        int nodeIndex;
        for (int pow = 5; pow <= 12; pow++) {
            int m = (int) Math.pow(2, pow);
            FibonacciHeap.cuts = 0;
            FibonacciHeap.links = 0;
            System.out.println("-----------------");
            System.out.println("run for m=" + m);
            FibonacciHeap.HeapNode[] nodes = new FibonacciHeap.HeapNode[m + 1];
            FibonacciHeap heap = new FibonacciHeap();
            long start = System.nanoTime();
            for (int j = m; j >= 0; j--) {
                node = heap.insert(j);
                nodes[j] = node;
            }
            heap.deleteMin();
            for (int j = 0; j <= log2(m) - 2; j++) {
                nodeIndex = (int) Math.floor(m * geomSum(0.5, j) + 2);
                heap.decreaseKey(nodes[nodeIndex], 150);
            }
            heap.decreaseKey(nodes[m - 1], 10);
            System.out.println("time ms: " + ((System.nanoTime() - start)) / 1000000.0);
            System.out.println("totalLinks=" + FibonacciHeap.totalLinks());
            System.out.println("totalCuts=" + FibonacciHeap.totalCuts());
            System.out.println("potential=" + heap.potential());
            Thread.sleep(1000);
        }
    }

    public static void secondMeasurement() {
        System.out.println("######################## Measurement 2 ########################");
        FibonacciHeap.HeapNode node;
        for (int m = 1000; m <= 3000; m += 1000) {
            FibonacciHeap.cuts= 0;
            FibonacciHeap.links = 0;
            System.out.println("-----------------");
            System.out.println("run for m=" + m);
            FibonacciHeap.HeapNode[] nodes = new FibonacciHeap.HeapNode[m];
            FibonacciHeap heap = new FibonacciHeap();
            long start = System.nanoTime();
            for (int j = m - 1; j >= 0; j--) {
                node = heap.insert(j);
                nodes[j] = node;
            }
            for (int j = 0; j < m / 2; j++) {
                heap.deleteMin();
            }
            System.out.println("time ms: " + ((System.nanoTime() - start) / 1000000.0));
            System.out.println("totalLinks=" + FibonacciHeap.totalLinks());
            System.out.println("totalCuts=" + FibonacciHeap.totalCuts());
            System.out.println("potential=" + heap.potential());
        }
    }

    public static void main(String[] args) throws Exception {
        firstMeasurement();
        secondMeasurement();
    }
}
