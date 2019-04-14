package cc.base;

public class UServletException extends UBaseException {

    public UServletException(String msg, Exception cause) {
        super(msg, cause);
    }

    public UServletException(String msg) {
        super(msg, null);
    }

    public UServletException(Exception cause) {
        super(cause);
    }
}
