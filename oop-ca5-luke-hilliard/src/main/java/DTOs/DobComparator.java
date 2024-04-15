package DTOs;

import DTOs.Employee;

import java.util.Comparator;

/**
 * Main author: Haroldas Tamosauskas
 * Other contributors: Luke Hilliard
 *
 */
public class DobComparator implements Comparator<Employee> {
    private final boolean ascending;

    public DobComparator(boolean ascending){this.ascending = ascending;}
    @Override
    public int compare(Employee emp1, Employee emp2) {
        if(ascending)
            return emp1.getDob().compareTo(emp2.getDob());
        else
            return emp2.getDob().compareTo(emp1.getDob());
    }
}
