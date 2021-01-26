package com.trade.utils.easytrade.document;

import com.trade.utils.easytrade.model.StatementFile;

import java.io.InputStream;

/**
 * Parse statement file
 */
public interface StatementFileParser {

    /**
     * Parses statement document from input stream
     * @param inputStream content of file
     * @return parsed statement file result holder
     */
    StatementFile parse(InputStream inputStream);
}
