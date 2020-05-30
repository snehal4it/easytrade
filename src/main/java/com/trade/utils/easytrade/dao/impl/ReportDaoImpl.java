package com.trade.utils.easytrade.dao.impl;

import com.trade.utils.easytrade.dao.ReportDao;
import com.trade.utils.easytrade.model.report.TransactionRecord;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ReportDaoImpl implements ReportDao {

    public static final String MAPPED_TRANSACTIONS_QUERY = "select s.id, s.transaction_date, sm.mapped_name scrip_name,"
            + " s.quantity, s.transaction_price, b.id b_id, b.transaction_date::date b_transaction_date,"
            + " b.quantity b_quantity, b.transaction_price b_transaction_price"
            + " from sell_transaction s"
            + " join buy_transaction b on s.id = b.sell_transaction_id and b.status = 120"
            + " join scrip_mapping sm on sm.original_name = s.scrip_name"
            + " where s.transaction_date between :startDate and :endDate"
            + " order by s.transaction_date, sm.mapped_name, s.id, b.transaction_date, b.id";

    @Inject
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Map<TransactionRecord, List<TransactionRecord>> getMappedTransactions(LocalDate startDate,
                                                                                 LocalDate endDate) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("startDate", startDate)
                .addValue("endDate", endDate);

        return namedParameterJdbcTemplate.query(MAPPED_TRANSACTIONS_QUERY, sqlParameterSource, rs -> {
            Map<TransactionRecord, List<TransactionRecord>> mappedTransactions = new LinkedHashMap<>();
            while(rs.next()) {
                TransactionRecord sellTransaction = TransactionRecord.builder()
                        .id(rs.getLong("id"))
                        .transactionDate(rs.getDate("transaction_date").toLocalDate())
                        .scripName(rs.getString("scrip_name"))
                        .quantity(rs.getInt("quantity"))
                        .unitPrice(rs.getDouble("transaction_price"))
                        .build();

                TransactionRecord buyTransaction = TransactionRecord.builder()
                        .id(rs.getLong("b_id"))
                        .transactionDate(rs.getDate("b_transaction_date").toLocalDate())
                        .quantity(rs.getInt("b_quantity"))
                        .unitPrice(rs.getDouble("b_transaction_price"))
                        .build();

                mappedTransactions
                        .computeIfAbsent(sellTransaction, key -> new ArrayList<>())
                        .add(buyTransaction);
            }
            return mappedTransactions;
        });
    }
}
