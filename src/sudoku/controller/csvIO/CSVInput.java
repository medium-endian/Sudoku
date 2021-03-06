package sudoku.controller.csvIO;

import com.opencsv.CSVReader;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVInput {
    /**
     * Method to load a .csv file.
     *
     * @param file CSV file containing all entries, directly mapped to a sudoku field
     */
    public static int[][] loadCSV(File file) {
        try (CSVReader csvReader = new CSVReader(new FileReader(file), ';')) {
            List<Integer[]> rows = strings2Integers(csvReader.readAll());
            int size = checkDimensions(rows);
            if (isValidCSV(size, rows)) {
                int[][] newBoard = new int[size][size];
                for (int rowIndex = 0; rowIndex < size; rowIndex++) {
                    Integer[] row = rows.get(rowIndex);
                    for (int colIndex = 0; colIndex < row.length; colIndex++) {
                        newBoard[rowIndex][colIndex] = row[colIndex];
                    }
                }
                return newBoard;
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Invalid file content!");
            alert.setContentText("The file you tried to open contained invalid input!");
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Invalid file!");
            alert.setContentText("There was an error during opening of the file you requested.");
            alert.showAndWait();
        }
        return null;
    }

    private static int checkDimensions(List<Integer[]> rows) {
        int length = rows.get(0).length;
        boolean correct = true;
        for (Integer[] integers : rows) {
            if (integers.length != length) {
                correct = false;
            }
        }
        if (rows.size() != length) { // not square
            correct = false;
        }
        return correct ? length : 0;
    }

    private static List<Integer[]> strings2Integers(List<String[]> lines) throws NumberFormatException {
        List<Integer[]> rows = new ArrayList<>(lines.size());
        for (String[] strings : lines) {
            Integer[] row = new Integer[strings.length];
            for (int i = 0; i < strings.length; i++) {
                row[i] = Integer.parseInt(strings[i]);
            }
            rows.add(row);
        }
        return rows;
    }

    private static boolean isValidCSV(int size, List<Integer[]> rows) {
        boolean correctLength = rows.stream().allMatch(intArr -> intArr.length == size);
        boolean correctRange = rows.stream().allMatch(ints ->
                Arrays.stream(ints).allMatch(i -> (i >= 0) && (i <= size)));
        return correctLength && correctRange;
    }
}
