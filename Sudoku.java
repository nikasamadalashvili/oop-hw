import java.awt.*;
import java.util.*;

/*
 * Encapsulates a Sudoku grid to be solved.
 * CS108 Stanford.
 */
public class Sudoku {
	private final int[][] sudokuGrid;
	private ArrayList<Spot> fillableSpots;
	private ArrayList<HashSet<Integer>> columnsState;
	private ArrayList<HashSet<Integer>> rowsState;
	private ArrayList<HashSet<Integer>> squaresState;
	private long elapsedTime;
	private int solutionQuantity;
	private ArrayList<String> solutionList;

	// Provided grid data for main/testing
	// The instance variable strategy is up to you.
	
	// Provided easy 1 6 grid
	// (can paste this text into the GUI too)
	public static final int[][] easyGrid = Sudoku.stringsToGrid(
	"1 6 4 0 0 0 0 0 2",
	"2 0 0 4 0 3 9 1 0",
	"0 0 5 0 8 0 4 0 7",
	"0 9 0 0 0 6 5 0 0",
	"5 0 0 1 0 2 0 0 8",
	"0 0 8 9 0 0 0 3 0",
	"8 0 9 0 4 0 2 0 0",
	"0 7 3 5 0 9 0 0 1",
	"4 0 0 0 0 0 6 7 9");
	
	
	// Provided medium 5 3 grid
	public static final int[][] mediumGrid = Sudoku.stringsToGrid(
	 "530070000",
	 "600195000",
	 "098000060",
	 "800060003",
	 "400803001",
	 "700020006",
	 "060000280",
	 "000419005",
	 "000080079");
	
	// Provided hard 3 7 grid
	// 1 solution this way, 6 solutions if the 7 is changed to 0
	public static final int[][] hardGrid = Sudoku.stringsToGrid(
	"3 7 0 0 0 0 0 8 0",
	"0 0 1 0 9 3 0 0 0",
	"0 4 0 7 8 0 0 0 3",
	"0 9 3 8 0 0 0 1 2",
	"0 0 0 0 4 0 0 0 0",
	"5 2 0 0 0 6 7 9 0",
	"6 0 0 0 2 1 0 4 0",
	"0 0 0 5 3 0 9 0 0",
	"0 3 0 0 0 0 0 5 1");
	
	
	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;
	
	// Provided various static utility methods to
	// convert data formats to int[][] grid.
	
	/**
	 * Returns a 2-d grid parsed from strings, one string per row.
	 * The "..." is a Java 5 feature that essentially
	 * makes "rows" a String[] array.
	 * (provided utility)
	 * @param rows array of row strings
	 * @return grid
	 */
	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row<rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
	}
	
	
	/**
	 * Given a single string containing 81 numbers, returns a 9x9 grid.
	 * Skips all the non-numbers in the text.
	 * (provided utility)
	 * @param text string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		int[] nums = stringToInts(text);
		if (nums.length != SIZE*SIZE) {
			throw new RuntimeException("Needed 81 numbers, but got:" + nums.length);
		}
		
		int[][] result = new int[SIZE][SIZE];
		int count = 0;
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				result[row][col] = nums[count];
				count++;
			}
		}
		return result;
	}
	
	
	/**
	 * Given a string containing digits, like "1 23 4",
	 * returns an int[] of those digits {1 2 3 4}.
	 * (provided utility)
	 * @param string string containing ints
	 * @return array of ints
	 */
	public static int[] stringToInts(String string) {
		int[] a = new int[string.length()];
		int found = 0;
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				a[found] = Integer.parseInt(string.substring(i, i+1));
				found++;
			}
		}
		int[] result = new int[found];
		System.arraycopy(a, 0, result, 0, found);
		return result;
	}


	// Provided -- the deliverable main().
	// You can edit to do easier cases, but turn in
	// solving hardGrid.
	public static void main(String[] args) {
		Sudoku sudoku;
		sudoku = new Sudoku(hardGrid);
		
		System.out.println(sudoku); // print the raw problem
		int count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
	}
	
	
	

	/**
	 * Sets up based on the given ints.
	 */
	public Sudoku(int[][] ints) {
		// YOUR CODE HERE
		elapsedTime = 0;
		solutionQuantity = -1;
		sudokuGrid = new int[SIZE][SIZE];
		solutionList = new ArrayList<>();
		initStateArrayLists();
		System.arraycopy(ints, 0, sudokuGrid, 0, SIZE);
		for (int i = 0; i < SIZE; i++) {
			System.arraycopy(ints[i], 0, sudokuGrid[i], 0, SIZE);
			var currRow = rowsState.get(i);
			for (int j = 0; j < SIZE; j++) {
				if (sudokuGrid[i][j] != 0) {
					currRow.add(sudokuGrid[i][j]);
					columnsState.get(j).add(sudokuGrid[i][j]);
					var currentSquareIndex = convertSudokuIndexToSquareIndex(j, i);
					squaresState.get(currentSquareIndex).add(sudokuGrid[i][j]);
				}
			}
		}
		initSpots();
	}

	private int convertSudokuIndexToSquareIndex(int x, int y) {
		int row = y/3;
		row *= 3;
		int col = x/3;
		return row + col;
	}

	private void initStateArrayLists(){
		columnsState = new ArrayList<>(SIZE);
		rowsState = new ArrayList<>(SIZE);
		squaresState = new ArrayList<>(SIZE);
		for (int i = 0; i < SIZE; i++) {
			columnsState.add(new HashSet());
			rowsState.add(new HashSet());
			squaresState.add(new HashSet());
		}
	}

	private void initSpots() {
		fillableSpots = new ArrayList<>();
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (sudokuGrid[i][j] == 0) {
					var newSpot = new Spot(j, i, sudokuGrid[i][j], getSpotAvailableQuantity(j,i));
					fillableSpots.add(newSpot);
				}
			}
		}
	}

	private int getSpotAvailableQuantity(int x, int y) {
		int result = 0;
		var rowSet = rowsState.get(y);
		var columnSet =columnsState.get(x);
		for (int i = 1; i <= SIZE; i++) {
			if (!rowSet.contains(i) && ! columnSet.contains(i))
				result++;
		}
		return result;
	}
	
	
	
	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 */
	public int solve() {
		if (solutionQuantity == -1) {
			var start = System.currentTimeMillis();
			Collections.sort(fillableSpots);
			solutionQuantity = solveRecursively(0);
			var end = System.currentTimeMillis();
			elapsedTime = end - start;
		}
		return solutionQuantity; // YOUR CODE HERE
	}

	private int solveRecursively(int startIndex) {
		if (startIndex == fillableSpots.size()) {
			solutionList.add(toString());
			return 1;
		}
		var currentSpot = fillableSpots.get(startIndex);
		var columnElems = columnsState.get(currentSpot.getX());
		var rowElems = rowsState.get(currentSpot.getY());
		var squareElems = squaresState.get(convertSudokuIndexToSquareIndex(currentSpot.getX(), currentSpot.getY()));
		var result = 0;
		for (int i = 1; i <= SIZE; i++) {
			if (!columnElems.contains(i) && !rowElems.contains(i) && !squareElems.contains(i) && solutionList.size() < MAX_SOLUTIONS) {
				columnElems.add(i);
				rowElems.add(i);
				squareElems.add(i);
				currentSpot.setValue(i);
				var quantity = solveRecursively(startIndex + 1);
				result += quantity;
				columnElems.remove(i);
				rowElems.remove(i);
				squareElems.remove(i);
				currentSpot.setValue(0);
			}
		}
		return result;
	}
	
	public String getSolutionText() {
		return solutionList.size() > 0 ? solutionList.stream().findFirst().get() : ""; // YOUR CODE HERE
	}
	
	public long getElapsed() {
		return elapsedTime; // YOUR CODE HERE
	}

	@Override
	public String toString() {
		var stringBuilder = new StringBuilder();
		var rowSizeWithSpacesAndDelimiter = 2 * SIZE;
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				stringBuilder.append(sudokuGrid[i][j]);
				stringBuilder.append(' ');
			}
			stringBuilder.setCharAt((i * rowSizeWithSpacesAndDelimiter) + (rowSizeWithSpacesAndDelimiter - 1), '\n');
		}

		return stringBuilder.toString();
	}

	public class Spot implements Comparable {
		private int x;
		private int y;
		private int value;
		private int assignableSize;

		public Spot(int x, int y, int value, int assignableSize) {

			this.x = x;
			this.y = y;
			this.value = value;
			this.assignableSize = assignableSize;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			sudokuGrid[y][x] = value;
			this.value = value;
		}

		public int getAssignableSize() {
			return assignableSize;
		}

		public void setAssignableSize(int assignableSize) {
			this.assignableSize = assignableSize;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Spot spot = (Spot) o;
			return x == spot.x && y == spot.y;
		}

		@Override
		public int hashCode() {
			return Objects.hash(x, y);
		}

		@Override
		public int compareTo(Object o) {
			return this.value - ((Spot) o).value;
		}
	}
}
