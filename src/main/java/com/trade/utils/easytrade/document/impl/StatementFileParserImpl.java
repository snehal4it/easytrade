package com.trade.utils.easytrade.document.impl;

import com.trade.utils.easytrade.document.StatementFileParser;
import com.trade.utils.easytrade.model.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.inject.Named;
import java.util.List;

@Named
public class StatementFileParserImpl implements StatementFileParser {

    @Override
    public List<Transaction> parse(String fileContent) {
        Document document = Jsoup.parse(fileContent);

        return null;
    }

    private Element getDataTable(Document document) {
        Element tableContainer = document.body().child(0);
        //tableContainer.
        return null;
    }
}
