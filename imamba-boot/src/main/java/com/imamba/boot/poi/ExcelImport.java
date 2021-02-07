package com.imamba.boot.poi;

import com.imamba.boot.common.exception.MError;
import com.imamba.boot.common.exception.MException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.DocumentFactoryHelper;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.List;

public class ExcelImport {
    protected final Logger logger = LoggerFactory.getLogger(ExcelImport.class);
    private List<ColumnToField> fields;

    public ExcelImport() {
    }

    public void read(CellCallBack cellCallback, InputStream is) {
        this.read((RowCallBack)null, cellCallback, is);
    }

    public void read(RowCallBack rowCallBack, InputStream is) {
        this.read(rowCallBack, (CellCallBack)null, is);
    }

    public void read(RowCallBack rowCallBack, CellCallBack cellCallback, InputStream is) {
        Object workbook = null;

        try {
            if (!((InputStream)is).markSupported()) {
                is = new PushbackInputStream((InputStream)is, 8);
            }

            if (POIFSFileSystem.hasPOIFSHeader((InputStream)is)) {
                workbook = new HSSFWorkbook((InputStream)is);
            } else if (DocumentFactoryHelper.hasOOXMLHeader((InputStream)is)) {
                workbook = new XSSFWorkbook(OPCPackage.open((InputStream)is));
            }
        } catch (Throwable var15) {
            this.logger.warn("read excel with  error", var15);
            throw new MException(MError.READ_EXCEL_ERROR, var15);
        }

        int sheetTotalNum = ((Workbook)workbook).getNumberOfSheets();

        for(int i = 0; i < sheetTotalNum; ++i) {
            Sheet sheet = ((Workbook)workbook).getSheetAt(i);

            for(int rowIndex = 0; rowIndex < sheet.getPhysicalNumberOfRows(); ++rowIndex) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    if (rowCallBack != null) {
                        try {
                            rowCallBack.call(new RowCall(sheet, i, row, rowIndex));
                        } catch (Exception var13) {
                            this.logger.error("call rowcallback error", var13);
                            throw new MException(MError.READ_EXCEL_ERROR, var13);
                        }
                    }

                    if (cellCallback != null) {
                        for(int cellIndex = 0; cellIndex < row.getPhysicalNumberOfCells(); ++cellIndex) {
                            Cell cell = row.getCell(cellIndex);
                            if (cell != null) {
                                try {
                                    cellCallback.call(new CellCall(sheet, i, row, rowIndex, cell, cellIndex));
                                } catch (Exception var14) {
                                    this.logger.error("call cellcallback error", var14);
                                    throw new MException(MError.READ_EXCEL_ERROR, var14);
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}