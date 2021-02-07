package com.imamba.boot.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultCellValue implements CellValue {
    private String pattern = "yyyy-MM-dd HH:mm:ss";
    private final SimpleDateFormat sdf;

    public DefaultCellValue() {
        this.sdf = new SimpleDateFormat(this.pattern);
    }

    public DefaultCellValue(String pattern) {
        this.sdf = new SimpleDateFormat(this.pattern);
        this.pattern = pattern;
    }

    public void setValue(ExcelExport export, Workbook workbook, Cell cell, Object value) {
        String cellValue = null;
        if (value == null) {
            cellValue = "";
        } else if (value instanceof Boolean) {
            cellValue = value.toString();
        } else if (value instanceof Date) {
            Date date = (Date)value;
            cellValue = this.sdf.format(date);
        } else if (EnumText.class.isAssignableFrom(value.getClass())) {
            cellValue = ((EnumText)value).text();
        } else {
            cellValue = value.toString();
        }

        if (cellValue != null) {
            Pattern p = Pattern.compile("^//d+(//.//d+)?$");
            Matcher matcher = p.matcher(cellValue);
            if (matcher.matches()) {
                cell.setCellValue(Double.parseDouble(cellValue));
            } else {
                cell.setCellValue(cellValue);
            }
        }

    }
}