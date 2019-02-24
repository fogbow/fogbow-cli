package cloud.fogbow.cli.constants;

public class Messages {
    public static class Exception {
        public static final String CLOUD_NAME_NOT_ALLOWED = "No need to specify a cloud name.";
        public static final String EMPTY_COMMAND = "Command is empty.";
        public static final String INCOMPLETE_COMMAND = "Missing manadatory fields in the command.";
        public static final String INCONSISTENT_PARAMS = "Invalid options; use either %s or %s.";
        public static final String INVALID_PARAMETER_S_S = "Parameter <%s> should be one of <%s>.";
        public static final String INVALID_KEY_VALUE_FORMAT = "Key-values should be in the form: " +
                "key1=value1,key2=value2,key3=value3";
        public static final String NO_CREDENTIALS_PARAMS = "No auth parameters passed.";
        public static final String NO_FOGBOW_URL_PARAMS = "No fogbow url passed as parameter.";
        public static final String NO_FEDERATION_TOKEN = "No federation token passed as parameter.";
        public static final String NO_ID_INFORMED = "No resource identification passed as parameter.";
        public static final String NO_MEMBER_ID_OR_CLOUD_NAME = "No member-id or cloud-ame passed as parameter.";
        public static final String NO_MEMBERSHIP_SERVICE_URL = "No Membership Service API URL passed as parameter.";
        public static final String NO_RULE_ID_INFORMED = "No rule identification informed.";
        public static final String UNABLE_TO_READ_PUBLIC_KEY_FILE = "Unable to read public key file.";
        public static final String UNABLE_TO_VALIDATE_PARAMETER_S_S = "Unable to validate parameter <%s> against <%s>.";
        public static final String UNEXPECTED = "Unexpected exception.";
    }
}
