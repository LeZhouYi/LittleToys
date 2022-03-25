package main.java.skily_leyu.sudoku;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SudokuFrame extends JFrame {

	private JPanel contentPane;

	private List<JPanel> cellPanels;
	private List<SudokuButton> cellBtns;

	private Sudoku sudoku;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SudokuFrame frame = new SudokuFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SudokuFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 474, 485);

		this.contentPane = new JPanel();
		this.setContentPane(contentPane);
		this.contentPane.setLayout(new GridLayout(3, 3, 2, 2));

		initSudoku();
	}

	private void initSudoku() {
		this.sudoku = new Sudoku();
		this.cellPanels = new ArrayList<>();

		for (int cellIndex = 0; cellIndex < 9; cellIndex++) {
			JPanel cellPanel = new JPanel();
			cellPanel.setLayout(new GridLayout(3, 3, 0, 0));
			this.cellPanels.add(cellPanel);
			this.contentPane.add(cellPanel);
		}

		this.cellBtns = new ArrayList<>();

		for (int row = 0; row < 9; row++) {
			for (int column = 0; column < 9; column++) {
				SudokuButton cellBtn = new SudokuButton(this.sudoku,this.sudoku.getCell(row,column));

				cellBtn.setBackground(Color.WHITE);

				int cellIndex = (row / 3) * 3 + column / 3;

				this.cellBtns.add(cellBtn);
				this.cellPanels.get(cellIndex).add(cellBtn);
			}
		}
	}

}
