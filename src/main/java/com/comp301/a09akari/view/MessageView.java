package com.comp301.a09akari.view;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class MessageView implements FXComponent {
  private final boolean isSolved;

  public MessageView(boolean isSolved) {
    this.isSolved = isSolved;
  }

  @Override
  public Parent render() {
    HBox messageBox = new HBox();
    if (isSolved) {
      Label victory = new Label("You solved the puzzle!");
      messageBox.getChildren().add(victory);
    }
    return messageBox;
  }
}
