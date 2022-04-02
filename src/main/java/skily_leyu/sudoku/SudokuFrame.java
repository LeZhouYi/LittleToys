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

@SuppressWarnings("serial")
public class SudokuFrame extends JFrame {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SudokuFrame frame = new SudokuFrame();
					frame.setTitle("Sudoku-数独@乐语天晴");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JPanel contentPane;
	private List<JPanel> cellPanels;

	private List<SudokuButton> cellBtns;

	private Sudoku sudoku;

	private JPopupMenu popupMenu;

	private List<Sudoku> saves;

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
		int x = (int) (toolkit.getScreenSize().getWidth() - this.getWidth()) / 2;
		int y = (int) (toolkit.getScreenSize().getHeight() - this.getHeight()) / 2;
		this.setLocation(x, y);

		initSaves();
		initSudoku(null);

		initPopMenu();

		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

			@Override
			public void eventDispatched(AWTEvent event) {
				KeyEvent keyEvent = ((KeyEvent) event);
				if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
					if (keyEvent.getKeyCode() == KeyEvent.VK_TAB) {
						Point framePoint = getFrame().getLocation();
						int xMin = (int) framePoint.getX() + 10;
						int xMax = (int) framePoint.getX() + getFrame().getWidth() - 10;
						int yMin = (int) framePoint.getY() + 30;
						int yMax = (int) framePoint.getY() + getFrame().getHeight() - 10;

						Point mousePoint = MouseInfo.getPointerInfo().getLocation();
						int mouseX = (int) mousePoint.getX();
						int mouseY = (int) mousePoint.getY();

						if (mouseX >= xMin && mouseX <= xMax && mouseY >= yMin && mouseY <= yMax) {
							popupMenu.show(getFrame(), mouseX - xMin, mouseY - yMin);
						}
					}
				}
			}
		}, AWTEvent.KEY_EVENT_MASK);
	}

	private JFrame getFrame() {
		return this;
	}

	/**
	 * 获取对应的格子控件
	 * @param row
	 * @param column
	 * @return
	 */
	public SudokuButton getSudokuButton(int row, int column) {
		return this.cellBtns.get(row * 9 + column);
	}

	/**
	 * 初始化或重置Tab键弹出菜单
	 */
	protected void initPopMenu() {
		this.popupMenu = new JPopupMenu();
		JMenuItem findItem = new JMenuItem("预填");
		findItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						List<Point> points = sudoku.showCellInferredValues();
						if (points.size() > 0) {
							long nowTime = new Date().getTime() / 100;
							for (Point point : points) {
								sudoku.getCell(point.x, point.y).setUpdateColor(new Color(0xCCFF99), nowTime, 3);
							}
							while (new Date().getTime() / 100 - nowTime < 3 * 10) {
								for (Point point : points) {
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
						sudoku.updateOutCellMatch();
						sudoku.updateInCellMatch();
						sudoku.updateLineMatch();
						updateAllButtons();
					}
				}).start();
			}
		});
		this.popupMenu.add(outItem);
		JMenuItem saveItem = new JMenuItem("备份");
		saveItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				saves.add(sudoku.getCopy());
				int size = saves.size();
				JMenuItem copyItem = new JMenuItem(String.format("存档%d", size));
				copyItem.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						initSudoku(saves.get(size-1).getCopy());
						updateAllButtons();
					}
				});
				popupMenu.add(copyItem);
			}
		});
		this.popupMenu.add(saveItem);

		JMenuItem clearItem = new JMenuItem("清空");
		clearItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				initSaves();
				initSudoku(new Sudoku());
				initPopMenu();
				updateAllButtons();
			}
		});
		this.popupMenu.add(clearItem);
	}

	/**
	 * 初始化并清空存档
	 */
	protected void initSaves() {
		this.saves = new ArrayList<>();
	}

	/**
	 * 初始化数独或设置数独
	 * @param sudoku 空时则为初始化，只能初始化一次，初始化多次控件会不可控
	 */
	protected void initSudoku(Sudoku sudoku) {
		this.sudoku = sudoku!=null?sudoku:new Sudoku();

		if(sudoku==null) {
			this.cellPanels = new ArrayList<>();

			for (int cellIndex = 0; cellIndex < 9; cellIndex++) {
				JPanel cellPanel = new JPanel();
				cellPanel.setLayout(new GridLayout(3, 3, 0, 0));
				this.cellPanels.add(cellPanel);
				this.contentPane.add(cellPanel);
			}
			this.cellBtns = new ArrayList<>();
		}

		for (int row = 0; row < 9; row++) {
			for (int column = 0; column < 9; column++) {
				if(sudoku==null) {
					SudokuButton cellBtn = new SudokuButton(this, this.sudoku, this.sudoku.getCell(row, column));

					int cellIndex = cellBtn.getCellIndex();

					this.cellBtns.add(cellBtn);
					this.cellPanels.get(cellIndex).add(cellBtn);
				}else {
					this.cellBtns.get(row*9+column).updateSudoku(this.sudoku,this.sudoku.getCell(row,column));
				}
			}
		}
	}

	/**
	 * 刷新数独
	 */
	public void updateAllButtons() {
		for (SudokuButton sudokuButton : cellBtns) {
			sudokuButton.updateButton();
		}
	}

	/**
	 * 根据当前格子部分更新
	 * @param sudokuCell
	 */
	public void updateButtons(SudokuCell sudokuCell) {
		Point point = sudokuCell.getPoint();
		for (int line = 0; line < 9; line++) {
			if (line != point.x) {
				this.getSudokuButton(line, point.y).updateButton();
			}
			if (line != point.y) {
				this.getSudokuButton(point.x, line).updateButton();
			}
		}
		Point cellPoint = sudokuCell.getCellPoint();
		for (int teRow = 0; teRow < 3; teRow++) {
			for (int teColumn = 0; teColumn < 3; teColumn++) {
				if (teRow + cellPoint.x != point.x && teColumn + cellPoint.y != point.y) {
					this.getSudokuButton(teRow + cellPoint.x, teColumn + cellPoint.y).updateButton();
				}
			}
		}
	}

}
