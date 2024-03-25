package DTOs;

import java.util.Comparator;

/**
 * Main author: Haroldas Tamosauskas
 * Other contributors: Luke Hilliard
 *
 */
public class FirstnameComparator implements Comparator<Employee> {
    private final boolean ascending;

    public FirstnameComparator(boolean ascending) {
        this.ascending = ascending;
    }


    public int compare(Employee emp1, Employee emp2)
    {
        if(ascending) {
            return emp1.getFirstName().compareTo(emp2.getFirstName());
        } else {
            return emp2.getFirstName().compareTo(emp1.getFirstName());
        }

    }
}
