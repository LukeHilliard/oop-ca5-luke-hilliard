package Exceptions;

/**
 * Author: Luke Hilliard
 * An exception to catch if an entered id/key is not within the database
 */
public class InvalidIdException extends Exception{
    public InvalidIdException(String aMessage){super(aMessage);}
}
