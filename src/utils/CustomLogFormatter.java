package utils;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;


public class CustomLogFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        return record.getSourceClassName() + ": " + record.getMessage() + "\n";
    }
}
