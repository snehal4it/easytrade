package com.trade.utils.easytrade.service.impl;

import com.trade.utils.easytrade.dao.StatementUploadHistoryDao;
import com.trade.utils.easytrade.dao.TransactionDao;
import com.trade.utils.easytrade.document.StatementDocument;
import com.trade.utils.easytrade.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Inject
    private StatementUploadHistoryDao statementUploadHistoryDao;

    @Inject
    private TransactionDao transactionDao;

    @Transactional
    @Override
    public void save(String filename, StatementDocument statementDocument) {

        long uploadHistoryId = statementUploadHistoryDao.save(filename, statementDocument.getTransactions().size());

        transactionDao.save(uploadHistoryId, statementDocument.getTransactions());
    }
}
