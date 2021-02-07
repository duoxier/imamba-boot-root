package com.imamba.boot.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

public interface CellValue {
    void setValue(ExcelExport var1, Workbook var2, Cell var3, Object var4);
}