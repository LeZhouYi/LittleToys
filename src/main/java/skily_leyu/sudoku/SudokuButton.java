package main.java.skily_leyu.sudoku;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class SudokuButton extends JButton {

	private SudokuCell sudokuCell;
	private Sudoku sudoku;

	private JPopupMenu optionalMenu;
	private SudokuFrame sudokuFrame;

	public SudokuButton(SudokuFrame sudokuFrame,Sudoku sudoku, SudokuCell sudokuCell) {
		this.sudokuFrame=sudokuFrame;
		this.sudoku = sudoku;
		this.sudokuCell = sudokuCell;
		this.setFont(new Font("宋体", Font.PLAIN, 20));
		this.updateButton();
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==MouseEvent.BUTTON1) {
					updateMenu();
					optionalMenu.show(e.getComponent(), e.getX(), e.getY());
				}
				else if(e.getButton()==MouseEvent.BUTTON2) {
					sudokuCell.setIsFixed();
					updateButton();
				}else if(e.getButton()==MouseEvent.BUTTON3) {
					if(!sudokuCell.isFixed()) {
						int before = sudokuCell.getNow();
						sudokuCell.setNow(SudokuCell.EMPTY);
						sudoku.rebackOptionalValues(sudokuCell, before);
						sudokuFrame.updateButtons(sudokuCell);
						updateButton();
					}
				}
			}
		});
	}

	public void updateMenu() {
		this.optionalMenu = new JPopupMenu();
		if (this.sudokuCell.getOptionalValues() == null) {
			return;
		}
		for (int value : this.sudokuCell.getOptionalValues()) {
			if (value != SudokuCell.EMPTY) {
				JMenuItem menuItem = new JMenuItem(String.valueOf(value));
				menuItem.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						int before = sudokuCell.getNow();
						if (before == SudokuCell.EMPTY) {
							sudokuCell.setNow(value);
							if (sudoku.checkNowSet(sudokuCell, sudokuCell.getNow())) {
								sudoku.updateOptionalValues(sudokuCell);
							}
						}
						sudokuFrame.updateButtons(sudokuCell);
						updateButton();
					}
				});
				this.optionalMenu.add(menuItem);
			}
		}
	}

	public void updateButton() {
		this.setText(sudokuCell.getNowString());
		this.setBackground(sudokuCell.getColor());
		this.setFocusPainted(false);
	}

}
