package com.comp301.a09akari.view;

import com.comp301.a09akari.SamplePuzzles;
import com.comp301.a09akari.controller.ClassicMvcController;
import com.comp301.a09akari.controller.ControllerImpl;
import com.comp301.a09akari.model.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AppLauncher extends Application {
  @Override
  public void start(Stage stage) {
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

    Text titleText = new Text("Akari");
    titleText.getStyleClass().add("title-text");
    
    Text indexText = new Text(getPuzzleIndexText(controller));
    indexText.getStyleClass().add("puzzle-index-text");
    
    Text solvedText = new Text();
    solvedText.getStyleClass().add("solved-text");

    VBox puzzleArea = new VBox();
    puzzleArea.setAlignment(Pos.CENTER);
    puzzleArea.setSpacing(10);
    
    HBox indexBox = new HBox(indexText);
    indexBox.setAlignment(Pos.CENTER);
    
    HBox solvedBox = new HBox(solvedText);
    solvedBox.setAlignment(Pos.CENTER);
    
    puzzleArea.getChildren().addAll(indexBox, puzzleView.render(), solvedBox);
    
    HBox titleBox = new HBox(titleText);
    titleBox.setAlignment(Pos.CENTER);
    titleBox.setPadding(new Insets(50, 0, 20, 0));
    
    BorderPane layout = new BorderPane();
    layout.getStyleClass().add("main-layout");
    layout.setTop(titleBox);
    layout.setCenter(puzzleArea);
    layout.setBottom(controlView.render());

    model.addObserver(
        newModel -> {
          puzzleArea.getChildren().clear();
          puzzleArea.getChildren().addAll(indexBox, puzzleView.render(), solvedBox);
          indexText.setText(getPuzzleIndexText(controller));
          if (model.isSolved()) {
            solvedText.setText("SOLVED!");
          } else {
            solvedText.setText("");
          }
        });

    Scene scene = new Scene(layout, 700, 700);
    scene.getStylesheets().add("main.css");
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
