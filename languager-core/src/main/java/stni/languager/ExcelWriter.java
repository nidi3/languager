package stni.languager;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 *
 */
public class ExcelWriter extends AbstractLowlevelWriter {
    private final OutputStream out;
    private final List<ExcelStyle> columnStyles;
    private final List<ExcelStyle> rowStyles;
    private final XSSFWorkbook wb;
    private final Sheet sheet;
    private int currentRow = -1;
    private int currentCol = 0;

    public ExcelWriter(OutputStream out, List<ExcelStyle> columnStyles, List<ExcelStyle> rowStyles) throws IOException {
        this.out = out;
        this.columnStyles = columnStyles;
        this.rowStyles = rowStyles;
        wb = new XSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        sheet = wb.createSheet("Translations");
        for (int i = 0; i < columnStyles.size(); i++) {
            sheet.setColumnWidth(i, columnStyles.get(i).getWidth() * 256);
        }
        newLine();
    }

    @Override
    public void writeField(String value) throws IOException {
        Cell cell = currentRow().createCell(currentCol);
        cell.setCellValue(value);
        cell.setCellStyle(createStyle());
        currentCol++;
    }

    private CellStyle createStyle() {
        return createStyle(columnStyle(currentCol), rowStyle(currentRow));
    }

    private ExcelStyle columnStyle(int col) {
        return columnStyles.size() > col ? columnStyles.get(col) : null;
    }

    private ExcelStyle rowStyle(int row) {
        return rowStyles.size() > row ? rowStyles.get(row) : null;
    }

    private CellStyle createStyle(ExcelStyle... excelStyles) {
        CellStyle cellStyle = wb.createCellStyle();
        for (ExcelStyle style : excelStyles) {
            if (style != null) {
                if (style.isBold()) {
                    XSSFFont font = wb.createFont();
                    font.setBold(true);
                    cellStyle.setFont(font);
                }
                if (style.isWordWrap()) {
                    cellStyle.setWrapText(true);
                }
            }
        }
        return cellStyle;
    }

    private Row currentRow() {
        return sheet.getRow(currentRow);
    }

    @Override
    public void newLine() throws IOException {
        currentCol = 0;
        currentRow++;
        sheet.createRow((short) currentRow);
    }

    @Override
    public void close() throws IOException {
        wb.write(out);
        out.close();
    }
}
