package com.github.crazymax.crossfitreader.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorCode;

/**
 * Custom log appender
 * @author CrazyMax
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public class LogAppender
        extends FileAppender {

    private final static String DOT = ".";

    public LogAppender() {

    }

    public LogAppender(final Layout layout, final String filename,
            final boolean append, final boolean bufferedIO, final int bufferSize)
            throws IOException {
        super(layout, filename, append, bufferedIO, bufferSize);
    }

    public LogAppender(final Layout layout, final String filename,
            final boolean append)
            throws IOException {
        super(layout, filename, append);
    }

    public LogAppender(final Layout layout, final String filename)
            throws IOException {
        super(layout, filename);
    }

    @Override
    public void activateOptions() {
        if (fileName == null) {
            return;
        }

        try {
            final StringBuilder newFileName = new StringBuilder();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            final String currentTime = dateFormat.format(new Date());
            final File logFile = new File(fileName);
            final String logFileName = logFile.getName();

            final int dotIndex = logFileName.indexOf(DOT);
            if (dotIndex != -1) {
                newFileName.append(logFileName.substring(0, dotIndex)).append(DOT);
                newFileName.append(currentTime).append(DOT);
                newFileName.append(logFileName.substring(dotIndex + 1));
            } else {
                newFileName.append(logFileName).append(DOT);
                newFileName.append(currentTime);
            }

            fileName = logFile.getParent() + File.separator + newFileName.toString();
            setFile(fileName, fileAppend, bufferedIO, bufferSize);
        } catch (Exception e) {
            errorHandler.error("Error while activating log options", e, ErrorCode.FILE_OPEN_FAILURE);
        }
    }
}
