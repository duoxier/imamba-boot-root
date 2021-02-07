package com.imamba.boot.poi;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class RowCall {
    private Sheet sheet;
    private int sheetIndex;
    private Row row;
    private int rowIndex;

    public RowCall(Sheet sheet, int sheetIndex, Row row, int rowIndex) {
        this.sheet = sheet;
        this.sheetIndex = sheetIndex;
        this.row = row;
        this.rowIndex = rowIndex;
    }

    public Sheet getSheet() {
        return this.sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    public int getSheetIndex() {
        return this.sheetIndex;
    }

    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public Row getRow() {
        return this.row;
    }

    public void setRow(Row row) {
        this.row = row;
    }

    public int getRowIndex() {
        return this.rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }
}