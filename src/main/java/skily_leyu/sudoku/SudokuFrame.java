package main.java.skily_leyu.sudoku;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class SudokuFrame extends JFrame {

	private JPanel contentPane;

	private List<JPanel> cellPanels;
	private List<SudokuButton> cellBtns;

	private Sudoku sudoku;

	private JPopupMenu popupMenu = new JPopupMenu();
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

		this.contentPane = new JPanel();
		this.setContentPane(contentPane);
		this.contentPane.setLayout(new GridLayout(3, 3, 2, 2));

		this.setSize(500, 500);
		this.setFocusTraversalKeysEnabled(false);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		int x = (int)(toolkit.getScreenSize().getWidth()-this.getWidth())/2;
		int y = (int)(toolkit.getScreenSize().getHeight()-this.getHeight())/2;
		this.setLocation(x, y);
		
		initSudoku();
		
		initPopMenu();
		
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			
			@Override
			public void eventDispatched(AWTEvent event) {
				KeyEvent keyEvent = ((KeyEvent)event);
				if(keyEvent.getID()==KeyEvent.KEY_PRESSED) {
					if(keyEvent.getKeyCode()==KeyEvent.VK_TAB) {
						Point framePoint = getFrame().getLocation();
						int xMin = (int)framePoint.getX()+10;
						int xMax = (int)framePoint.getX()+getFrame().getWidth()-10;
						int yMin = (int)framePoint.getY()+30;
						int yMax = (int)framePoint.getY()+getFrame().getHeight()-10;

						Point mousePoint = MouseInfo.getPointerInfo().getLocation();
						int mouseX = (int)mousePoint.getX();
						int mouseY = (int)mousePoint.getY();
						
						if(mouseX>=xMin&&mouseX<=xMax&&mouseY>=yMin&&mouseY<=yMax) {
							popupMenu.show(getFrame(), mouseX-xMin, mouseY-yMin);
						}
					}
				}
			}
		}, AWTEvent.KEY_EVENT_MASK);
	}

	protected void initPopMenu() {
		JMenuItem findItem = new JMenuItem("预填");
		findItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						List<Point> points = sudoku.showCellInferredValues();
						if(points.size()>0) {
							long nowTime = new Date().getTime()/100;
							for(Point point:points) {
								sudoku.getCell(point.x, point.y).setUpdateColor(new Color(0xCCFF99),nowTime,3);
							}
							while(new Date().getTime()/100-nowTime<3*10) {
								for(Point point:points) {
									getSudokuButton(point.x, point.y).updateButton();
								}
							}
						}
					}
				}).start();
			}
		});
		this.popupMenu.add(findItem);
		
		JMenuItem outItem = new JMenuItem("推测");
		outItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
					}
				}).start();
			}
		});
		this.popupMenu.add(outItem);
	}

	private JFrame getFrame() {
		return this;
	}
	
	protected void initSudoku() {
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
				SudokuButton cellBtn = new SudokuButton(this,this.sudoku,this.sudoku.getCell(row,column));

				int cellIndex = (row / 3) * 3 + column / 3;

				this.cellBtns.add(cellBtn);
				this.cellPanels.get(cellIndex).add(cellBtn);
			}
		}
	}

	public void updateButtons(SudokuCell sudokuCell) {
		int row = sudokuCell.getRow();
		int column = sudokuCell.getColumn();
		for (int teRow = 0; teRow < 9; teRow++) {
			if (teRow!=row) {
				this.getSudokuButton(teRow, column).updateButton();
			}
		}
		for (int teColumn = 0; teColumn < 9; teColumn++) {
			if (teColumn!=column) {
				this.getSudokuButton(row, teColumn).updateButton();
			}
		}
		int cellIndex = sudokuCell.getCellIndex();
		int cellRow = (cellIndex/3)*3;
		int cellColumn = (cellIndex%3)*3;
		for (int teRow = 0; teRow < 3; teRow++) {
			for (int teColumn = 0; teColumn < 3; teColumn++) {
				if (teRow+cellRow!=row&&teColumn+cellColumn!=column) {
					this.getSudokuButton(teRow+cellRow, teColumn+cellColumn).updateButton();
				}
			}
		}
	}

	public SudokuButton getSudokuButton(int row, int column) {
		return this.cellBtns.get(row*9+column);
	}
	
}
