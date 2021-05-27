import org.junit.Test;

import static org.junit.Assert.*;

public class SudokuTest {

    @Test
    public void testToString() {
        var grid  = new Sudoku(Sudoku.easyGrid);
        var sudokuGrid = "1 6 4 0 0 0 0 0 2\n" +
                "2 0 0 4 0 3 9 1 0\n" +
                "0 0 5 0 8 0 4 0 7\n" +
                "0 9 0 0 0 6 5 0 0\n" +
                "5 0 0 1 0 2 0 0 8\n" +
                "0 0 8 9 0 0 0 3 0\n" +
                "8 0 9 0 4 0 2 0 0\n" +
                "0 7 3 5 0 9 0 0 1\n" +
                "4 0 0 0 0 0 6 7 9\n";
        assertEquals(sudokuGrid, grid.toString());
        grid.solve();
        assertEquals(sudokuGrid, grid.toString());
    }

    @Test
    public void testSolve() {
        var grid = new Sudoku(Sudoku.hardGrid);
        assertEquals(1, grid.solve());
        assertNotEquals(0, grid.getElapsed());
        grid = new Sudoku(Sudoku.stringsToGrid(
                "3 0 0 0 0 0 0 8 0",
                "0 0 1 0 9 3 0 0 0",
                "0 4 0 7 8 0 0 0 3",
                "0 9 3 8 0 0 0 1 2",
                "0 0 0 0 4 0 0 0 0",
                "5 2 0 0 0 6 7 9 0",
                "6 0 0 0 2 1 0 4 0",
                "0 0 0 5 3 0 9 0 0",
                "0 3 0 0 0 0 0 5 1"));
        assertEquals(6, grid.solve());
    }
}