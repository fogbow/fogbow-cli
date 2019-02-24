package cloud.fogbow.cli.exceptions;

import cloud.fogbow.cli.constants.Messages;

public class FogbowCLIException extends Exception {
    private static final long serialVersionUID = 1L;

    public FogbowCLIException() {
        super(Messages.Exception.UNEXPECTED);
    }

    public FogbowCLIException(String message) {
        super(message);
    }

    public FogbowCLIException(String message, Throwable cause) {
        super(message, cause);
    }
}
