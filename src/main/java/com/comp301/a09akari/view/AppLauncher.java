package com.comp301.a09akari.view;

import com.comp301.a09akari.SamplePuzzles;
import com.comp301.a09akari.controller.ClassicMvcController;
import com.comp301.a09akari.controller.ControllerImpl;
import com.comp301.a09akari.model.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.scene.control.Label;

public class AppLauncher extends Application {
  @Override
  public void start(Stage stage) {
    // TODO: Create your Model, View, and Controller instances and launch your GUI
    PuzzleLibrary puzzles = new PuzzleLibraryImpl();
    puzzles.addPuzzle(new PuzzleImpl(SamplePuzzles.PUZZLE_01));
    puzzles.addPuzzle(new PuzzleImpl(SamplePuzzles.PUZZLE_02));
    puzzles.addPuzzle(new PuzzleImpl(SamplePuzzles.PUZZLE_03));
    puzzles.addPuzzle(new PuzzleImpl(SamplePuzzles.PUZZLE_04));
    puzzles.addPuzzle(new PuzzleImpl(SamplePuzzles.PUZZLE_05));

    Model model = new ModelImpl(puzzles);
    ClassicMvcController controller = new ControllerImpl(model);

    PuzzleView puzzleView = new PuzzleView(controller);
    ControlView controlView = new ControlView(controller);

    Text indexText = new Text(getPuzzleIndexText(controller));
    Text solvedText = new Text();

    BorderPane layout = new BorderPane();
    layout.setCenter(puzzleView.render());
    layout.setBottom(controlView.render());
    layout.setTop(indexText);

    HBox solvedBox = new HBox(solvedText);
    solvedBox.setAlignment(Pos.CENTER_RIGHT); // Align to the right
    solvedBox.setPadding(new Insets(10, 40, 10, 10)); // Add padding (top, right, bottom, left)
    layout.setRight(solvedBox);

    model.addObserver(
        newModel -> {
          layout.setCenter(puzzleView.render());
          indexText.setText(getPuzzleIndexText(controller));
          if (model.isSolved()) {
            solvedText.setText("SOLVED!");
          } else {
            solvedText.setText("");
          }
        });

    Scene scene = new Scene(layout, 600, 600);
    stage.setScene(scene);
    stage.setTitle("Akari");
    stage.show();
  }

  private String getPuzzleIndexText(ClassicMvcController controller) {
    return "Puzzle "
        + (controller.getModel().getActivePuzzleIndex() + 1)
        + " of "
        + (controller.getModel().getPuzzleLibrarySize());
  }
}
