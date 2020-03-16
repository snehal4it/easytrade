package com.trade.utils.easytrade.dao.impl;

import com.trade.utils.easytrade.dao.StatementUploadHistoryDao;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository
public class StatementUploadHistoryDaoImpl implements StatementUploadHistoryDao {

    private static final String SAVE_UPLOAD_HISTORY_QUERY
            = "insert into statement_upload_history (filename, transaction_count)"
                + " values (:filename, :transactionCount)";

    private static final String[] UPLOAD_HISTORY_ID_COLUMN = {"id"};

    @Inject
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public long save(String filename, int transactionCount) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("filename", filename)
                .addValue("transactionCount", transactionCount);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(SAVE_UPLOAD_HISTORY_QUERY, sqlParameterSource,
                keyHolder, UPLOAD_HISTORY_ID_COLUMN);

        return keyHolder.getKey().longValue();
    }
}
