package com.comp301.a09akari.view;

import com.comp301.a09akari.controller.ClassicMvcController;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ControlView implements FXComponent {
  private final ClassicMvcController controller;

  public ControlView(ClassicMvcController controller) {
    this.controller = controller;
  }

  @Override
  public Parent render() {
    HBox controls = new HBox(10);

    Button resetButton = new Button("Reset");
    resetButton.setOnAction(e -> controller.clickResetPuzzle());

    Button nextButton = new Button("Next");
    nextButton.setOnAction(
        e -> {
          try {
            controller.clickNextPuzzle();
          } catch (IllegalStateException ex) {
            System.out.println("Can't go this way...");
          }
        });

    Button prevButton = new Button("Previous");
    prevButton.setOnAction(
        e -> {
          try {
            controller.clickPrevPuzzle();
          } catch (IllegalStateException ex) {
            System.out.println("Can't go this way...");
          }
        });

    Button randButton = new Button("Random");
    randButton.setOnAction(e -> controller.clickRandPuzzle());

    controls.getChildren().addAll(resetButton, prevButton, nextButton, randButton);
    return controls;
  }
}
