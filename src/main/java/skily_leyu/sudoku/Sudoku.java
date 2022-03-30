package main.java.skily_leyu.sudoku;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sudoku {

	private List<SudokuCell> sudokuCells;

	public Sudoku() {
		this.sudokuCells = new ArrayList<>();
		for (int row = 0; row < 9; row++) {
			for (int column = 0; column < 9; column++) {
				sudokuCells.add(new SudokuCell(row, column));
			}
		}
	}

	/**
	 * 获得某行某列的格子
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	public SudokuCell getCell(int row, int column) {
		return this.sudokuCells.get(row * 9 + column);
	}

	/**
	 * 检查某格子的输入值或可选值是否可以填入或更新
	 *
	 * @param sudokuCell 要检查的格子
	 * @param checkValue 要检查的输入值/可选值
	 * @return
	 */
	public boolean checkNowSet(SudokuCell sudokuCell, int checkValue) {
		if (checkValue != SudokuCell.EMPTY) {
			int row = sudokuCell.getRow();
			int column = sudokuCell.getColumn();
			for (int teRow = 0; teRow < 9; teRow++) {
				if (teRow != row && this.getCell(teRow, column).getNow() == checkValue) {
					return false;
				}
			}
			for (int teColumn = 0; teColumn < 9; teColumn++) {
				if (teColumn != column && this.getCell(row, teColumn).getNow() == checkValue) {
					return false;
				}
			}
			int cellIndex = sudokuCell.getCellIndex();
			int cellRow = (cellIndex / 3) * 3;
			int cellColumn = (cellIndex % 3) * 3;
			for (int teRow = 0; teRow < 3; teRow++) {
				for (int teColumn = 0; teColumn < 3; teColumn++) {
					if (teRow + cellRow != row && teColumn + cellColumn != column
							&& this.getCell(teRow + cellRow, teColumn + cellColumn).getNow() == checkValue) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * 更新当前格子有输入后其它格子的可选值
	 *
	 * @param sudokuCell
	 */
	public void updateOptionalValues(SudokuCell sudokuCell) {
		int updateValue = sudokuCell.getNow();
		int row = sudokuCell.getRow();
		int column = sudokuCell.getColumn();
		for (int teRow = 0; teRow < 9; teRow++) {
			this.getCell(teRow, column).setOptionalValuesEmpty(updateValue);
		}
		for (int teColumn = 0; teColumn < 9; teColumn++) {
			this.getCell(row, teColumn).setOptionalValuesEmpty(updateValue);
		}
		int cellIndex = sudokuCell.getCellIndex();
		int cellRow = (cellIndex / 3) * 3;
		int cellColumn = (cellIndex % 3) * 3;
		for (int teRow = 0; teRow < 3; teRow++) {
			for (int teColumn = 0; teColumn < 3; teColumn++) {
				this.getCell(teRow + cellRow, teColumn + cellColumn).setOptionalValuesEmpty(updateValue);
			}
		}
	}

	/**
	 * 更新当前格子被清空输入后其它格子的可选值
	 *
	 * @param sudokuCell
	 * @param before
	 */
	public void rebackOptionalValues(SudokuCell sudokuCell, int before) {
		int row = sudokuCell.getRow();
		int column = sudokuCell.getColumn();
		for (int teRow = 0; teRow < 9; teRow++) {
			SudokuCell teCell = this.getCell(teRow, column);
			if (this.checkNowSet(teCell, before)) {
				teCell.setOptionalValuesValue(before);
			}
		}
		for (int teColumn = 0; teColumn < 9; teColumn++) {
			SudokuCell teCell = this.getCell(row, teColumn);
			if (this.checkNowSet(teCell, before)) {
				teCell.setOptionalValuesValue(before);
			}
		}
		int cellIndex = sudokuCell.getCellIndex();
		int cellRow = (cellIndex / 3) * 3;
		int cellColumn = (cellIndex % 3) * 3;
		for (int teRow = 0; teRow < 3; teRow++) {
			for (int teColumn = 0; teColumn < 3; teColumn++) {
				SudokuCell teCell = this.getCell(teRow + cellRow, teColumn + cellColumn);
				if (this.checkNowSet(teCell, before)) {
					teCell.setOptionalValuesValue(before);
				}
			}
		}
	}

	/**
	 * 获取所有宫未填格中有一个可选值的格子的坐标 会更新该格子宫内其它格子的可选值 会设定该格子显示的短暂颜色
	 *
	 * @return
	 */
	public List<Point> showCellInferredValues() {
		List<Point> tePoints = new ArrayList<>();
		for (int cellIndex = 0; cellIndex < 9; cellIndex++) {
			for (int value = 1; value <= 9; value++) {
				Point point = fintCellInferredValues(cellIndex, value);
				if (point != null) {
					this.getCell(point.x, point.y).setOtherValuesEmpty(value);
					tePoints.add(point);
				}
			}
		}
		for (int row = 0; row < 9; row++) {
			for (int column = 0; column < 9; column++) {
				SudokuCell sudokuCell = this.getCell(row, column);
				if (sudokuCell.getNow() == SudokuCell.EMPTY && sudokuCell.getOptionalValuesLength() == 1) {
					tePoints.add(new Point(sudokuCell.getRow(), sudokuCell.getColumn()));
				}
			}
		}
		return tePoints;
	}

	/**
	 * 获取某宫内只有一个可选值且等于findValues的格子
	 *
	 * @param cellIndex
	 * @param findValues
	 * @return
	 */
	public Point fintCellInferredValues(int cellIndex, int findValues) {
		int cellRow = (cellIndex / 3) * 3;
		int cellColumn = (cellIndex % 3) * 3;
		Point point = null;
		int amount = 0;
		for (int teRow = 0; teRow < 3; teRow++) {
			for (int teColumn = 0; teColumn < 3; teColumn++) {
				SudokuCell teCell = this.getCell(teRow + cellRow, teColumn + cellColumn);
				if (teCell.getNow() == findValues) {
					return null;
				} else if (teCell.isEmpty() && teCell.isOptionalExist(findValues)) {
					point = teCell.getPoint();
					amount++;
				}
			}
		}
		return amount == 1 ? point : null;
	}

	/**
	 * 根据所有宫内的可选对依次更新格子的可选值 会更新宫内及宫外格子的可选值 会显示短暂更新的颜色
	 *
	 * @return
	 */
	public List<Point> updateCellMatchIndex() {
		List<Point> tePoints = new ArrayList<>();
		for (int cellIndex = 0; cellIndex < 9; cellIndex++) {
			List<Point> cellPoints = new ArrayList<>();
			int cellRow = (cellIndex / 3) * 3;
			int cellColumn = (cellIndex % 3) * 3;
			// 寻找宫内唯二可选值的格子
			for (int teRow = 0; teRow < 3; teRow++) {
				for (int teColumn = 0; teColumn < 3; teColumn++) {
					SudokuCell sudokuCell = this.getCell(teRow + cellRow, teColumn + cellColumn);
					if (sudokuCell.getNow() != SudokuCell.EMPTY && sudokuCell.getOptionalValuesLength() == 1) {
						tePoints.add(new Point(sudokuCell.getRow(), sudokuCell.getColumn()));
					}
				}
			}
			if (cellPoints.size() <= 1) {
				continue;
			}
		}
		return tePoints;
	}

	/**
	 * 若某一宫内的某一可选值均在同一行/列,则宫外的该行/列的格子的可选值清空该值
	 */
	public void updateOutCellMatch() {
		for (int cellIndex = 0; cellIndex < 9; cellIndex++) {
			for (int value = 1; value <= 9; value++) {
				List<Point> points = getCellOptionalValuesPoints(cellIndex, value, 2, 3);
				if (isRowLine(points)) {
					updateOutCellOptionValues(cellIndex, value, points.get(0).x, true);
				} else if (isColumnLine(points)) {
					updateOutCellOptionValues(cellIndex, value, points.get(0).y, false);
				}
			}
		}
	}

	/**
	 * 更新宫外的某一行/列的格子的可选值(设置为EMPTY)
	 *
	 * @param cellIndex 当前不更新的宫
	 * @param value     当前要更新的可选值
	 * @param line      当前行/列
	 * @param isRow     true=列更新
	 */
	public void updateOutCellOptionValues(int cellIndex, int value, int line, boolean isRow) {
		if (isRow) {
			for (int teColumn = 0; teColumn < 9; teColumn++) {
				SudokuCell sudokuCell = getCell(line, teColumn);
				if (sudokuCell.getCellIndex() != cellIndex) {
					sudokuCell.setOptionalValuesEmpty(value);
				}
			}
		} else {
			for (int teRow = 0; teRow < 9; teRow++) {
				SudokuCell sudokuCell = getCell(teRow, line);
				if (sudokuCell.getCellIndex() != cellIndex) {
					sudokuCell.setOptionalValuesEmpty(value);
				}
			}
		}
	}

	/**
	 * 判断一系列点的y坐标是否相等
	 *
	 * @param points
	 * @return
	 */
	public boolean isColumnLine(List<Point> points) {
		if (points != null && points.size() > 0) {
			int row = points.get(0).y;
			for (int index = 1; index < points.size(); index++) {
				if (row != points.get(index).y) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 判断一系列点的x坐标是否相等
	 *
	 * @param points
	 * @return
	 */
	public boolean isRowLine(List<Point> points) {
		if (points != null && points.size() > 0) {
			int row = points.get(0).x;
			for (int index = 1; index < points.size(); index++) {
				if (row != points.get(index).x) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 获取某一宫内未填格子中，某一选项值的可填格子数在[minLength,maxLength]间的格子的坐标
	 *
	 * @param cellIndex 宫坐标
	 * @param value     当前要检索的可选值
	 * @param minLength 最小可填格子数
	 * @param maxLength 最大可填格子数
	 * @return
	 */
	public List<Point> getCellOptionalValuesPoints(int cellIndex, int value, int minLength, int maxLength) {
		int cellRow = (cellIndex / 3) * 3;
		int cellColumn = (cellIndex % 3) * 3;
		List<Point> points = new ArrayList<>();
		for (int teRow = 0; teRow < 3; teRow++) {
			for (int teColumn = 0; teColumn < 3; teColumn++) {
				SudokuCell sudokuCell = getCell(cellRow + teRow, cellColumn + teColumn);
				if (sudokuCell.isEmpty() && sudokuCell.isOptionalExist(value)) {
					points.add(sudokuCell.getPoint());
				}
			}
		}
		return points.size() >= minLength && points.size() <= maxLength ? points : null;
	}

	/**
	 * 若宫内存在只有两个可选值的格子，且这些格子形成AB-BA/AB-BC-CA的形状，则更新剩余格子的可选值
	 */
	public void updateInCellMatch() {
		for (int cellIndex = 0; cellIndex < 9; cellIndex++) {
			Map<Integer, List<Point>> valueMap = new HashMap<>();
			for (int value = 1; value <= 9; value++) {
				List<Point> points = getCellOptionalValuesPoints(cellIndex, value, 2, 2);
				if (points != null) {
					valueMap.put(value, points);
				}
			}
		}
	}

	/**
	 * 获取对应坐标的格子
	 *
	 * @param point
	 * @return
	 */
	public SudokuCell getCell(Point point) {
		return getCell(point.x, point.y);
	}

}
