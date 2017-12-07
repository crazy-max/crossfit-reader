package com.github.crazymax.crossfitreader.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

/**
 * OutputCmd model
 * @author CrazyMax
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public class OutputCmd {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private int exitCode = 0;
    private List<String> output = new ArrayList<String>();
    private List<String> error = new ArrayList<String>();

    private String print(final List<String> data) {
        if (data == null || data.size() == 0) {
            return "";
        }

        StringBuilder db = new StringBuilder();
        for (String row : data) {
            row = row.trim();
            if (Strings.isNullOrEmpty(row)) {
                continue;
            }
            if (!Strings.isNullOrEmpty(db.toString())) {
                db.append(LINE_SEPARATOR);
            }
            db.append(row);
        }

        return db.toString();
    }

    public boolean isEmpty() {
        return output.size() == 0;
    }

    public boolean isSingle() {
        return output.size() == 1;
    }

    public void addOutput(final String row) {
        output.add(row.trim());
    }

    public String printOutput() {
        return print(output);
    }

    public boolean hasError() {
        return error.size() > 0;
    }

    public void addError(final String row) {
        error.add(row.trim());
    }

    public String printError() {
        return print(error);
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public List<String> getOutput() {
        return output;
    }

    public void setOutput(List<String> output) {
        this.output = output;
    }

    public List<String> getError() {
        return error;
    }

    public void setError(List<String> error) {
        this.error = error;
    }
}
