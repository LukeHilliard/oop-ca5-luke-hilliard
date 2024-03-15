package Objects;
import DTOs.Employee;

import java.util.Comparator;

/**
 * Main author: Haroldas Tamosauskas
 * Other contributors:
 *
 */
public class FirstnameComparator implements Comparator<Employee> {
    public int compare(Employee emp1, Employee emp2)
    {
        return emp1.getFirstName().compareTo(emp2.getFirstName());
    }
}
