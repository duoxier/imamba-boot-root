package com.imamba.boot.poi;

public class CelllMerge {
    private CelllMerge.MergeType type;
    private int rowIndex;
    private int cellIndex;

    public CelllMerge(CelllMerge.MergeType type) {
        this.type = type;
    }

    public CelllMerge(CelllMerge.MergeType type, int rowIndex, int cellIndex) {
        this.type = type;
        this.rowIndex = rowIndex;
        this.cellIndex = cellIndex;
    }

    public int getRowIndex() {
        return this.rowIndex;
    }

    public int getCellIndex() {
        return this.cellIndex;
    }

    public CelllMerge.MergeType getType() {
        return this.type;
    }

    public void setType(CelllMerge.MergeType type) {
        this.type = type;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public void setCellIndex(int cellIndex) {
        this.cellIndex = cellIndex;
    }

    public static enum MergeType {
        START,
        END;

        private MergeType() {
        }
    }
}