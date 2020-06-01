package com.trade.utils.easytrade.service.impl;

import com.trade.utils.easytrade.exception.SystemException;
import com.trade.utils.easytrade.model.report.ReportDetails;
import com.trade.utils.easytrade.model.report.TransactionRecord;
import com.trade.utils.easytrade.service.CGSpreadSheetHelper;
import com.trade.utils.easytrade.service.ExcelReportHelper;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE;

@Component
public class ExcelReportHelperImpl implements ExcelReportHelper {

    private static final DateTimeFormatter DATE_FORMATTER = BASIC_ISO_DATE;//ofPattern("");

    @Value("${report.dir}")
    private String reportDir;

    @Inject
    private Provider<CGSpreadSheetHelper> cgSpreadSheetHelperProvider;

    @Override
    public void generateMappedTransactionsReport(ReportDetails reportDetails) {
        SXSSFWorkbook wb = new SXSSFWorkbook(-1);

        CGSpreadSheetHelper cgSpreadSheetHelper = createCGSpreadSheetHelper(wb, reportDetails.getMappedTransactions());
        cgSpreadSheetHelper.createSpreadSheet("CG");

        CGSpreadSheetHelper stCGSpreadSheetHelper = createCGSpreadSheetHelper(
                wb, reportDetails.getStCGMappedTransactions());
        stCGSpreadSheetHelper.createSpreadSheet("ST-CG");

        CGSpreadSheetHelper ltCGSpreadSheetHelper = createCGSpreadSheetHelper(
                wb, reportDetails.getLtCGMappedTransactions());
        ltCGSpreadSheetHelper.createSpreadSheet("LT-CG");

        CGSpreadSheetHelper speculativeCGSpreadSheetHelper = createCGSpreadSheetHelper(
                wb, reportDetails.getSpeculativeMappedTransactions());
        speculativeCGSpreadSheetHelper.createSpreadSheet("Speculative");

        saveWorkbook(wb, reportDetails);
    }

    private void saveWorkbook(SXSSFWorkbook wb, ReportDetails reportDetails) {
        StringBuilder reportName = new StringBuilder("cg_")
                .append(reportDetails.getStartDate().format(BASIC_ISO_DATE))
                .append('_')
                .append(reportDetails.getEndDate().format(BASIC_ISO_DATE))
                .append(".xlsx");
        String currentDate = LocalDate.now().format(BASIC_ISO_DATE);
        try {
            Path reportDirPath = Paths.get(reportDir + '/' + currentDate);
            Files.createDirectories(reportDirPath);
            File report = reportDirPath.resolve(reportName.toString()).toFile();
            FileOutputStream out = new FileOutputStream(report);
            wb.write(out);
            out.close();

            wb.dispose();
        } catch (Exception e) {
            throw new SystemException("Error while generating report", e);
        }
    }

    private CGSpreadSheetHelper createCGSpreadSheetHelper(SXSSFWorkbook wb,
            Map<TransactionRecord, List<TransactionRecord>> mappedTransactions) {
        CGSpreadSheetHelper cgSpreadSheetHelper = cgSpreadSheetHelperProvider.get();
        cgSpreadSheetHelper.setMappedTransactions(mappedTransactions);
        cgSpreadSheetHelper.setWorkbook(wb);
        return cgSpreadSheetHelper;
    }
}
