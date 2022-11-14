package jzeigler7.cmpplugin;
/**
 *
 * This exception is thrown when attempting to execute a score check while another
 * score check is already in progress.
 *
 * @author Jacob Zeigler
 * @version 1.0
 */

public class CheckInProgressException extends Exception {
    public CheckInProgressException(String message) {
        super(message);
    }
}