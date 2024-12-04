package com.comp301.a09akari.view;

import com.comp301.a09akari.controller.ClassicMvcController;
import com.comp301.a09akari.model.Puzzle;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.shape.Rectangle;

public class PuzzleView implements FXComponent {
  private final ClassicMvcController controller;

  public PuzzleView(ClassicMvcController controller) {
    this.controller = controller;
  }

  @Override
  public Parent render() {
    GridPane gridPane = new GridPane();
    gridPane.setGridLinesVisible(true);
    Puzzle puzzle = controller.getModel().getActivePuzzle();

    for (int r = 0; r < puzzle.getHeight(); r++) {
      for (int c = 0; c < puzzle.getWidth(); c++) {
        Rectangle cell = new Rectangle(40, 40);
        StackPane cellPane = new StackPane();

        cellPane.getChildren().add(cell);

        switch (puzzle.getCellType(r, c)) {
          case WALL:
            cell.setFill(Color.BLACK);
            break;
          case CORRIDOR:
            if (controller.getModel().isLamp(r, c)) {
              cell.setFill(Color.ORANGE);
            } else if (controller.getModel().isLit(r, c)) {
              cell.setFill(Color.YELLOW);
            } else {
              cell.setFill(Color.WHITE);
            }
            if (controller.getModel().isLamp(r, c) && controller.getModel().isLampIllegal(r, c)) {
              cell.setFill(Color.RED);
            }

            cell.setStroke(Color.BLACK);
            cell.setStrokeWidth(1);

            int finalR = r;
            int finalC = c;
            cell.setOnMouseClicked(e -> controller.clickCell(finalR, finalC));
            break;
          case CLUE:
            int clueVal = puzzle.getClue(r, c);
            if (controller.getModel().isClueSatisfied(r, c)) {
              cell.setFill(Color.LIGHTGREEN);
            } else {
              cell.setFill(Color.LIGHTBLUE);
            }
            cellPane.getChildren().add(new Text(String.valueOf(clueVal)));
            break;
        }
        gridPane.add(cellPane, c, r);
      }
    }
    return gridPane;
  }
}
