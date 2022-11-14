package jzeigler7.cmpplugin;
/**
 *
 * This exception is thrown when attempting to execute a score check for a
 * player who has not set their reference point.
 *
 * @author Jacob Zeigler
 * @version 1.0
 */

public class NoReferencePointException extends Exception {
    public NoReferencePointException(String message) {
        super(message);
    }
}
