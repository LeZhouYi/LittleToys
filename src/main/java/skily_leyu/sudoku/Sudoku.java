package main.java.skily_leyu.sudoku;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

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
	 * @param row
	 * @param column
	 * @return
	 */
	public SudokuCell getCell(int row, int column) {
		return this.sudokuCells.get(row * 9 + column);
	}

	/**
	 * 检查某格子的输入值或可选值是否可以填入或更新
	 * @param sudokuCell 要检查的格子
	 * @param checkValue 要检查的输入值/可选值
	 * @return
	 */
	public boolean checkNowSet(SudokuCell sudokuCell, int checkValue) {
		if (checkValue != SudokuCell.EMPTY) {
			int row = sudokuCell.getRow();
			int column = sudokuCell.getColumn();
			for (int teRow = 0; teRow < 9; teRow++) {
				if (teRow!=row&&this.getCell(teRow, column).getNow() == checkValue) {
					return false;
				}
			}
			for (int teColumn = 0; teColumn < 9; teColumn++) {
				if (teColumn!=column&&this.getCell(row, teColumn).getNow() == checkValue) {
					return false;
				}
			}
			int cellIndex = sudokuCell.getCellIndex();
			int cellRow = (cellIndex/3)*3;
			int cellColumn = (cellIndex%3)*3;
			for (int teRow = 0; teRow < 3; teRow++) {
				for (int teColumn = 0; teColumn < 3; teColumn++) {
					if (teRow+cellRow!=row&&teColumn+cellColumn!=column&&this.getCell(teRow+cellRow, teColumn+cellColumn).getNow() == checkValue) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * 更新当前格子有输入后其它格子的可选值
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
		int cellRow = (cellIndex/3)*3;
		int cellColumn = (cellIndex%3)*3;
		for (int teRow = 0; teRow < 3; teRow++) {
			for (int teColumn = 0; teColumn < 3; teColumn++) {
				this.getCell(teRow+cellRow, teColumn+cellColumn).setOptionalValuesEmpty(updateValue);
			}
		}
	}

	/**
	 * 更新当前格子被清空输入后其它格子的可选值
	 * @param sudokuCell
	 * @param before
	 */
	public void rebackOptionalValues(SudokuCell sudokuCell, int before) {
		int row = sudokuCell.getRow();
		int column = sudokuCell.getColumn();
		for (int teRow = 0; teRow < 9; teRow++) {
			SudokuCell teCell = this.getCell(teRow, column);
			if(this.checkNowSet(teCell, before)) {
				teCell.setOptionalValuesValue(before);
			}
		}
		for (int teColumn = 0; teColumn < 9; teColumn++) {
			SudokuCell teCell = this.getCell(row, teColumn);
			if(this.checkNowSet(teCell, before)) {
				teCell.setOptionalValuesValue(before);
			}
		}
		int cellIndex = sudokuCell.getCellIndex();
		int cellRow = (cellIndex/3)*3;
		int cellColumn = (cellIndex%3)*3;
		for (int teRow = 0; teRow < 3; teRow++) {
			for (int teColumn = 0; teColumn < 3; teColumn++) {
				SudokuCell teCell = this.getCell(teRow+cellRow, teColumn+cellColumn);
				if(this.checkNowSet(teCell, before)) {
					teCell.setOptionalValuesValue(before);
				}
			}
		}
	}

	/**
	 * 获取所有宫未填格中有一个可选值的格子的坐标
	 * 会更新该格子宫内其它格子的可选值
	 * 会设定该格子显示的短暂颜色
	 * @return
	 */
	public List<Point> showCellInferredValues() {
		List<Point> tePoints = new ArrayList<>();
		for(int cellIndex=0;cellIndex<9;cellIndex++) {
			for(int value = 1;value<=9;value++) {
				Point point = fintCellInferredValues(cellIndex, value);
				if(point!=null) {
					this.getCell(point.x, point.y).setOtherValuesEmpty(value);
					tePoints.add(point);
				}
			}
		}
		for(int row=0;row<9;row++){
			for(int column=0;column<9;column++){
				SudokuCell sudokuCell = this.getCell(row, column);
				if(sudokuCell.getNow()==SudokuCell.EMPTY&&sudokuCell.getOptionalValuesLength()==1){
					tePoints.add(new Point(sudokuCell.getRow(),sudokuCell.getColumn()));
				}
			}
		}
		return tePoints;
	}

	/**
	 * 获取某宫内只有一个可选值且等于findValues的格子
	 * @param cellIndex
	 * @param findValues
	 * @return
	 */
	public Point fintCellInferredValues(int cellIndex,int findValues) {
		int cellRow = (cellIndex/3)*3;
		int cellColumn = (cellIndex%3)*3;
		Point point = null;
		int amount = 0;
		for (int teRow = 0; teRow < 3; teRow++) {
			for (int teColumn = 0; teColumn < 3; teColumn++) {
				SudokuCell teCell = this.getCell(teRow+cellRow, teColumn+cellColumn);
				if(teCell.getNow()==findValues) {
					return null;
				}else if(teCell.getNow()==SudokuCell.EMPTY&&teCell.isOptionalExist(findValues)) {
					point = new Point(teCell.getRow(), teCell.getColumn());
					amount++;
				}
			}
		}
		return amount==1?point:null;
	}

	/**
	 * 根据所有宫内的可选对依次更新格子的可选值
	 * 会更新宫内及宫外格子的可选值
	 * 会显示短暂更新的颜色
	 * @return
	 */
	public List<Point> updateCellMatchIndex(){
		List<Point> tePoints = new ArrayList<>();
		for(int cellIndex=0;cellIndex<9;cellIndex++) {
			List<Point> cellPoints = new ArrayList<>();
			int cellRow = (cellIndex/3)*3;
			int cellColumn = (cellIndex%3)*3;
			//寻找宫内唯二可选值的格子
			for (int teRow = 0; teRow < 3; teRow++) {
				for (int teColumn = 0; teColumn < 3; teColumn++) {
					SudokuCell sudokuCell = this.getCell(teRow+cellRow, teColumn+cellColumn);
					if(sudokuCell.getNow()!=SudokuCell.EMPTY&&sudokuCell.getOptionalValuesLength()==1){
						tePoints.add(new Point(sudokuCell.getRow(),sudokuCell.getColumn()));
					}
				}
			}
			if(cellPoints.size()<=1) {
				continue;
			}
		}
		return tePoints;
	}
	
}
