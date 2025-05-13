package fifteenpuzzle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.*;


public class Solver {
	
	private static class Node implements Comparable<Node> {
        FifteenPuzzle state;
        Node prev;
        int moves;
        int priority;
        String direction;


        Node(FifteenPuzzle state, Node prev, int moves) {
            this.state = state;
            this.prev = prev;
            this.moves = moves;
            this.priority = state.sumDistance() + moves ;
        }
        
        Node(FifteenPuzzle state, Node prev, int moves, String direction) {
            this.state = state;
            this.prev = prev;
            this.moves = moves;
            this.priority = state.sumDistance() + moves ;
            this.direction = direction;
        }

        public int compareTo(Node that) {
            return Integer.compare(this.priority, that.priority);
        }
    }
	
	public class MinPQ<T extends Comparable<T>> { //minval priority queue
	    private T[] pq;   // heap-ordered complete binary tree
	    private int n;    // number of items in priority queue

	    public MinPQ() {
	        pq = (T[]) new Comparable[2];
	        n = 0;
	    }

	    public boolean isEmpty() {
	        return n == 0;
	    }

	    public int size() {
	        return n;
	    }

	    public void insert(T item) {
	        if (n == pq.length - 1) {
	            resize(2 * pq.length);
	        }
	        pq[++n] = item;
	        swim(n);
	    }

	    public T delMin() {
	        if (isEmpty()) {
	            throw new RuntimeException("Priority queue underflow");
	        }
	        T min = pq[1];
	        exch(1, n--);
	        sink(1);
	        pq[n+1] = null;
	        if ((n > 0) && (n == (pq.length - 1) / 4)) {
	            resize(pq.length / 2);
	        }
	        return min;
	    }

	    private void swim(int k) {
	        while (k > 1 && greater(k/2, k)) {
	            exch(k, k/2);
	            k = k/2;
	        }
	    }

	    private void sink(int k) {
	        while (2*k <= n) {
	            int j = 2*k;
	            if (j < n && greater(j, j+1)) {
	                j++;
	            }
	            if (!greater(k, j)) {
	                break;
	            }
	            exch(k, j);
	            k = j;
	        }
	    }

	    private boolean greater(int i, int j) {
	        return pq[i].compareTo(pq[j]) > 0;
	    }

	    private void exch(int i, int j) {
	        T swap = pq[i];
	        pq[i] = pq[j];
	        pq[j] = swap;
	    }

	    private void resize(int capacity) {
	        pq = Arrays.copyOf(pq, capacity);
	    }
	}


    private Node solutionNode;

    public Solver(FifteenPuzzle initial, String board) {
        MinPQ<Node> pq1 = new MinPQ<>();
        MinPQ<Node> pq2 = new MinPQ<>();
        pq1.insert(new Node(initial, null, 0));
        FifteenPuzzle twin = initial.twin();
        pq2.insert(new Node(twin, null, 0));

        while (true) {
            Node minNode1 = pq1.delMin();
            if (minNode1.state.isGoal()) {
                solutionNode = minNode1;
                break;
            }
            for (FifteenPuzzle neighbor : minNode1.state.neighbors()) {
                if (minNode1.prev == null || !neighbor.equals(minNode1.prev.state)) {
                    pq1.insert(new Node(neighbor, minNode1, minNode1.moves + 1));
                }
            }

            Node minNode2 = pq2.delMin();
            if (minNode2.state.isGoal()) {
                solutionNode = null;
                break;
            } 
            for (FifteenPuzzle neighbor : minNode2.state.neighbors()) {
                if (minNode2.prev == null || !neighbor.equals(minNode2.prev.state)) {
                    pq2.insert(new Node(neighbor, minNode2, minNode2.moves + 1));
                }
            }
        }
    }
    
    public Solver(FifteenPuzzle initial) {
        MinPQ<Node> pq1 = new MinPQ<>();
        MinPQ<Node> pq2 = new MinPQ<>();
        pq1.insert(new Node(initial, null, 0, null));
        FifteenPuzzle twin = initial.twin();
        pq2.insert(new Node(twin, null, 0, null));

        while (true) {
            Node minNode1 = pq1.delMin();
            if (minNode1.state.isGoal()) {
                solutionNode = minNode1;
                break;
            }
            for (FifteenPuzzle neighbor : minNode1.state.neighbors()) {
                if (minNode1.prev == null || !neighbor.equals(minNode1.prev.state)) {
                    String direction = getDirection(minNode1.state, neighbor);
                    pq1.insert(new Node(neighbor, minNode1, minNode1.moves + 1, direction));
                }
            }

            Node minNode2 = pq2.delMin();
            if (minNode2.state.isGoal()) {
                solutionNode = null;
                break;
            }
            for (FifteenPuzzle neighbor : minNode2.state.neighbors()) {
                if (minNode2.prev == null || !neighbor.equals(minNode2.prev.state)) {
                    String direction = getDirection(minNode2.state, neighbor);
                    pq2.insert(new Node(neighbor, minNode2, minNode2.moves + 1, direction));
                }
            }
        }
    }

    private static String getDirection(FifteenPuzzle now, FifteenPuzzle next) {
        int currEmptyRow = now.getEmptyTileRow();
        int currEmptyCol = now.getEmptyTileCol();
        int nextEmptyRow = next.getEmptyTileRow();
        int nextEmptyCol = next.getEmptyTileCol();

        if (currEmptyRow == nextEmptyRow && currEmptyCol < nextEmptyCol) {
            return "L";
        } else if (currEmptyRow == nextEmptyRow && currEmptyCol > nextEmptyCol) {
            return "R";
        } else if (currEmptyRow < nextEmptyRow && currEmptyCol == nextEmptyCol) {
            return "U";
        } else if (currEmptyRow > nextEmptyRow && currEmptyCol == nextEmptyCol) {
            return "D";
        } else {
            return null;
        }
    }



    public boolean isSolvable() {
        return solutionNode != null;
    }

    public int moves() {
        if (!isSolvable()) {
            return -1;
        }
        return solutionNode.moves;
    }

    public Iterable<FifteenPuzzle> solution() {
        if (!isSolvable()) {
            return null;
        }
        Stack<FifteenPuzzle> stack = new Stack<>();
        Node node = solutionNode;
        while (node != null) {
            stack.push(node.state);
            node = node.prev;
        }
        return stack;
    }
    
    public List<String> solutionDirection() {
        if (!isSolvable()) {
            return null;
        }
        List<String> solution = new ArrayList<>();
        Node node = solutionNode;
        while (node.prev != null) {
            int tile = node.state.getTileAt(node.prev.state.getEmptyTileRow(), node.prev.state.getEmptyTileCol());
            String direction = node.direction;
            solution.add(tile + " " + direction);
            node = node.prev;
        }
        Collections.reverse(solution);
        return solution;
    }
    
    private static void writeToFile(File outputFile, List<String> solution) throws IOException 
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        for (String move : solution) 
        {
            writer.write(move.toString() + System.lineSeparator());
        }
        writer.close();
    }
    
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("File names are not specified");
            System.out.println("usage: java " + MethodHandles.lookup().lookupClass().getName() + " input_file output_file");
            return;
        }
        
        File inputFile = new File(args[0]);
        File outputFile = new File(args[1]);

        try {
	        FifteenPuzzle game = new FifteenPuzzle(args[0]);
	        Solver solver = new Solver(game);
	        if (!solver.isSolvable()) 
	        {
	            System.out.println("No solution possible");
	        } 
	        else 
	        {
	            List<String> solution = solver.solutionDirection();
	                for (String move : solution) 
	                {
	                    writeToFile(outputFile,solution);
	                }
	        }
	    } catch (Exception e) { // catching all exception
	        System.out.println("SolverTest ERROR with exception: " + e);
	        e.printStackTrace();
	    }
    }
    
    
}
