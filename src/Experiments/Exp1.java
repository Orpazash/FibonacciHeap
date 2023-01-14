package Experiments;
import fibHeap.FibonacciHeap;

public class Exp1 {
    public static void testForSmallM(){
        for(int j = 1; j<5; j++){
            FibonacciHeap fib = new FibonacciHeap();
            long start = System.nanoTime(); //System.currentTimeMillis();
            int m  = (int) Math.pow(2,j);
            FibonacciHeap.HeapNode[] nodesArray = new FibonacciHeap.HeapNode[m+1];
            for (int k =m-1; k > -2; k--){
                nodesArray[k+1]=fib.insert(k);
            }
            fib.deleteMin();
            for (int i = m; i>=2; i/=2) {
                fib.decreaseKey(nodesArray[m - i + 2], m + 1);
            }
            long end = System.nanoTime(); //System.currentTimeMillis();
            System.out.print((float) (end-start)/1000000);
            System.out.print(", Links = " + FibonacciHeap.totalLinks());
            System.out.print(", Cuts = " + FibonacciHeap.totalCuts());
            System.out.println(", Potential = " + fib.potential());
            FibonacciHeap.cuts = 0;
            FibonacciHeap.links = 0;
        }
    }
    public static void SectionB(){
        System.out.println("Section B:");
        for(int j = 5; j<21; j+=5){
            FibonacciHeap fib = new FibonacciHeap();
            long start = System.nanoTime(); //System.currentTimeMillis();
            int m  = (int) Math.pow(2,j);
            FibonacciHeap.HeapNode[] nodesArray = new FibonacciHeap.HeapNode[m+1];
            for (int k =m-1; k > -2; k--){
                nodesArray[k+1]=fib.insert(k);
            }
            fib.deleteMin();
            for (int i = m; i>=2; i/=2) {
                fib.decreaseKey(nodesArray[m - i + 2], m + 1);
            }
            long end = System.nanoTime(); //System.currentTimeMillis();
            System.out.print((float) (end-start)/1000000);
            System.out.print(", Links = " + FibonacciHeap.totalLinks());
            System.out.print(", Cuts = " + FibonacciHeap.totalCuts());
            System.out.println(", Potential = " + fib.potential());
            FibonacciHeap.cuts = 0;
            FibonacciHeap.links = 0;
        }
    }

    public static void SectionD(){
        System.out.println("Section D:");
        for(int j = 5; j<21; j+=5){
            FibonacciHeap fib = new FibonacciHeap();
            int m  = (int) Math.pow(2,j);
            FibonacciHeap.HeapNode[] nodesArray = new FibonacciHeap.HeapNode[m+1];
            for (int k =m-1; k > -2; k--){
                nodesArray[k+1]=fib.insert(k);
            }
            fib.deleteMin();
            for (int i = m; i>=2; i/=2) {
                fib.decreaseKey(nodesArray[m - i + 1], m + 1);
            }
            System.out.print("Links = " + FibonacciHeap.totalLinks());
            System.out.print(", Cuts = " + FibonacciHeap.totalCuts());
            System.out.println(", Potential = " + fib.potential());
            FibonacciHeap.cuts = 0;
            FibonacciHeap.links = 0;
        }
    }

    public static void SectionE(){
        System.out.println("Section E:");
        for(int j = 5; j<21; j+=5){
            FibonacciHeap fib = new FibonacciHeap();
            int m  = (int) Math.pow(2,j);
            FibonacciHeap.HeapNode[] nodesArray = new FibonacciHeap.HeapNode[m+1];
            for (int k =m-1; k > -2; k--){
                nodesArray[k+1]=fib.insert(k);
            }
            for (int i = m; i>=2; i/=2) {
                fib.decreaseKey(nodesArray[m - i + 2], m + 1);
            }
            System.out.print("Links = " + FibonacciHeap.totalLinks());
            System.out.print(", Cuts = " + FibonacciHeap.totalCuts());
            System.out.println(", Potential = " + fib.potential());
            FibonacciHeap.cuts = 0;
            FibonacciHeap.links = 0;
        }
    }

    public static void SectionF(){
        System.out.println("Section F:");
        for(int j = 5; j<21; j+=5){
            FibonacciHeap fib = new FibonacciHeap();
            int m  = (int) Math.pow(2,j);
            FibonacciHeap.HeapNode[] nodesArray = new FibonacciHeap.HeapNode[m+1];
            for (int k =m-1; k > -2; k--){
                nodesArray[k+1]=fib.insert(k);
            }
            fib.deleteMin();
            for (int i = m; i>=2; i/=2) {
                fib.decreaseKey(nodesArray[m - i + 2], m + 1);
            }
            fib.decreaseKey(nodesArray[m-1], m+1);
            System.out.print("Links = " + FibonacciHeap.totalLinks());
            System.out.print(", Cuts = " + FibonacciHeap.totalCuts());
            System.out.println(", Potential = " + fib.potential());
            FibonacciHeap.cuts = 0;
            FibonacciHeap.links = 0;
        }
    }

    public static void main(String[] args) {
        // Check for understanding
        testForSmallM();
        System.out.println("~~~~~~~~~~~~~~");
        // Real Tests
        SectionB();
        System.out.println("~~~~~~~~~~~~~~");
        SectionD();
        System.out.println("~~~~~~~~~~~~~~");
        SectionE();
        System.out.println("~~~~~~~~~~~~~~");
        SectionF();
    }

}
