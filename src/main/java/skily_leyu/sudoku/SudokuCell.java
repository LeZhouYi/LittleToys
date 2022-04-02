package main.java.skily_leyu.sudoku;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SudokuCell implements Cloneable {
	public static final int EMPTY = 0;
	public static final Color COLOR_BASE = new Color(0x66CCCC);
	public static final Color COLOR_BACKGROUND = new Color(0xFFFFFF);
	public static final Color COLOR_FIXED = new Color(0x99CCCC);

	/**
	 * 根据比例返回当可选的渐变颜色
	 *
	 * @param base      基础色
	 * @param backgroud 渐变方向色
	 * @param nowStep   当前步数
	 * @param step      总步长
	 * @return
	 */
	public static Color getStepColor(Color base, Color backgroud, int nowStep, float step) {
		int red = base.getRed() - backgroud.getRed();
		int blue = base.getBlue() - backgroud.getBlue();
		int green = base.getGreen() - backgroud.getGreen();
		red = (int) ((red / step) * (step - nowStep));
		blue = (int) ((blue / step) * (step - nowStep));
		green = (int) ((green / step) * (step - nowStep));
		return new Color(base.getRed() - red, base.getGreen() - green, base.getBlue() - blue);
	}

	/**
	 * 是否为空值
	 *
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(int value) {
		return value == EMPTY;
	}

	private int now;
	private int[] optionalValues;
	private int row;
	private int column;
	private boolean isTrue;
	private boolean isFixed;
	private Color updateColor;

	private long updateTime;

	private int updateTick;

	public SudokuCell(int row, int column) {

		this.now = EMPTY;
		this.optionalValues = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		this.row = row;
		this.column = column;
		this.isTrue = true;
		this.isFixed = false;
		this.updateColor = COLOR_BACKGROUND;
		this.updateTime = EMPTY;
		this.updateTick = EMPTY;

	}

	@Override
	protected SudokuCell clone() throws CloneNotSupportedException {
		SudokuCell sudokuCell = new SudokuCell(row, column);
		sudokuCell.now = now;
		sudokuCell.optionalValues = optionalValues.clone();
		sudokuCell.isTrue = isTrue;
		sudokuCell.isFixed = isFixed;
		sudokuCell.updateColor = updateColor;
		sudokuCell.updateTime = updateTime;
		sudokuCell.updateTick = updateTick;
		return sudokuCell;
	}

	/**
	 * 获得当前格子所在的宫
	 *
	 * @return [0,8]
	 */
	public int getCellIndex() {
		return this.row / 3 * 3 + this.column / 3;
	}

	/**
	 * 获取当前宫的起始坐标
	 *
	 * @return
	 */
	public Point getCellPoint() {
		int cellIndex = getCellIndex();
		return new Point((cellIndex / 3) * 3, (cellIndex % 3) * 3);
	}

	/**
	 * 返回当前格子要显示的颜色
	 *
	 * @return
	 */
	public Color getColor() {
		if (now == EMPTY) {
			Color updateColor = getUpdateColor();
			if (updateColor != null) {
				return updateColor;
			}
			if (getOptionalValuesLength() <= 3) {
				return getStepColor(COLOR_BASE, COLOR_BACKGROUND, getOptionalValuesLength(), 16.0F);
			}
		} else if (isFixed) {
			return COLOR_FIXED;
		}
		return COLOR_BACKGROUND;
	}

	/**
	 * 当前格子所在的列
	 *
	 * @return [0,8]
	 */
	public int getColumn() {
		return this.column;
	}

	/**
	 * 获得当前格子显示的值
	 *
	 * @return
	 */
	public int getNow() {
		return this.now;
	}

	/**
	 * 返回当前格子要显示的字符串
	 *
	 * @return
	 */
	public String getNowString() {
		return this.now != EMPTY ? String.valueOf(this.now) : "";
	}

	/**
	 * 获得当前格子可输入的可选值
	 *
	 * @return
	 */
	public int[] getOptionalValues() {
		return now != EMPTY && isTrue ? null : this.optionalValues;
	}

	/**
	 * 获取当前格子不含Empty的可选值数组
	 *
	 * @return
	 */
	public List<Integer> getOptionalValuesArray() {
		List<Integer> arrays = new ArrayList<>();
		for (int value : optionalValues) {
			if (value != EMPTY) {
				arrays.add(value);
			}
		}
		return arrays;
	}

	/**
	 * 获得当前格子可选值的长度
	 *
	 * @return
	 */
	public int getOptionalValuesLength() {
		int length = 0;
		for (int value : this.optionalValues) {
			if (value != EMPTY) {
				length++;
			}
		}
		return length;
	}

	/**
	 * 获取当前格子行列的坐标形式值
	 *
	 * @return
	 */
	public Point getPoint() {
		return new Point(row, column);
	}

	/**
	 * 当前格子所在的行
	 *
	 * @return [0,8]
	 */
	public int getRow() {
		return this.row;
	}

	/**
	 * 获得当前短暂显示的颜色，若有
	 *
	 * @return
	 */
	public Color getUpdateColor() {
		long nowTick = new Date().getTime() / 100 - this.updateTime;
		if (this.updateTick != EMPTY) {
			if (nowTick < this.updateTick * 10) {
				return getStepColor(this.updateColor, COLOR_BACKGROUND, (int) nowTick, this.updateTick * 10);
			} else {
				this.updateTick = EMPTY;
			}
		}
		return null;
	}

	/**
	 * 判断当前输入值是否为空
	 *
	 * @return
	 */
	public boolean isEmpty() {
		return this.now == EMPTY;
	}

	/**
	 * 当前格子是否被锁定
	 *
	 * @return
	 */
	public boolean isFixed() {
		if (this.now != EMPTY) {
			return this.isFixed;
		}
		return false;
	}

	/**
	 * 查找可选值中是否存在该值
	 *
	 * @param findValues 要寻找的值[1,9]
	 * @return
	 */
	public boolean isOptionalExist(int findValues) {
		if (findValues != EMPTY) {
			return this.optionalValues[findValues - 1] == findValues;
		}
		return false;
	}

	/**
	 * 锁定/解锁当前格子
	 */
	public void setIsFixed() {
		if (this.now != EMPTY) {
			this.isFixed = !this.isFixed;
		}
	}

	/**
	 * 设置当前格子是否是正确的
	 *
	 * @param isTure
	 */
	public void setIsTrue(boolean isTure) {
		this.isTrue = isTure;
	}

	/**
	 * 设置当前格子显示值并清除错误标记
	 *
	 * @param now
	 */
	public void setNow(int now) {
		this.now = now;
		this.setIsTrue(true);
	}

	/**
	 * 消除当前可选值
	 *
	 * @param updateValues[1,9]
	 */
	public void setOptionalValuesEmpty(int updateValues) {
		if (updateValues != EMPTY) {
			this.optionalValues[updateValues - 1] = EMPTY;
		}
	}

	/**
	 * 增加当前可选值
	 *
	 * @param optinalValue 当前可选值
	 */
	public void setOptionalValuesValue(int optinalValue) {
		if (optinalValue != EMPTY) {
			this.optionalValues[optinalValue - 1] = optinalValue;
		}
	}

	/**
	 * 将当前值外的值设置为EMPTY
	 *
	 * @param value 当前值[1,9]
	 */
	public void setOtherValuesEmpty(int value) {
		if (value != EMPTY) {
			for (int teValue : this.optionalValues) {
				if (teValue != EMPTY && teValue != value) {
					this.optionalValues[teValue - 1] = EMPTY;
				}
			}
		}
	}

	/**
	 * 设置短暂更新的颜色
	 *
	 * @param color   基本色
	 * @param nowTime 设置颜色的时间
	 * @param second  显示该颜色的秒数
	 */
	public void setUpdateColor(Color color, long nowTime, int second) {
		this.updateColor = color;
		this.updateTime = nowTime;
		this.updateTick = second;
	}

}
