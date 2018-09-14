package org.fogbowcloud.cli.exceptions;

import org.fogbowcloud.cli.constants.Messages;

public class UnexpectedException extends Exception {
    private static final long serialVersionUID = 1L;

    public UnexpectedException() {
        super(Messages.Exception.UNEXPECTED);
    }

    public UnexpectedException(String message) {
        super(message);
    }

    public UnexpectedException(String message, Throwable cause) {
        super(message, cause);
    }
}
