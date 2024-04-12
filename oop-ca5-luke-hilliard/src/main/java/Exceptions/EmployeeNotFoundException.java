package Exceptions;

/**
 * Author: Luke Hilliard
 * An exception to catch if an entered id/key is not within the database
 */
public class EmployeeNotFoundException extends Exception{
    public EmployeeNotFoundException(String aMessage){super(aMessage);}
}
