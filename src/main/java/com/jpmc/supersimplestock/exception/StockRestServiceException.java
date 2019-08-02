package com.jpmc.supersimplestock.exception;

public class StockRestServiceException extends Exception {

    private boolean suppressStacktrace = false;

    public StockRestServiceException(String message, boolean suppressStacktrace) {
        super(message, null, suppressStacktrace, !suppressStacktrace);
        this.suppressStacktrace = suppressStacktrace;
    }

    @Override
    public String toString() {
        if (suppressStacktrace) {
            return getLocalizedMessage();
        } else {
            return super.toString();
        }
    }
}

