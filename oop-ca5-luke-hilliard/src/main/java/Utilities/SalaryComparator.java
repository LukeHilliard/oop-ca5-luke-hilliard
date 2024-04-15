package Utilities;

import DTOs.Employee;

import java.util.Comparator;

/**
 * Main author: Haroldas Tamosauskas
 * Other contributors: Luke Hilliard
 *
 */
public class SalaryComparator implements Comparator<Employee> {
    private final boolean ascending;

    public SalaryComparator(boolean ascending){this.ascending = ascending; }
    public int compare(Employee emp1, Employee emp2)
    {
        if(ascending)
            return Double.compare(emp1.getSalary(), emp2.getSalary());
        else
            return Double.compare(emp2.getSalary(), emp1.getSalary());
    }
}
