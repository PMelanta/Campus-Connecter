package cc.base;

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.PrintWriter;
import java.io.IOException;

public class UMailDaemon extends Thread {

    static Logger logger = Logger.getLogger(UMailDaemon.class);

    public void run() {

	logger.info("Mail daemon running....");

	while(true) {

	    try {
                // Sleep for 5 minutes by default
		Thread.sleep(UConfig.getParameterValue(0, "MAILD_WAKEUP_INTERVAL", 3000));                  

                UMailer.flushMailQueue();

	    } catch (Exception e) {
		// Let's just keep going!
		logger.error("Mail daemon error: " + e.getMessage());
	    }
	}
    }
}
