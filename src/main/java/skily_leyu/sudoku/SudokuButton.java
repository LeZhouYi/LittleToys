package main.java.skily_leyu.sudoku;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class SudokuButton extends JButton{

	private SudokuCell sudokuCell;
	private Sudoku sudoku;

	public SudokuButton(Sudoku sudoku,SudokuCell sudokuCell) {
		this.sudoku =sudoku;
		this.sudokuCell = sudokuCell;
	}

}
