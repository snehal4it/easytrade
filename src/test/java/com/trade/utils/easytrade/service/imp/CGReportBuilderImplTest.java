package com.trade.utils.easytrade.service.imp;

import com.google.common.collect.ImmutableList;
import com.trade.utils.easytrade.model.report.ReportDetails;
import com.trade.utils.easytrade.model.report.TransactionRecord;
import com.trade.utils.easytrade.service.impl.CGReportBuilderImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CGReportBuilderImplTest {
    @InjectMocks
    private CGReportBuilderImpl reportBuilder;

    @Test
    public void emptyDataWhenNoTransactions() {
        java.time.LocalDate startDate = LocalDate.now().minus(5, ChronoUnit.MONTHS);
        LocalDate endDate = LocalDate.now();
        Map<TransactionRecord, List<TransactionRecord>> mappedTransactions = new HashMap<>();

        ReportDetails reportDetails = reportBuilder.buildReportDetails(startDate, endDate, mappedTransactions);
        assertThat(reportDetails).isNotNull();
        assertThat(reportDetails.getStartDate()).isEqualTo(startDate);
        assertThat(reportDetails.getEndDate()).isEqualTo(endDate);
        assertThat(reportDetails.getMappedTransactions()).isEmpty();
        assertThat(reportDetails.getStCGMappedTransactions()).isEmpty();
        assertThat(reportDetails.getLtCGMappedTransactions()).isEmpty();
        assertThat(reportDetails.getSpeculativeMappedTransactions()).isEmpty();
    }

    @Test
    public void verifyWhenOnlyShortTermTransactions() {
        java.time.LocalDate startDate = LocalDate.of(2019, 12, 1);
        LocalDate endDate = LocalDate.of(2020, 3, 31);
        Map<TransactionRecord, List<TransactionRecord>> mappedTransactions = buildShortTermTransactions();

        ReportDetails reportDetails = reportBuilder.buildReportDetails(startDate, endDate, mappedTransactions);
        assertThat(reportDetails).isNotNull();
        assertThat(reportDetails.getStartDate()).isEqualTo(startDate);
        assertThat(reportDetails.getEndDate()).isEqualTo(endDate);

        compareTransactionMap(reportDetails.getMappedTransactions(), mappedTransactions);
        compareTransactionMap(reportDetails.getStCGMappedTransactions(), mappedTransactions);
        assertThat(reportDetails.getLtCGMappedTransactions()).isEmpty();
        assertThat(reportDetails.getSpeculativeMappedTransactions()).isEmpty();
    }

    @Test
    public void verifyWhenOnlyLongTermTransactions() {
        java.time.LocalDate startDate = LocalDate.of(2019, 12, 1);
        LocalDate endDate = LocalDate.of(2020, 3, 31);
        Map<TransactionRecord, List<TransactionRecord>> mappedTransactions = buildLongTermTransactions();

        ReportDetails reportDetails = reportBuilder.buildReportDetails(startDate, endDate, mappedTransactions);
        assertThat(reportDetails).isNotNull();
        assertThat(reportDetails.getStartDate()).isEqualTo(startDate);
        assertThat(reportDetails.getEndDate()).isEqualTo(endDate);

        compareTransactionMap(reportDetails.getMappedTransactions(), mappedTransactions);
        assertThat(reportDetails.getStCGMappedTransactions()).isEmpty();
        compareTransactionMap(reportDetails.getLtCGMappedTransactions(), mappedTransactions);
        assertThat(reportDetails.getSpeculativeMappedTransactions()).isEmpty();
    }

    @Test
    public void verifyWhenBothShortTermAndLongTermTransactions() {
        java.time.LocalDate startDate = LocalDate.of(2019, 12, 1);
        LocalDate endDate = LocalDate.of(2020, 3, 31);
        ReportDetails expectedReportDetails = buildShortTermAndLongTermTransactions();

        ReportDetails actualReportDetails = reportBuilder.buildReportDetails(startDate, endDate,
                expectedReportDetails.getMappedTransactions());
        assertThat(actualReportDetails).isNotNull();
        assertThat(actualReportDetails.getStartDate()).isEqualTo(startDate);
        assertThat(actualReportDetails.getEndDate()).isEqualTo(endDate);

        compareTransactionMap(actualReportDetails.getMappedTransactions(), expectedReportDetails.getMappedTransactions());
        compareTransactionMap(actualReportDetails.getStCGMappedTransactions(),
                expectedReportDetails.getStCGMappedTransactions());
        compareTransactionMap(actualReportDetails.getLtCGMappedTransactions(),
                expectedReportDetails.getLtCGMappedTransactions());
        assertThat(actualReportDetails.getSpeculativeMappedTransactions()).isEmpty();
    }

    private void compareTransactionMap(Map<TransactionRecord, List<TransactionRecord>> actual,
                                       Map<TransactionRecord, List<TransactionRecord>> expected) {
        assertThat(actual).hasSize(expected.size());
        // comparison by id
        assertThat(actual).containsExactlyEntriesOf(expected);

        Iterator<Map.Entry<TransactionRecord, List<TransactionRecord>>> actualEntryIter = actual.entrySet().iterator();
        Iterator<Map.Entry<TransactionRecord, List<TransactionRecord>>> expectedEntryIter = expected.entrySet().iterator();
        while(actualEntryIter.hasNext() && expectedEntryIter.hasNext()) {
            Map.Entry<TransactionRecord, List<TransactionRecord>> actualEntry = actualEntryIter.next();
            Map.Entry<TransactionRecord, List<TransactionRecord>> expectedEntry = expectedEntryIter.next();

            TransactionRecord actualSellTransaction = actualEntry.getKey();
            TransactionRecord expectedSellTransaction = expectedEntry.getKey();
            compareTransactionRecord(actualSellTransaction, expectedSellTransaction);


            compareTransactionRecordList(actualEntry.getValue(), expectedEntry.getValue());
        }
    }

    private void compareTransactionRecordList(List<TransactionRecord> actualBuyTransactions,
                                              List<TransactionRecord> expectedBuyTransactions) {
        assertThat(actualBuyTransactions).hasSize(expectedBuyTransactions.size());
        for (int i = 0; i < actualBuyTransactions.size(); i++) {
            compareTransactionRecord(actualBuyTransactions.get(i), expectedBuyTransactions.get(i));
        }
    }

    private void compareTransactionRecord(TransactionRecord actual, TransactionRecord expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    private Map<TransactionRecord, List<TransactionRecord>> buildShortTermTransactions() {
        TransactionRecord sellTransactionRecord1 = TransactionRecord.builder()
                .id(100)
                .quantity(10)
                .transactionDate(LocalDate.of(2020, 2, 12))
                .scripName("ABC. 123 Scrip1")
                .unitPrice(12.77)
                .build();

        List<TransactionRecord> buyTransactions1 = ImmutableList.of(
                TransactionRecord.builder()
                        .id(1)
                        .quantity(10)
                        .transactionDate(LocalDate.of(2020, 1, 31))
                        .scripName("ABC. 123 Scrip1")
                        .unitPrice(10.01)
                        .build());

        TransactionRecord sellTransactionRecord2 = TransactionRecord.builder()
                .id(110)
                .quantity(50)
                .transactionDate(LocalDate.of(2020, 3, 1))
                .scripName("XYZ Test Scrip2")
                .unitPrice(2)
                .build();

        List<TransactionRecord> buyTransactions2 = ImmutableList.of(
                TransactionRecord.builder()
                        .id(10)
                        .quantity(15)
                        .transactionDate(LocalDate.of(2019, 12, 31))
                        .scripName("XYZ Test Scrip2")
                        .unitPrice(1.25)
                        .build(),
                TransactionRecord.builder()
                        .id(11)
                        .quantity(15)
                        .transactionDate(LocalDate.of(2020, 1, 7))
                        .scripName("XYZ Test Scrip2")
                        .unitPrice(1.27)
                        .build(),
                TransactionRecord.builder()
                        .id(12)
                        .quantity(20)
                        .transactionDate(LocalDate.of(2020, 1, 17))
                        .scripName("XYZ Test Scrip2")
                        .unitPrice(1.37)
                        .build());

        Map<TransactionRecord, List<TransactionRecord>> mappedTransactions = new LinkedHashMap<>();
        mappedTransactions.put(sellTransactionRecord1, buyTransactions1);
        mappedTransactions.put(sellTransactionRecord2, buyTransactions2);

        return mappedTransactions;
    }

    private Map<TransactionRecord, List<TransactionRecord>> buildLongTermTransactions() {
        TransactionRecord sellTransactionRecord1 = TransactionRecord.builder()
                .id(1000)
                .quantity(100)
                .transactionDate(LocalDate.of(2020, 1, 1))
                .scripName("ABC LT Scrip LTCG-1")
                .unitPrice(752.25)
                .build();

        List<TransactionRecord> buyTransactions1 = ImmutableList.of(
                TransactionRecord.builder()
                        .id(20)
                        .quantity(10)
                        .transactionDate(LocalDate.of(2018, 5, 1))
                        .scripName("ABC LT Scrip LTCG-1")
                        .unitPrice(1001)
                        .build(),
                TransactionRecord.builder()
                        .id(21)
                        .quantity(90)
                        .transactionDate(LocalDate.of(2018, 10, 10))
                        .scripName("ABC LT Scrip LTCG-1")
                        .unitPrice(801.78)
                        .build()
                );

        TransactionRecord sellTransactionRecord2 = TransactionRecord.builder()
                .id(10010)
                .quantity(30)
                .transactionDate(LocalDate.of(2020, 2, 28))
                .scripName("XYZ Test Scrip2 LTCG-1")
                .unitPrice(55.7)
                .build();

        List<TransactionRecord> buyTransactions2 = ImmutableList.of(
                TransactionRecord.builder()
                        .id(50)
                        .quantity(30)
                        .transactionDate(LocalDate.of(2019, 2, 20))
                        .scripName("XYZ Test Scrip2 LTCG-1")
                        .unitPrice(38.25)
                        .build());

        Map<TransactionRecord, List<TransactionRecord>> mappedTransactions = new LinkedHashMap<>();
        mappedTransactions.put(sellTransactionRecord1, buyTransactions1);
        mappedTransactions.put(sellTransactionRecord2, buyTransactions2);

        return mappedTransactions;
    }

    private ReportDetails buildShortTermAndLongTermTransactions() {
        TransactionRecord sellTransactionRecord1 = TransactionRecord.builder()
                .id(2000)
                .quantity(100)
                .transactionDate(LocalDate.of(2020, 1, 1))
                .scripName("ABC LT Scrip 1AB")
                .unitPrice(952)
                .build();

        List<TransactionRecord> buyTransactions1 = ImmutableList.of(
                TransactionRecord.builder()
                        .id(32)
                        .quantity(30)
                        .transactionDate(LocalDate.of(2018, 5, 11))
                        .scripName("ABC LT Scrip 1AB")
                        .unitPrice(1001)
                        .build(),
                TransactionRecord.builder()
                        .id(33)
                        .quantity(20)
                        .transactionDate(LocalDate.of(2018, 10, 1))
                        .scripName("ABC LT Scrip 1AB")
                        .unitPrice(859)
                        .build(),
                TransactionRecord.builder()
                        .id(34)
                        .quantity(50)
                        .transactionDate(LocalDate.of(2019, 11, 23))
                        .scripName("ABC LT Scrip 1AB")
                        .unitPrice(598.81)
                        .build());

        TransactionRecord sellTransactionRecord2 = TransactionRecord.builder()
                .id(200012)
                .quantity(10)
                .transactionDate(LocalDate.of(2020, 1, 20))
                .scripName("XYZ Test Scrip2")
                .unitPrice(55.7)
                .build();

        List<TransactionRecord> buyTransactions2 = ImmutableList.of(
                TransactionRecord.builder()
                        .id(130)
                        .quantity(10)
                        .transactionDate(LocalDate.of(2019, 1, 25))
                        .scripName("XYZ Test Scrip2")
                        .unitPrice(67)
                        .build());

        Map<TransactionRecord, List<TransactionRecord>> mappedTransactions = new LinkedHashMap<>();
        mappedTransactions.put(sellTransactionRecord1, buyTransactions1);
        mappedTransactions.put(sellTransactionRecord2, buyTransactions2);

        Map<TransactionRecord, List<TransactionRecord>> stCGMappedTransactions = new LinkedHashMap<>();
        stCGMappedTransactions.put(
                sellTransactionRecord1.toBuilder().quantity(50).build(),
                ImmutableList.of(buyTransactions1.get(2)));
        stCGMappedTransactions.put(sellTransactionRecord2, buyTransactions2);

        Map<TransactionRecord, List<TransactionRecord>> ltCGMappedTransactions = new LinkedHashMap<>();
        ltCGMappedTransactions.put(
                sellTransactionRecord1.toBuilder().quantity(50).build(),
                ImmutableList.of(buyTransactions1.get(0), buyTransactions1.get(1)));


        return ReportDetails.builder()
                .mappedTransactions(mappedTransactions)
                .stCGMappedTransactions(stCGMappedTransactions)
                .ltCGMappedTransactions(ltCGMappedTransactions)
                .build();
    }
}
