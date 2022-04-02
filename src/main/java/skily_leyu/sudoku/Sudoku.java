package main.java.skily_leyu.sudoku;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sudoku {

	/**
	 * 获取当前宫对应的起始坐标
	 *
	 * @param cellIndex
	 * @return
	 */
	public static Point getCellPoint(int cellIndex) {
		return new Point((cellIndex / 3) * 3, (cellIndex % 3) * 3);
	}

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
	 * 检查某格子的输入值或可选值是否可以填入或更新
	 *
	 * @param sudokuCell 要检查的格子
	 * @param checkValue 要检查的输入值/可选值
	 * @return
	 */
	public boolean checkNowSet(SudokuCell sudokuCell, int checkValue) {
		if (checkValue != SudokuCell.EMPTY) {
			Point point = sudokuCell.getPoint();
			for (int line = 0; line < 9; line++) {
				if (line != point.x && this.getCell(line, point.y).getNow() == checkValue) {
					return false;
				}
				if (line != point.y && this.getCell(point.x, line).getNow() == checkValue) {
					return false;
				}
			}
			Point cellPoint = sudokuCell.getCellPoint();
			for (int teRow = 0; teRow < 3; teRow++) {
				for (int teColumn = 0; teColumn < 3; teColumn++) {
					if (teRow + cellPoint.x != point.x && teColumn + cellPoint.y != point.y
							&& this.getCell(teRow + cellPoint.x, teColumn + cellPoint.y).getNow() == checkValue) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * 获取某宫内只有一个可选值且等于findValues的格子
	 *
	 * @param cellIndex
	 * @param findValues
	 * @return
	 */
	public Point fintCellInferredValues(int cellIndex, int findValues) {
		Point cellPoint = Sudoku.getCellPoint(cellIndex);
		Point point = null;
		int amount = 0;
		for (int teRow = 0; teRow < 3; teRow++) {
			for (int teColumn = 0; teColumn < 3; teColumn++) {
				SudokuCell teCell = this.getCell(teRow + cellPoint.x, teColumn + cellPoint.y);
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
	 * 获取对应格子
	 *
	 * @param index
	 * @return
	 */
	public SudokuCell getCell(int index) {
		return this.sudokuCells.get(index);
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
	 * 获取对应坐标的格子
	 *
	 * @param point
	 * @return
	 */
	public SudokuCell getCell(Point point) {
		return getCell(point.x, point.y);
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
		Point cellPoint = Sudoku.getCellPoint(cellIndex);
		List<Point> points = new ArrayList<>();
		for (int teRow = 0; teRow < 3; teRow++) {
			for (int teColumn = 0; teColumn < 3; teColumn++) {
				SudokuCell sudokuCell = getCell(cellPoint.x + teRow, cellPoint.y + teColumn);
				if (sudokuCell.isEmpty() && sudokuCell.isOptionalExist(value)) {
					points.add(sudokuCell.getPoint());
				}
			}
		}
		return points.size() >= minLength && points.size() <= maxLength ? points : null;
	}

	/**
	 * 获取拷贝
	 *
	 * @return
	 */
	public Sudoku getCopy() {
		Sudoku sudoku = new Sudoku();
		sudoku.sudokuCells = new ArrayList<>();
		for (SudokuCell sudokuCell : this.sudokuCells) {
			try {
				sudoku.sudokuCells.add((SudokuCell) sudokuCell.clone());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		return sudoku;
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
	 * 更新当前格子被清空输入后其它格子的可选值
	 *
	 * @param sudokuCell
	 * @param before
	 */
	public void rebackOptionalValues(SudokuCell sudokuCell, int before) {
		Point point = sudokuCell.getPoint();
		for (int cross = 0; cross < 9; cross++) {
			SudokuCell teCell = this.getCell(cross, point.y);
			if (this.checkNowSet(teCell, before)) {
				teCell.setOptionalValuesValue(before);
			}
			teCell = this.getCell(point.x, cross);
			if (this.checkNowSet(teCell, before)) {
				teCell.setOptionalValuesValue(before);
			}
		}
		Point cellPoint = sudokuCell.getCellPoint();
		for (int teRow = 0; teRow < 3; teRow++) {
			for (int teColumn = 0; teColumn < 3; teColumn++) {
				SudokuCell teCell = this.getCell(teRow + cellPoint.x, teColumn + cellPoint.y);
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
					this.getCell(point).setOtherValuesEmpty(value);
					tePoints.add(point);
				}
			}
		}
		for (int index = 0; index < 81; index++) {
			SudokuCell sudokuCell = this.getCell(index);
			if (SudokuCell.isEmpty(sudokuCell.getNow()) && sudokuCell.getOptionalValuesLength() == 1) {
				tePoints.add(sudokuCell.getPoint());
			}
		}
		return tePoints;
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
			Point cellPoint = Sudoku.getCellPoint(cellIndex);
			// 寻找宫内唯二可选值的格子
			for (int teRow = 0; teRow < 3; teRow++) {
				for (int teColumn = 0; teColumn < 3; teColumn++) {
					SudokuCell sudokuCell = this.getCell(teRow + cellPoint.x, teColumn + cellPoint.y);
					if (!SudokuCell.isEmpty(sudokuCell.getNow()) && sudokuCell.getOptionalValuesLength() == 1) {
						tePoints.add(sudokuCell.getPoint());
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
	 * 更新宫内除标注外的未填格子，清空那些格子的[以values中所列的]可选值
	 *
	 * @param cellIndex
	 * @param points
	 * @param values
	 */
	public void updateCellOptionalValues(int cellIndex, List<Point> points, int[] values) {
		Point cellPoint = Sudoku.getCellPoint(cellIndex);
		for (int teRow = 0; teRow < 3; teRow++) {
			for (int teColumn = 0; teColumn < 3; teColumn++) {
				SudokuCell sudokuCell = getCell(cellPoint.x + teRow, cellPoint.y + teColumn);
				if (sudokuCell.isEmpty() && !points.contains(sudokuCell.getPoint())) {
					for (int value : values) {
						sudokuCell.setOptionalValuesEmpty(value);
					}
				}
			}
		}
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
			for (int value = 1; value <= 9; value++) {
				if (valueMap.containsKey(value)) {
					List<Point> points = valueMap.get(value);
					for (int otherValue = 1; otherValue <= 9; otherValue++) {
						if (valueMap.containsKey(otherValue) && otherValue != value) {
							List<Point> otherPoints = valueMap.get(otherValue);
							if (points.contains(otherPoints.get(0)) && points.contains(otherPoints.get(1))) {
								updateCellOptionalValues(cellIndex, points, new int[] { value, otherValue });
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 检查某一行/列的格子中某可选值是否只有一个，若是，更新该格可选值
	 *
	 * @param line
	 * @param b
	 */
	public void updateLineInferredValue(int line, boolean isRow) {
		int[] values = new int[9];
		for (int cross = 0; cross < 9; cross++) {
			SudokuCell sudokuCell = (isRow) ? getCell(line, cross) : getCell(cross, line);
			if (sudokuCell.isEmpty()) {
				for (int value : sudokuCell.getOptionalValues()) {
					if (!SudokuCell.isEmpty(value)) {
						values[value - 1]++;
					}
				}
			}
		}
		for (int value = 1; value <= 9; value++) {
			if (values[value - 1] == 1) {
				for (int cross = 0; cross < 9; cross++) {
					SudokuCell sudokuCell = (isRow) ? getCell(line, cross) : getCell(cross, line);
					if (sudokuCell.isEmpty() && sudokuCell.isOptionalExist(value)) {
						sudokuCell.setOtherValuesEmpty(value);
					}
				}
			}
		}
	}

	/**
	 * 若某一行/列的格子中某可选值只有一个，更新该格可选值
	 */
	public void updateLineMatch() {
		for (int line = 0; line < 9; line++) {
			updateLineInferredValue(line, true);
			updateLineInferredValue(line, false);
		}
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
		for (int line = 0; line < 9; line++) {
			this.getCell(line, column).setOptionalValuesEmpty(updateValue);
			this.getCell(row, line).setOptionalValuesEmpty(updateValue);
		}
		Point cellPoint = sudokuCell.getCellPoint();
		for (int teRow = 0; teRow < 3; teRow++) {
			for (int teColumn = 0; teColumn < 3; teColumn++) {
				this.getCell(teRow + cellPoint.x, teColumn + cellPoint.y).setOptionalValuesEmpty(updateValue);
			}
		}
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
		for (int cross = 0; cross < 9; cross++) {
			SudokuCell sudokuCell = isRow ? getCell(line, cross) : getCell(cross, line);
			if (sudokuCell.getCellIndex() != cellIndex) {
				sudokuCell.setOptionalValuesEmpty(value);
			}
		}
	}

}
