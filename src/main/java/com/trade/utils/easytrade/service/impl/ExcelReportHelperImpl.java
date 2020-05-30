package com.trade.utils.easytrade.service.impl;

import com.google.common.collect.ImmutableList;
import com.trade.utils.easytrade.exception.SystemException;
import com.trade.utils.easytrade.model.report.TransactionRecord;
import com.trade.utils.easytrade.service.ExcelReportHelper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Component
public class ExcelReportHelperImpl implements ExcelReportHelper {

    public static final int DATA_START_ROW_NUM = 5;

    public static final int ID_CELL_NUM = 0;
    public static final int SCRIP_NAME_CELL_NUM = 1;

    public static final int TRANS_DETAILS_CELL_COUNT = 4;
    public static final int SELL_TRANS_BASE_CELL_NUM = SCRIP_NAME_CELL_NUM + 1;
    public static final int BUY_TRANS_BASE_CELL_NUM = SELL_TRANS_BASE_CELL_NUM + TRANS_DETAILS_CELL_COUNT + 1;
    public static final int BUY_TRANS_SUM_AMOUNT_CELL_NUM = BUY_TRANS_BASE_CELL_NUM + TRANS_DETAILS_CELL_COUNT + 1;
    public static final int CG_CELL_NUM = BUY_TRANS_SUM_AMOUNT_CELL_NUM + 2;

    @Value("${report.dir}")
    private String reportDir;

    @Override
    public void generateMappedTransactionsReport(Map<TransactionRecord, List<TransactionRecord>> mappedTransactions) {
        SXSSFWorkbook wb = new SXSSFWorkbook(-1);
        SXSSFSheet sheet = wb.createSheet("CG");
        sheet.trackAllColumnsForAutoSizing();

        int rowNum = DATA_START_ROW_NUM;
        int id = 1;
        Cell firstCGCell = null;
        Cell lastCGSell = null;
        for (Map.Entry<TransactionRecord, List<TransactionRecord>> mappedTransaction : mappedTransactions.entrySet()) {
            TransactionRecord sellTransaction = mappedTransaction.getKey();

            Row row = sheet.createRow(rowNum);

            Cell idCell = row.createCell(ID_CELL_NUM);
            idCell.setCellValue(id);

            Cell scripNameCell = row.createCell(SCRIP_NAME_CELL_NUM);
            scripNameCell.setCellValue(sellTransaction.getScripName());

            List<Cell> sellTransactionCells = createTransactionDetails(
                    wb, row, SELL_TRANS_BASE_CELL_NUM, sellTransaction);
            Cell sellTransTotalAmountCell = sellTransactionCells.get(sellTransactionCells.size() - 1);

            Cell firstBuyTransTotalAmountCell = null;
            Cell lastBuyTransTotalAmountCell = null;
            Row buyTransactionRow = row;
            for (TransactionRecord buyTransaction: mappedTransaction.getValue()) {
                List<Cell> cells = createTransactionDetails(
                        wb, buyTransactionRow, BUY_TRANS_BASE_CELL_NUM, buyTransaction);

                if (firstBuyTransTotalAmountCell == null) {
                    firstBuyTransTotalAmountCell = cells.get(cells.size() - 1);
                }
                lastBuyTransTotalAmountCell = cells.get(cells.size() - 1);

                rowNum++;
                buyTransactionRow = sheet.createRow(rowNum);
            }

            String totalPriceFormula = "ROUND (SUM (" + getRef(firstBuyTransTotalAmountCell)
                    + ":" + getRef(lastBuyTransTotalAmountCell) + "), 2)";
            Cell buyTransactionSumAmountCell = row.createCell(BUY_TRANS_SUM_AMOUNT_CELL_NUM);
            buyTransactionSumAmountCell.setCellFormula(totalPriceFormula);

            String cgFormula = "ROUND (" + getRef(sellTransTotalAmountCell)
                    + " - " + getRef(buyTransactionSumAmountCell) + ", 2)";
            Cell cgCell = row.createCell(CG_CELL_NUM);
            cgCell.setCellFormula(cgFormula);

            if (firstCGCell == null) {
                firstCGCell = cgCell;
            }
            lastCGSell = cgCell;

            rowNum++;
            id++;
        }

        Row row = sheet.createRow(rowNum);
        String totalCGFormula = "ROUND (SUM (" + getRef(firstCGCell)
                + ":" + getRef(lastCGSell) + "), 2)";
        Cell cgCell = row.createCell(CG_CELL_NUM);
        cgCell.setCellFormula(totalCGFormula);

        IntStream.range(1, CG_CELL_NUM).forEach(colNum -> {
            if (colNum == (SELL_TRANS_BASE_CELL_NUM + TRANS_DETAILS_CELL_COUNT)
                    || colNum >= (BUY_TRANS_SUM_AMOUNT_CELL_NUM - 1)) {
                sheet.setColumnWidth(colNum, 15);
            } else {
                sheet.autoSizeColumn(colNum);
            }
        });


        try {
            Path reportDirPath = Paths.get(reportDir);
            Files.createDirectories(reportDirPath);
            File report = reportDirPath.resolve("report3.xlsx").toFile();
            FileOutputStream out = new FileOutputStream(report);
            wb.write(out);
            out.close();

            wb.dispose();
        } catch (Exception e) {
            throw new SystemException("Error while generating report", e);
        }
    }

    private String getRef(Cell cell) {
        return new CellReference(cell).formatAsString(false);
    }

    private List<Cell> createTransactionDetails(SXSSFWorkbook wb, Row row, int baseColNum, TransactionRecord record) {
        int colNum = baseColNum + 1;

        Cell transactionDateCell = row.createCell(colNum);
        DataFormat df = wb.createDataFormat();
        CellStyle dateStyle = wb.createCellStyle();
        dateStyle.setDataFormat(df.getFormat("d-mmm-yy"));
        transactionDateCell.setCellStyle(dateStyle);
        transactionDateCell.setCellValue(record.getTransactionDate());
        colNum++;

        Cell quantityCell = row.createCell(colNum);
        quantityCell.setCellValue(record.getQuantity());
        colNum++;

        Cell unitPriceCell = row.createCell(colNum);
        unitPriceCell.setCellValue(record.getUnitPrice());
        colNum++;

        String totalPriceFormula = "ROUND (" + getRef(quantityCell) + " * " + getRef(unitPriceCell) + ", 2)";

        Cell totalPriceCell = row.createCell(colNum);
        totalPriceCell.setCellFormula(totalPriceFormula);

        return ImmutableList.of(transactionDateCell, quantityCell, unitPriceCell, totalPriceCell);
    }
}
