package com.humanharvest.organz.server.exceptions;

public class IfMatchRequiredException extends Exception {

    public IfMatchRequiredException() {}

    public IfMatchRequiredException(String text) {
        super(text);
    }

}
