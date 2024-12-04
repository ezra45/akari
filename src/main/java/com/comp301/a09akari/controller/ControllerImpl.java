package com.comp301.a09akari.controller;

import com.comp301.a09akari.model.CellType;
import com.comp301.a09akari.model.Model;
import com.comp301.a09akari.model.Puzzle;

public class ControllerImpl implements ClassicMvcController {
  private final Model model;

  public ControllerImpl(Model model) {
    if (model == null) {
      throw new IllegalArgumentException("Invalid model.");
    }
    this.model = model;
  }

  @Override
  public void clickNextPuzzle() {
    int current = model.getActivePuzzleIndex();
    if (current == model.getPuzzleLibrarySize() - 1) {
      throw new IllegalStateException("There is no next puzzle.");
    }
    model.setActivePuzzleIndex(current + 1);
  }

  @Override
  public void clickPrevPuzzle() {
    int current = model.getActivePuzzleIndex();
    if (current == 0) {
      throw new IllegalStateException("There is no previous puzzle.");
    }
    model.setActivePuzzleIndex(current - 1);
  }

  @Override
  public void clickRandPuzzle() {
    model.setActivePuzzleIndex((int) (Math.random() * model.getPuzzleLibrarySize()));
  }

  @Override
  public void clickResetPuzzle() {
    model.resetPuzzle();
  }

  @Override
  public void clickCell(int r, int c) {
    Puzzle activePuzzle = model.getActivePuzzle();
    if (r < 0 || r >= activePuzzle.getHeight() || c < 0 || c >= activePuzzle.getWidth()) {
      throw new IndexOutOfBoundsException("Coordinates are invalid.");
    }
    CellType ct = activePuzzle.getCellType(r, c);
    if (ct == CellType.CORRIDOR) {
      if (model.isLamp(r, c)) {
        model.removeLamp(r, c);
      } else {
        model.addLamp(r, c);
      }
    }
  }

  @Override
  public Model getModel() {
    return this.model;
  }
}
