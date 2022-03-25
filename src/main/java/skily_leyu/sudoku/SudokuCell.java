package main.java.skily_leyu.sudoku;

import java.awt.Color;

public class SudokuCell {
    public static final int EMPTY = 0;

    private int now;
    private int[] optionalValues;
    private int row;
    private int column;
    private boolean isTrue;
    private boolean isFixed;
    private int baseColor;
    private int updateColor;
    private long updateTime;
    private int udpateTick;

    public SudokuCell(int row, int column) {
        this.now=EMPTY;
        this.optionalValues = new int[] {1,2,3,4,5,6,7,8,9};
        this.row = row;
        this.column = column;
        this.isTrue = true;
        this.isFixed = false;
        this.baseColor = Color.WHITE.getRGB();
        this.updateColor = EMPTY;
        this.updateTime = EMPTY;
        this.udpateTick = EMPTY;
    }

}
