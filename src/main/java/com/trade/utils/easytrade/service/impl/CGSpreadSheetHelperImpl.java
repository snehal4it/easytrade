package com.trade.utils.easytrade.service.impl;

import com.google.common.collect.ImmutableList;
import com.trade.utils.easytrade.model.report.TransactionRecord;
import com.trade.utils.easytrade.service.CGSpreadSheetHelper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.collections4.MapUtils.isEmpty;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Scope(SCOPE_PROTOTYPE)
@Component
public class CGSpreadSheetHelperImpl implements CGSpreadSheetHelper {
    public static final int DATA_START_ROW_NUM = 5;

    public static final int ID_CELL_NUM = 0;
    public static final int SCRIP_NAME_CELL_NUM = 1;

    public static final int TRANS_DETAILS_CELL_COUNT = 4;
    public static final int SELL_TRANS_BASE_CELL_NUM = SCRIP_NAME_CELL_NUM + 1;
    public static final int BUY_TRANS_BASE_CELL_NUM = SELL_TRANS_BASE_CELL_NUM + TRANS_DETAILS_CELL_COUNT + 1;
    public static final int BUY_TRANS_SUM_AMOUNT_CELL_NUM = BUY_TRANS_BASE_CELL_NUM + TRANS_DETAILS_CELL_COUNT + 1;
    public static final int CG_CELL_NUM = BUY_TRANS_SUM_AMOUNT_CELL_NUM + 2;

    public static final int SELL_TRANS_TOTAL_AMOUNT_CELL = SELL_TRANS_BASE_CELL_NUM + TRANS_DETAILS_CELL_COUNT;

    private Map<TransactionRecord, List<TransactionRecord>> mappedTransactions;

    private SXSSFWorkbook workbook;

    private SXSSFSheet sheet;

    private Cell firstCGCell = null;
    private Cell lastCGSell = null;

    private Cell firstSellTransTotalAmountCell = null;
    private Cell lastSellTransTotalAmountCell = null;

    private Cell firstBuyTransactionSumAmountCell = null;
    private Cell lastBuyTransactionSumAmountCell = null;

    @Override
    public void createSpreadSheet(String name) {
        checkNotNull(name);
        checkNotNull(workbook);
        checkNotNull(mappedTransactions);

        sheet = workbook.createSheet(name);
        if (isEmpty(mappedTransactions)) {
            // if no transaction, return
            return;
        }

        sheet.trackAllColumnsForAutoSizing();

        createTransactionDetails();

        adjustColumnWidth();
    }

    private void createTransactionDetails() {
        int rowNum = DATA_START_ROW_NUM;
        int id = 1;

        for (Map.Entry<TransactionRecord, List<TransactionRecord>> mappedTransaction : mappedTransactions.entrySet()) {
            TransactionRecord sellTransaction = mappedTransaction.getKey();

            Row row = sheet.createRow(rowNum);

            Cell idCell = row.createCell(ID_CELL_NUM);
            idCell.setCellValue(id);

            Cell scripNameCell = row.createCell(SCRIP_NAME_CELL_NUM);
            scripNameCell.setCellValue(sellTransaction.getScripName());

            List<Cell> sellTransactionCells = createTransactionDetails(row, SELL_TRANS_BASE_CELL_NUM, sellTransaction);
            Cell sellTransTotalAmountCell = sellTransactionCells.get(sellTransactionCells.size() - 1);

            if (firstSellTransTotalAmountCell == null) {
                firstSellTransTotalAmountCell = sellTransTotalAmountCell;
            }
            lastSellTransTotalAmountCell = sellTransTotalAmountCell;

            Cell firstBuyTransTotalAmountCell = null;
            Cell lastBuyTransTotalAmountCell = null;
            Row buyTransactionRow = row;
            for (TransactionRecord buyTransaction: mappedTransaction.getValue()) {
                List<Cell> cells = createTransactionDetails(buyTransactionRow, BUY_TRANS_BASE_CELL_NUM, buyTransaction);

                if (firstBuyTransTotalAmountCell == null) {
                    firstBuyTransTotalAmountCell = cells.get(cells.size() - 1);
                }
                lastBuyTransTotalAmountCell = cells.get(cells.size() - 1);

                rowNum++;
                buyTransactionRow = sheet.createRow(rowNum);
            }

            String totalPriceFormula = "ROUND(SUM(" + getRef(firstBuyTransTotalAmountCell)
                    + ":" + getRef(lastBuyTransTotalAmountCell) + "),2)";
            Cell buyTransactionSumAmountCell = row.createCell(BUY_TRANS_SUM_AMOUNT_CELL_NUM);
            buyTransactionSumAmountCell.setCellFormula(totalPriceFormula);

            if (firstBuyTransactionSumAmountCell == null) {
                firstBuyTransactionSumAmountCell = buyTransactionSumAmountCell;
            }
            lastBuyTransactionSumAmountCell = buyTransactionSumAmountCell;

            String cgFormula = "ROUND(" + getRef(sellTransTotalAmountCell)
                    + "-" + getRef(buyTransactionSumAmountCell) + ",2)";
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
        createTotalSellAmountCell(row);
        createTotalBuyAmountCell(row);
        createTotalCapitalGainCell(row);
    }

    private void createTotalBuyAmountCell(Row row) {
        String totalBuyAmountFormula = "ROUND(SUM(" + getRef(firstBuyTransactionSumAmountCell)
                + ":" + getRef(lastBuyTransactionSumAmountCell) + "),2)";
        Cell totalBuyTranAmountCell = row.createCell(BUY_TRANS_SUM_AMOUNT_CELL_NUM);
        totalBuyTranAmountCell.setCellFormula(totalBuyAmountFormula);
    }

    private void createTotalSellAmountCell(Row row) {
        String totalSellAmountFormula = "ROUND(SUM(" + getRef(firstSellTransTotalAmountCell)
                + ":" + getRef(lastSellTransTotalAmountCell) + "),2)";
        Cell totalSellTranAmountCell = row.createCell(SELL_TRANS_TOTAL_AMOUNT_CELL);
        totalSellTranAmountCell.setCellFormula(totalSellAmountFormula);
    }

    private void createTotalCapitalGainCell(Row row) {
        String totalCGFormula = "ROUND(SUM(" + getRef(firstCGCell)
                + ":" + getRef(lastCGSell) + "),2)";
        Cell cgCell = row.createCell(CG_CELL_NUM);
        cgCell.setCellFormula(totalCGFormula);
    }

    private void adjustColumnWidth() {
        IntStream.range(1, CG_CELL_NUM).forEach(colNum -> {
            if (colNum == SELL_TRANS_TOTAL_AMOUNT_CELL || colNum >= (BUY_TRANS_SUM_AMOUNT_CELL_NUM - 1)) {
                sheet.setColumnWidth(colNum, 12 * 256);
            } else {
                sheet.autoSizeColumn(colNum);
            }
        });
    }

    private String getRef(Cell cell) {
        return new CellReference(cell).formatAsString(false);
    }

    private List<Cell> createTransactionDetails(Row row, int baseColNum, TransactionRecord record) {
        int colNum = baseColNum + 1;

        Cell transactionDateCell = row.createCell(colNum);
        DataFormat df = workbook.createDataFormat();
        CellStyle dateStyle = workbook.createCellStyle();
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

        String totalPriceFormula = "ROUND(" + getRef(quantityCell) + "*" + getRef(unitPriceCell) + ",2)";

        Cell totalPriceCell = row.createCell(colNum);
        totalPriceCell.setCellFormula(totalPriceFormula);

        return ImmutableList.of(transactionDateCell, quantityCell, unitPriceCell, totalPriceCell);
    }

    @Override
    public void setMappedTransactions(Map<TransactionRecord, List<TransactionRecord>> mappedTransactions) {
        this.mappedTransactions = mappedTransactions;
    }

    @Override
    public void setWorkbook(SXSSFWorkbook workbook) {
        this.workbook = workbook;
    }
}
