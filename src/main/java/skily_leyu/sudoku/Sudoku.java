package main.java.skily_leyu.sudoku;

import java.util.ArrayList;
import java.util.List;

public class Sudoku {

	private List<SudokuCell> sudokuCells;

	public Sudoku() {
		this.sudokuCells = new ArrayList<>();
		for (int row = 0; row < 9; row++) {
			for (int column = 0; column < 9; column++) {
				sudokuCells.add(new SudokuCell(row,column));
			}
		}
	}

	public SudokuCell getCell(int row, int column) {
		return this.sudokuCells.get(row*9+column);
	}

}
