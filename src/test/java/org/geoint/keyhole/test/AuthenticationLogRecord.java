package org.geoint.keyhole.test;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class AuthenticationLogRecord extends LogRecord {

    private static final Logger logger = Logger.getLogger(AuthenticationLogRecord.class.getName());

    public AuthenticationLogRecord(Level level, String msg) {
        super(level, msg);
        logger.log(Level.INFO, "AuthenticationLogRecord constructor called");
        
    }

}
