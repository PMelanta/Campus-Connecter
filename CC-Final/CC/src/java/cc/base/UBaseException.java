package cc.base;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

public class UBaseException extends Exception {

    static Logger logger = Logger.getLogger(UBaseException.class);

    public UBaseException(String msg, Throwable cause) {
        super(msg, cause);
 
	logger.error(msg);

	if(cause != null && !"ubq.base".equals(cause.getClass().getPackage().getName()))
	    printStackTrace();
    }

    public UBaseException(String msg) {
        super(msg, null);

	logger.error(msg);

	printStackTrace();
    }

    public UBaseException(Throwable cause) {
        super(cause.getMessage(), cause);

	logger.error(cause.getMessage());

	if(cause != null && !"ubq.base".equals(cause.getClass().getPackage().getName()))
	    printStackTrace();
    }

}
