package com.imamba.boot.poi;

import com.imamba.boot.common.exception.MError;
import com.imamba.boot.common.exception.MException;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by xiezhouyan on 15-12-10.
 */
public class ExcelExport {

    protected final Logger logger = LoggerFactory.getLogger(ExcelExport.class);

    private Collection<?> values;

    private List<ColumnToField> fields;

    private String title;

    private int rowIndex = 0;
    private int cellIndex = 0;
    private Map<Integer, Row> rows = new HashMap<Integer, Row>();
    private Map<String, Object> datas = new HashMap<>();

    private CellStyle cellStyle;

    public ExcelExport(String title, List<ColumnToField> fields) {
        this.title = title;
        this.fields = fields;
    }

    public void write(Collection<?> datas, OutputStream out) throws IOException {
        this.values = datas;
        List<String> headers = new ArrayList<String>();
        setHeaders(headers, fields);
        String[] result = new String[headers.size()];
        write(title, headers.toArray(result), out);
    }

    private void setHeaders(List<String> headers, List<ColumnToField> fields) {
        for (ColumnToField field : fields) {
            if (!field.getIsShow()) {
                continue;
            }
            if (field.getFields() != null && field.getFields().size() != 0) {
                setHeaders(headers, field.getFields());
            } else {
                headers.add(field.getColname());
            }
        }
    }

    private XSSFRow getRow(XSSFSheet sheet, Integer rowIndex) {
        Object o = MapUtils.getObject(rows, rowIndex);
        if (o == null) {
            XSSFRow row = sheet.createRow(rowIndex);
            rows.put(rowIndex, row);
            return row;
        } else {
            return (XSSFRow) o;
        }
    }

    private void write(String title, String[] headers, OutputStream out) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(title);
        sheet.setDefaultColumnWidth(15);
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = row.createCell(i);
            XSSFRichTextString text = new XSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        this.cellStyle = cellStyle;
        write(workbook, sheet, values, fields);
        workbook.write(out);
    }

    private void write(Workbook workbook, XSSFSheet sheet, Collection<?> datas, List<ColumnToField> fields) throws IOException {
        Iterator<?> it = datas.iterator();
        while (it.hasNext()) {
            rowIndex++;
            cellIndex = 0;
            int firstCol = new Integer(cellIndex);
            int maxRow = new Integer(rowIndex);
            List<CellRangeAddress> ranges = null;
            XSSFRow row = getRow(sheet, rowIndex);
            Object t = it.next();
            for (ColumnToField colField : fields) {
                if (!colField.getIsShow()) {
                    continue;
                }
                String fieldName = colField.getFieldName();
                Object value = getProperty(fieldName, t);
                if (colField.getFields() != null && colField.getFields().size() != 0) {
                    Collection<?> values = ((Collection<?>) value);
                    if (ranges == null) {
                        ranges = new ArrayList<CellRangeAddress>();
                    }
                    int firstRow = new Integer(rowIndex);
                    int lastRow = new Integer(rowIndex + values.size() - 1);
                    if (firstCol < cellIndex) {
                        for (int i = firstCol; i < cellIndex; i++) {
                            int lastCol = new Integer(i);
                            if (lastRow > maxRow) {
                                maxRow = lastRow;
                            }
                            ranges.add(new CellRangeAddress(firstRow, lastRow, lastCol, lastCol));
                        }
                    } else {
                        if (lastRow > maxRow) {
                            maxRow = lastRow;
                        }
                    }
                    if (values != null && values.size() != 0) {
                        write(workbook, sheet, values, colField.getFields(), new Integer(rowIndex));
                    }
                    cellIndex = cellIndex + colField.getFields().size();
                    firstCol = new Integer(cellIndex);
                } else {
                    try {
                        CellValue cellValue = colField.getCellValue();
                        XSSFCell cell = row.createCell(cellIndex);
                        cell.setCellStyle(cellStyle);
                        cellValue.setValue(this, workbook, cell, value);
                    } catch (Exception e) {
                        logger.error("export excel error and the values is " + value, e);
                        throw new MException(MError.WRITE_EXCEL_ERROR);
                    } finally {
                        cellIndex++;
                    }
                }
            }
            if (ranges != null && ranges.size() != 0) {
                for (int i = firstCol; i < cellIndex; i++) {
                    int firstRow = new Integer(rowIndex);
                    int lastRow = maxRow;
                    int lastCol = new Integer(i);
                    ranges.add(new CellRangeAddress(firstRow, lastRow, lastCol, lastCol));
                }
                for (CellRangeAddress range : ranges) {
                    range.setLastRow(maxRow);
                    sheet.addMergedRegion(range);
                }
            }
            rowIndex = maxRow;
        }

    }

    private void write(Workbook workbook, XSSFSheet sheet, Collection<?> datas, List<ColumnToField> fields, int rowIndex) {
        Iterator<?> it = datas.iterator();
        while (it.hasNext()) {
            int cellIndex = new Integer(this.cellIndex);
            XSSFRow row = getRow(sheet, rowIndex);
            Object t = it.next();
            for (ColumnToField colField : fields) {
                if (!colField.getIsShow()) {
                    continue;
                }
                String fieldName = colField.getFieldName();
                Object value = getProperty(fieldName, t);
                try {
                    CellValue cellValue = colField.getCellValue();
                    XSSFCell cell = row.createCell(cellIndex);
                    cell.setCellStyle(cellStyle);
                    cellValue.setValue(this, workbook, cell, value);
                } catch (Exception e) {
                    logger.error("export excel error and the values is " + value, e);
                    throw new MException(MError.WRITE_EXCEL_ERROR);
                } finally {
                    cellIndex++;
                }
            }
            rowIndex++;
        }
    }

    private Object getProperty(String fieldName, Object t) {
        Object value = null;
        try {
            value = MVEL.getProperty(fieldName, t);
        } catch (Exception e) {
            logger.trace("get val with mvel error", e);
        }
        return value;
    }

    public <T> T data(String key) {
        return (T) datas.get(key);
    }

    public void data(String key, Object o) {
        this.datas.put(key, o);
    }
}