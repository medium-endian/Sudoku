package sudoku.game;

import java.util.Arrays;
import java.util.Observable;

/**
 * Created by ra on 23.06.16.
 * Part of Sudoku, in package sudoku.game.solver.
 */
public class SudokuBoard extends Observable {
    /* Using simple 2-d array. Arrays can hold primitive types, collections cannot.
    Arrays have O(1) indexing and changing values. Size of board is constant.
    */
    private static final int SIZE = 9;

    private final int[][] board = new int[SIZE][SIZE]; // all arrays initialized with 0
    private final int[][] initial;

    public SudokuBoard(int[][] initialState) {
        if (initialState.length == SIZE && initialState[0].length == SIZE && correctRange(initialState)) {
            initial = initialState;
            for (int rowIndex = 0; rowIndex < SIZE; rowIndex++) { // initialize board as well
                //for (int colIndex = 0; colIndex < SIZE; colIndex++) {
                //    board[rowIndex][colIndex] = initial[rowIndex][colIndex];
                System.arraycopy(initial[rowIndex], 0, board[rowIndex], 0, SIZE);
            }
        } else {
            initial = new int[SIZE][SIZE]; // board can stay at 0
        }
        assert isPerfectSquare(SIZE);
    }

    private static boolean correctRange(int[][] array) {
        return Arrays.stream(array).allMatch(
                integers -> Arrays.stream(integers).allMatch(
                        i -> i >= 0 && i <= SIZE));
    }

    public int getValue(int rowIndex, int colIndex) {
        if (rowIndex >= 0 && rowIndex < SIZE &&
                colIndex >= 0 && rowIndex < SIZE) {
            return board[rowIndex][colIndex];
        }
        System.err.println("Invalid access!");
        return 0;
    }

    private void clear() {
        for (int rowIndex = 0; rowIndex < SIZE; rowIndex++) {
            for (int colIndex = 0; colIndex < SIZE; colIndex++) {
                setValue(rowIndex, colIndex, 0); // only writes to board, not initial state
            }
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Set a value in the grid and return true if arguments valid, false otherwise.
     *
     * @param rowIndex valid board index
     * @param colIndex valid board index
     * @param valIndex valid number between 1 and (including) SIZE.
     * @return false if invalid args, true otherwise.
     */
    public boolean setValue(int rowIndex, int colIndex, int valIndex) {
        if (rowIndex < SIZE && rowIndex >= 0 &&
                colIndex < SIZE && colIndex >= 0 &&
                valIndex <= SIZE && valIndex > 0) { // valid arguments?

            if (initial[rowIndex][colIndex] != 0) {
                System.err.println("write on initial position!");
                return false;
            }
            board[rowIndex][colIndex] = valIndex;
            setChanged();
            notifyObservers();
            return true;
        } else {
            return false;
        }
    }

    int[] getRow(int rowIndex) {
        int[] row = new int[SIZE];
        for (int colIndex = 0; colIndex < SIZE; colIndex++) {
            row[rowIndex] = board[rowIndex][colIndex];
        }
        return row;
    }

    int[] getColumn(int colIndex) {
        int[] column = new int[SIZE];
        for (int rowIndex = 0; rowIndex < SIZE; rowIndex++) {
            column[rowIndex] = board[rowIndex][colIndex];
        }
        return column;
    }

    /**
     * Returns if the specified column contains the specified value.
     *
     * @param colIndex 0..SIZE-1
     * @return true if column at colIndex contains value.
     */
    public boolean columnContains(final int colIndex, final int value) {
        for (int rowIndex = 0; rowIndex < SIZE; rowIndex++) {
            if (board[rowIndex][colIndex] == value)
                return true;
        }
        return false;
    }

    /**
     * Returns if the specified row contains the specified value.
     *
     * @param rowIndex 0..SIZE-1
     * @return true if row at rowIndex contains value.
     */
    public boolean rowContains(final int rowIndex, final int value) {
        for (int colIndex = 0; colIndex < SIZE; colIndex++) {
            if (board[rowIndex][colIndex] == value)
                return true;
        }
        return false;
    }

    /**
     * Returns if the specified square contains the given value.
     *
     * @param rowIndex index of the row the square starts on divided by sqrt(SIZE)
     *                 I.e., to get to the middle row in a sudoku of 9,
     *                 rowIndex would be 1
     * @param colIndex index of the column the square starts on divided by sqrt(SIZE)
     *                 I.e., to get to the first column in a sudoku of 9,
     *                 colIndex would be 0
     * @return true if value present in specified square.
     */
    boolean squareContains(final int rowIndex, final int colIndex, final int value)  {
        if (rowIndex * rowIndex > SIZE || colIndex * colIndex > SIZE) {
            throw new IllegalArgumentException();
        }
        int blockSize = (int) Math.sqrt(SIZE);
        for (int row = rowIndex * blockSize; row < rowIndex * blockSize + blockSize; row++) {
            for (int col = colIndex * blockSize; col < colIndex * blockSize + blockSize; col++) {
                if (board[row][col] == value) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isPerfectSquare(int number) {
        if (number > 0) { // 0 is explicitly excluded
            int temp = (int) (Math.sqrt(number));
            return temp * temp == number;
        } else {
            return false;
        }
    }

    public int getSize() {
        return SIZE;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int rowIndex = 0; rowIndex < SIZE; rowIndex++) {
            for (int colIndex = 0; colIndex < SIZE - 1; colIndex++) { // all but last, ';' each
                stringBuilder.append(board[rowIndex][colIndex]).append(";");
            }
            stringBuilder.append(board[rowIndex][SIZE - 1]).append('\n'); // no ';' for last
        }
        return stringBuilder.toString();
    }
}