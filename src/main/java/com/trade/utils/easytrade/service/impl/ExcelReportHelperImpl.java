package com.trade.utils.easytrade.service.impl;

import com.trade.utils.easytrade.exception.SystemException;
import com.trade.utils.easytrade.model.report.ReportDetails;
import com.trade.utils.easytrade.model.report.TransactionRecord;
import com.trade.utils.easytrade.service.CGSpreadSheetHelper;
import com.trade.utils.easytrade.service.ExcelReportHelper;
import org.apache.poi.xssf.streaming.SXSSFSheet;
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
import java.util.List;
import java.util.Map;

@Component
public class ExcelReportHelperImpl implements ExcelReportHelper {

    @Value("${report.dir}")
    private String reportDir;

    @Inject
    private Provider<CGSpreadSheetHelper> cgSpreadSheetHelperProvider;

    @Override
    public void generateMappedTransactionsReport(ReportDetails reportDetails) {
        SXSSFWorkbook wb = new SXSSFWorkbook(-1);

        CGSpreadSheetHelper cgSpreadSheetHelper = createCGSpreadSheetHelper(wb, reportDetails.getMappedTransactions());
        cgSpreadSheetHelper.createSpreadSheet("CG");

        saveWorkbook(wb);
    }

    private void saveWorkbook(SXSSFWorkbook wb) {
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

    private CGSpreadSheetHelper createCGSpreadSheetHelper(SXSSFWorkbook wb,
            Map<TransactionRecord, List<TransactionRecord>> mappedTransactions) {
        CGSpreadSheetHelper cgSpreadSheetHelper = cgSpreadSheetHelperProvider.get();
        cgSpreadSheetHelper.setMappedTransactions(mappedTransactions);
        cgSpreadSheetHelper.setWorkbook(wb);
        return cgSpreadSheetHelper;
    }
}
