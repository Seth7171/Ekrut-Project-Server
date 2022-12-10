import java.io.Serializable;

public enum MessageFromServer implements Serializable {
    IMPORT_ORDER_TABLE_COMPLETE, IMPORT_ORDER_TABLE_UNSUCCESSFUL, IMPORT_USER_TABLE_SUCCESSFUL, IMPORT_USER_UNSUCCESSFUL, UPDATE_SUCCESSFUL, UPDATE_UNSUCCESSFUL, ADD_USER_SUCCESSFUL, ADD_USER_UNSUCCESSFUL, DELETE_USER_SUCCESSFUL, DELETE_USER_UNSUCCESSFUL, UNKNOWN;
}