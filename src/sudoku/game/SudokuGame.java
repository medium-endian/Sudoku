package sudoku.game;

import sudoku.controller.Message;
import sudoku.controller.Move;
import sudoku.game.solver.BacktrackSolver;
import sudoku.game.solver.LogicSolver;

import java.util.Observable;

import static sudoku.controller.Message.*;
import static sudoku.game.SudokuUtil.isPerfectSquare;

/**
 * The game of Sudoku.
 * This class provides a model of a sudoku game. The class can be constructed of an initial board.
 * The entries of the initial board are immutable.
 * The board array always keeps all entries, even the ones from the initial board, but they are protected.
 * Since the class inherits from Observable, observers can be added to it.
 * They are notified whenever an entry in the backing array changes.
 */
public class SudokuGame extends Observable {
    /* Using simple 2-d array. Arrays can hold primitive types, collections cannot.
    Arrays have O(1) indexing and changing values. Size of board is constant.
    */
    private final int SIZE;

    // all arrays initialized with 0
    private final int[][] board;
    private final int[][] initial;

    public SudokuGame(int[][] initial) throws IllegalArgumentException {
        if (isPerfectSquare(initial.length) &&
                initial.length == initial[0].length) {
            SIZE = initial.length;
            this.board = new int[SIZE][SIZE];
            this.initial = new int[SIZE][SIZE];
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    this.board[row][col] = 0;
                    this.initial[row][col] = 0;
                    if (initial[row][col] != 0) {
                        this.initial[row][col] = initial[row][col];
                        this.board[row][col] = initial[row][col];
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Not a valid initial Array!");
        }
    }

    public boolean isInitial(int row, int col) {
        return initial[row][col] != 0;
    }

    public int getSize() {
        return SIZE;
    }

    public int getValue(int rowIndex, int colIndex) {
        if (rowIndex >= 0 && rowIndex < SIZE &&
                colIndex >= 0 && rowIndex < SIZE) {
            return board[rowIndex][colIndex];
        }
        System.err.println("Invalid access!");
        return 0;
    }

    /**
     * Set a value in the grid and return true if arguments valid, false otherwise.
     *
     * @param row   valid board index
     * @param col   valid board index
     * @param value valid number between 1 and (including) SIZE.
     * @return false if invalid args, true otherwise.
     */
    public boolean setValue(int row, int col, int value) {
        if (row < SIZE && row >= 0 &&
                col < SIZE && col >= 0 &&
                value <= SIZE && value >= 0) { // valid arguments?

            if (isInitial(row, col)) {
                System.err.println("write on initial position!");
                return false;
            }
            Message msg = validPosition(row, col, value) ? (isSolved() ? WON : VALID_ENTERED) : INVALID_ENTERED;
            msg.setMove(row, col, value);
            board[row][col] = value;
            setChanged();
            notifyObservers(msg);
            return true;
        } else {
            return false;
        }
    }

    boolean colContains(final int colIndex, final int value) {
        return SudokuUtil.colContains(board, SIZE, colIndex, value);
    }

    public boolean rowContains(final int rowIndex, final int value) {
        return SudokuUtil.rowContains(board, SIZE, rowIndex, value);
    }

    boolean squareContains(final int row, final int col, final int value) {
        return SudokuUtil.squareContains(board, SIZE, row, col, value);
    }

    private boolean validPosition(int row, int col, int value) {
        return SudokuUtil.validPosition(board, SIZE, row, col, value);
    }

    public boolean logicSolve() {
        if (LogicSolver.solve(board, SIZE)) {
            setChanged();
            notifyObservers(AUTO_SOLVED);
            return true;
        }
        return false;
    }

    public boolean bTrackSolve() {
        int[][] clone = cloneBoard();
        if (BacktrackSolver.solve(board, clone, SIZE)) {
            setChanged();
            notifyObservers(AUTO_SOLVED);
            return true;
        } else
            return false;
    }

    public void reset() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (!isInitial(row, col)) {
                    board[row][col] = 0;
                }
            }
        }
        setChanged();
        notifyObservers(RESET);
    }

    private boolean isSolved() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0 || !validPosition(row, col, board[row][col]))
                    return false;
            }
        }
        return true;
    }

    private int[][] cloneBoard() {
        int[][] clone = new int[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            System.arraycopy(board[row], 0, clone[row], 0, SIZE);
        }
        return clone;
    }

    public String toCSV() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int rowIndex = 0; rowIndex < SIZE; rowIndex++) {
            for (int colIndex = 0; colIndex < SIZE - 1; colIndex++) { // all but last, ';' each
                stringBuilder.append(board[rowIndex][colIndex]).append(";");
            }
            stringBuilder.append(board[rowIndex][SIZE - 1]).append('\n'); // no ';' for last
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int row = 0; row < SIZE; ++row) {
            if (row % Math.sqrt(SIZE) == 0)
                stringBuilder.append(" ———————————————————————\n");
            for (int col = 0; col < SIZE; ++col) {
                if (col % Math.sqrt(SIZE) == 0) stringBuilder.append("| ");
                stringBuilder.append(board[row][col] == 0
                        ? " "
                        : board[row][col]);

                stringBuilder.append(' ');
            }
            stringBuilder.append("|\n");
        }
        stringBuilder.append(" ———————————————————————\n");
        return stringBuilder.toString();
    }

    public void getHint() {
        Move hint = LogicSolver.createHint(board, SIZE);
        if (hint != null) {
            board[hint.row][hint.col] = hint.value;
            Message msg = HINT;
            msg.setMove(hint.row, hint.col, hint.value);
            setChanged();
            notifyObservers(msg);
        }
    }

    public boolean isValid(int row, int col) {
        int value = board[row][col];
        board[row][col] = -1;
        boolean valid = validPosition(row, col, value);
        board[row][col] = value;
        return valid;
    }
}
