package com.comp301.a09akari.model;

import java.util.ArrayList;
import java.util.List;

public class ModelImpl implements Model {
  private final PuzzleLibrary library;
  private int activeIndex;
  private boolean[][] lampLocation;
  private boolean[][] isLit;
  private final List<ModelObserver> modelObserverList;
  private int[][] lightCount;

  public ModelImpl(PuzzleLibrary library) {
    if (library == null || library.size() == 0) {
      throw new IllegalArgumentException("Library must contain puzzles.");
    }
    this.library = library;
    this.activeIndex = 0;
    this.modelObserverList = new ArrayList<>();
    loadPuzzle();
  }

  @Override
  public void addLamp(int r, int c) {
    isValidCorridor(r, c);
    if (lampLocation[r][c]) {
      throw new IllegalArgumentException("Lamp already here.");
    }
    lampLocation[r][c] = true;
    updateLight(r, c, true);
    notifyObservers();
  }

  @Override
  public void removeLamp(int r, int c) {
    isValidCorridor(r, c);
    if (!lampLocation[r][c]) {
      throw new IllegalArgumentException("No lamp here.");
    }
    lampLocation[r][c] = false;
    updateLight(r, c, false);
    notifyObservers();
  }

  @Override
  public boolean isLit(int r, int c) {
    isValidCorridor(r, c);
    return isLit[r][c];
  }

  @Override
  public boolean isLamp(int r, int c) {
    isValidCorridor(r, c);
    return lampLocation[r][c];
  }

  @Override
  public boolean isLampIllegal(int r, int c) {
    isValidCorridor(r, c);
    if (!lampLocation[r][c]) {
      throw new IllegalArgumentException("No lamp here.");
    }
    for (int i = r - 1; i >= 0; i--) {
      CellType ct = getActivePuzzle().getCellType(i, c);
      if (ct == CellType.WALL || ct == CellType.CLUE) {
        break;
      }
      if (lampLocation[i][c]) {
        return true;
      }
    }
    for (int i = r + 1; i < getActivePuzzle().getHeight(); i++) {
      CellType ct = getActivePuzzle().getCellType(i, c);
      if (ct == CellType.WALL || ct == CellType.CLUE) {
        break;
      }
      if (lampLocation[i][c]) {
        return true;
      }
    }
    for (int j = c - 1; j >= 0; j--) {
      CellType ct = getActivePuzzle().getCellType(r, j);
      if (ct == CellType.WALL || ct == CellType.CLUE) {
        break;
      }
      if (lampLocation[r][j]) {
        return true;
      }
    }
    for (int j = c + 1; j < getActivePuzzle().getWidth(); j++) {
      CellType ct = getActivePuzzle().getCellType(r, j);
      if (ct == CellType.WALL || ct == CellType.CLUE) {
        break;
      }
      if (lampLocation[r][j]) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Puzzle getActivePuzzle() {
    return library.getPuzzle(activeIndex);
  }

  @Override
  public int getActivePuzzleIndex() {
    return activeIndex;
  }

  @Override
  public void setActivePuzzleIndex(int index) {
    if (index < 0 || index >= library.size()) {
      throw new IndexOutOfBoundsException();
    }
    activeIndex = index;
    resetPuzzle();
  }

  @Override
  public int getPuzzleLibrarySize() {
    return library.size();
  }

  @Override
  public void resetPuzzle() {
    loadPuzzle();
    notifyObservers();
  }

  @Override
  public boolean isSolved() {
    Puzzle activePuzzle = getActivePuzzle();
    // Goes thru all cells
    for (int r = 0; r < activePuzzle.getHeight(); r++) {
      for (int c = 0; c < activePuzzle.getWidth(); c++) {
        CellType ct = activePuzzle.getCellType(r, c);

        // Making sure all lamps are legal and lit
        if (ct == CellType.CORRIDOR) {
          if (!isLit(r, c)) {
            return false;
          }
          if (isLamp(r, c) && isLampIllegal(r, c)) {
            return false;
          }
        }

        // Makes sure all clues are satisfied
        if (ct == CellType.CLUE) {
          if (!isClueSatisfied(r, c)) {
            return false;
          }
        }
      }
    }
    return true;
  }

  @Override
  public boolean isClueSatisfied(int r, int c) {
    CellType ct = getActivePuzzle().getCellType(r, c);

    if (ct != CellType.CLUE) {
      throw new IllegalArgumentException("Not a clue cell.");
    }
    isValidCell(r, c);
    int lamps = 0;
    int clue = getActivePuzzle().getClue(r, c);
    // These if statements check each neighbor of the clue cell
    if (isValidCell(r + 1, c)
        && getActivePuzzle().getCellType(r + 1, c) == CellType.CORRIDOR
        && isLamp(r + 1, c)) {
      lamps++;
    }
    if (isValidCell(r - 1, c)
        && getActivePuzzle().getCellType(r - 1, c) == CellType.CORRIDOR
        && isLamp(r - 1, c)) {
      lamps++;
    }
    if (isValidCell(r, c + 1)
        && getActivePuzzle().getCellType(r, c + 1) == CellType.CORRIDOR
        && isLamp(r, c + 1)) {
      lamps++;
    }
    if (isValidCell(r, c - 1)
        && getActivePuzzle().getCellType(r, c - 1) == CellType.CORRIDOR
        && isLamp(r, c - 1)) {
      lamps++;
    }
    return lamps == clue;
  }

  @Override
  public void addObserver(ModelObserver observer) {
    if (!modelObserverList.contains(observer)) {
      modelObserverList.add(observer);
    }
  }

  @Override
  public void removeObserver(ModelObserver observer) {
    modelObserverList.remove(observer);
  }

  private void notifyObservers() {
    for (ModelObserver o : modelObserverList) {
      o.update(this);
    }
  }

  // Additional helper methods
  private void loadPuzzle() {
    Puzzle activePuzzle = getActivePuzzle();
    lampLocation = new boolean[activePuzzle.getHeight()][activePuzzle.getWidth()];
    isLit = new boolean[activePuzzle.getHeight()][activePuzzle.getWidth()];
    lightCount = new int[activePuzzle.getHeight()][activePuzzle.getWidth()];
  }

  private void updateLight(int r, int c, boolean addLight) {
    if (!isValidCell(r, c)) {
      throw new IndexOutOfBoundsException();
    }
    int lightChange;
    if (addLight) {
      lightChange = 1;
    } else {
      lightChange = -1;
    }
    lightCount[r][c] += lightChange;
    isLit[r][c] = lightCount[r][c] > 0;

    for (int i = r - 1; i >= 0; i--) {
      CellType ct = getActivePuzzle().getCellType(i, c);
      if (ct == CellType.WALL || ct == CellType.CLUE) {
        break;
      }
      lightCount[i][c] += lightChange;
      isLit[i][c] = lightCount[i][c] > 0;
    }
    for (int i = r + 1; i < getActivePuzzle().getHeight(); i++) {
      CellType ct = getActivePuzzle().getCellType(i, c);
      if (ct == CellType.WALL || ct == CellType.CLUE) {
        break;
      }
      lightCount[i][c] += lightChange;
      isLit[i][c] = lightCount[i][c] > 0;
    }
    for (int j = c - 1; j >= 0; j--) {
      CellType ct = getActivePuzzle().getCellType(r, j);
      if (ct == CellType.WALL || ct == CellType.CLUE) {
        break;
      }
      lightCount[r][j] += lightChange;
      isLit[r][j] = lightCount[r][j] > 0;
    }
    for (int j = c + 1; j < getActivePuzzle().getWidth(); j++) {
      CellType ct = getActivePuzzle().getCellType(r, j);
      if (ct == CellType.WALL || ct == CellType.CLUE) {
        break;
      }
      lightCount[r][j] += lightChange;
      isLit[r][j] = lightCount[r][j] > 0;
    }
  }

  private boolean isValidCell(int r, int c) {
    Puzzle activePuzzle = getActivePuzzle();
    return r >= 0 && r < activePuzzle.getHeight() && c >= 0 && c < activePuzzle.getWidth();
  }

  private void isValidCorridor(int r, int c) {
    if (!isValidCell(r, c)) {
      throw new IndexOutOfBoundsException();
    }
    if (getActivePuzzle().getCellType(r, c) != CellType.CORRIDOR) {
      throw new IllegalArgumentException("Cell must be of type CORRIDOR");
    }
  }
}
