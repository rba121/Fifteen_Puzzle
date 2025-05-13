package fifteenpuzzle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FifteenPuzzle {
	public final static int UP = 0;
	public final static int DOWN = 1;
	public final static int LEFT = 2;
	public final static int RIGHT = 3;

	public int SIZE;

	int board[][];

	private void checkBoard() throws BadBoardException {
		int[] vals = new int[SIZE * SIZE];

		// check that the board contains all number 0...15
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (board[i][j]<0 || board[i][j]>=SIZE*SIZE)
					throw new BadBoardException("found tile " + board[i][j]);
				vals[board[i][j]] += 1;
			}
		}

		for (int i = 0; i < vals.length; i++)
			if (vals[i] != 1)
				throw new BadBoardException("tile " + i +
											" appears " + vals[i] + "");

	}

	/**
	 * @param fileName
	 * @throws FileNotFoundException if file not found
	 * @throws BadBoardException     if the board is incorrectly formatted Reads a
	 *                               board from file and creates the board
	 */
	public FifteenPuzzle(String fileName) throws IOException, BadBoardException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));

		SIZE = Integer.parseInt(br.readLine());
		board = new int[SIZE][SIZE];
		int c1, c2, s;
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				c1 = br.read();
				c2 = br.read();
				s = br.read(); // skip the space
				if (s != ' ' && s != '\n') {
					br.close();
					throw new BadBoardException("error in line " + i);
				}
				if (c1 == ' ')
					c1 = '0';
				if (c2 == ' ')
					c2 = '0';
				board[i][j] = 10 * (c1 - '0') + (c2 - '0');
			}
		}
		checkBoard();

		br.close();
	}
	
	public FifteenPuzzle(int[][] newBoard) {
	    if (newBoard == null || newBoard.length != newBoard[0].length) {
	        throw new IllegalArgumentException("Invalid puzzle dimensions");
	    }
	    SIZE = newBoard.length;
	    board = new int[SIZE][SIZE];
	    for (int i = 0; i < SIZE; i++) {
	        for (int j = 0; j < SIZE; j++) {
	            board[i][j] = newBoard[i][j];
	        }
	    }
	}
	
	 public FifteenPuzzle twin() {
	        int[][] twinBoard = new int[board.length][board[0].length];
	        for (int i = 0; i < board.length; i++) {
	            for (int j = 0; j < board[0].length; j++) {
	                twinBoard[i][j] = board[i][j];
	            }
	        }
	        if (twinBoard[0][0] != 0 && twinBoard[0][1] != 0) {
	            int temp = twinBoard[0][0];
	            twinBoard[0][0] = twinBoard[0][1];
	            twinBoard[0][1] = temp;
	        } else {
	            int temp = twinBoard[1][0];
	            twinBoard[1][0] = twinBoard[1][1];
	            twinBoard[1][1] = temp;
	        }
	        return new FifteenPuzzle(twinBoard);
	    }

	public class Pair {
		int i, j;

		Pair(int i, int j) {
			this.i = i;
			this.j = j;
		}
	}

	public Pair findCoord(int tile) {
		int i = 0, j = 0;
		for (i = 0; i < SIZE; i++)
			for (j = 0; j < SIZE; j++)
				if (board[i][j] == tile)
					return new Pair(i, j);
		return null;
	}

	/**
	 * Get the number of the tile, and moves it to the specified direction
	 * 
	 * @throws IllegalMoveException if the move is illegal
	 */
	public void makeMove(int tile, int direction) throws IllegalMoveException {
		Pair p = findCoord(tile);
		if (p == null)
			throw new IllegalMoveException("tile " + tile + " not found");
		int i = p.i;
		int j = p.j;

		// the tile is in position [i][j]
		switch (direction) {
		case UP: {
			if (i > 0 && board[i - 1][j] == 0) {
				board[i - 1][j] = tile;
				board[i][j] = 0;
				break;
			} else
				throw new IllegalMoveException("" + tile + "cannot move UP");
		}
		case DOWN: {
			if (i < SIZE - 1 && board[i + 1][j] == 0) {
				board[i + 1][j] = tile;
				board[i][j] = 0;
				break;
			} else
				throw new IllegalMoveException("" + tile + "cannot move DOWN");
		}
		case RIGHT: {
			if (j < SIZE - 1 && board[i][j + 1] == 0) {
				board[i][j + 1] = tile;
				board[i][j] = 0;
				break;
			} else
				throw new IllegalMoveException("" + tile + "cannot move LEFT");
		}
		case LEFT: {
			if (j > 0 && board[i][j - 1] == 0) {
				board[i][j - 1] = tile;
				board[i][j] = 0;
				break;
			} else
				throw new IllegalMoveException("" + tile + "cannot move LEFT");
		}
		default:
			throw new IllegalMoveException("Unexpected direction: " + direction);
		}

	}
	
	public void swap(int i1, int j1, int i2, int j2) {
	    int temp = board[i1][j1];
	    board[i1][j1] = board[i2][j2];
	    board[i2][j2] = temp;
	}

	/**
	 * @return true if and only if the board is solved, i.e., the board has all
	 *         tiles in their correct positions
	 */
	public boolean isSolved() {
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				if (board[i][j] != (4 * i + j + 1) % (16))
					return false;
		return true;
	}
	
	public Pair getGoalPosition(int tile) {
		int row = (tile - 1) / SIZE;
        int col = (tile - 1) % SIZE;
        return new Pair(row, col);
	}
	
	public int sumDistance() {
		  int distance = 0;
		    int misplacedTiles = 0;
		    for (int i = 0; i < SIZE; i++) {
		        for (int j = 0; j < SIZE; j++) {
		            int tile = board[i][j];
		            if (tile == 0) continue; // skip the empty tile
		            Pair goalPos = getGoalPosition(tile);
		            int goalRow = goalPos.i;
		            int goalCol = goalPos.j;
		            int dx = Math.abs(j - goalCol);
		            int dy = Math.abs(i - goalRow);
		            distance += dx + dy;
		            if (i != goalRow || j != goalCol) {
		                misplacedTiles++;
		            }
		        }
		    }
		    return distance+ misplacedTiles;
	}
	
	public boolean isGoal() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int tile = board[i][j];
                if (tile != 0) {
                    Pair goalPos = getGoalPosition(tile);
                    if (goalPos.i != i || goalPos.j != j) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
	
	public List<FifteenPuzzle> neighbors() {
	    List<FifteenPuzzle> neighbors = new ArrayList<>();

	    // find the empty tile and generate neighbors
	    for (int i = 0; i < SIZE; i++) {
	        for (int j = 0; j < SIZE; j++) {
	            if (board[i][j] == 0) {
	                if (i > 0) {
	                    FifteenPuzzle neighbor = new FifteenPuzzle(board);
	                    neighbor.swap(i, j, i-1, j);
	                    neighbors.add(neighbor);
	                }
	                if (i < SIZE-1) {
	                    FifteenPuzzle neighbor = new FifteenPuzzle(board);
	                    neighbor.swap(i, j, i+1, j);
	                    neighbors.add(neighbor);
	                }
	                if (j > 0) {
	                    FifteenPuzzle neighbor = new FifteenPuzzle(board);
	                    neighbor.swap(i, j, i, j-1);
	                    neighbors.add(neighbor);
	                }
	                if (j < SIZE-1) {
	                    FifteenPuzzle neighbor = new FifteenPuzzle(board);
	                    neighbor.swap(i, j, i, j+1);
	                    neighbors.add(neighbor);
	                }
	                break;
	            }
	        }
	    }

	    return neighbors;
	}

	private String num2str(int i) {
		if (i == 0)
			return "  ";
		else if (i < 10)
			return " " + Integer.toString(i);
		else
			return Integer.toString(i);
	}

	public String toString() {
		String ans = "";
		for (int i = 0; i < SIZE; i++) {
			ans += num2str(board[i][0]);
			for (int j = 1; j < SIZE; j++)
				ans += " " + num2str(board[i][j]);
			ans += "\n";
		}
		return ans;
	}

	public int getTileAt(int row, int col) {
	    if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
	        throw new IllegalArgumentException("Invalid row or column");
	    }
	    return board[row][col];
	}
	
	public int getEmptyTileRow() {
	    for (int i = 0; i < SIZE; i++) {
	        for (int j = 0; j < SIZE; j++) {
	            if (board[i][j] == 0) {
	                return i;
	            }
	        }
	    }
	    return -1;
	}

	
	public int getEmptyTileCol() {
	    for (int i = 0; i < SIZE; i++) {
	        for (int j = 0; j < SIZE; j++) {
	            if (board[i][j] == 0) {
	                return j;
	            }
	        }
	    }
	    return -1;
	}

}
