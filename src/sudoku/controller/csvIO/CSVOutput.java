package sudoku.controller.csvIO;

import javafx.scene.control.Alert;
import sudoku.game.SudokuGame;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CSVOutput {
    /**
     * Method to write a .csv file.
     *
     * @param game game field to be filled with csv entries
     * @param file csv file containing all entries, directly mapped to a sudoku field
     */
    public static void saveCSV(SudokuGame game, File file) {
        try {
            Files.write(file.toPath(), game.toCSV().getBytes());
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Invalid file!");
            alert.setContentText("There was an error during writing to the file you specified.");
            alert.showAndWait();
        }
    }
}
