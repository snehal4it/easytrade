package com.trade.utils.easytrade.util;

import com.trade.utils.easytrade.exception.SystemException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public final class IOUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(IOUtil.class);

    public static String getFileContent(InputStream inputStream) {
        try {
            return IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException e) {
            LOGGER.error("Error in reading file", e);
            throw new SystemException("Error in reading file", e);
        }
    }

    private IOUtil() {
    }
}
