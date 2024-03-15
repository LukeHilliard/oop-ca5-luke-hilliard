package Objects;

import DTOs.Employee;

import java.util.Comparator;

/**
 * Main author: Haroldas Tamosauskas
 * Other contributors:
 *
 */
public class SalaryComparator implements Comparator<Employee> {
    public int compare(Employee emp1, Employee emp2)
    {
        int ans = (int)((emp1.getSalary() - emp2.getSalary())*100);
        return ans;
    }
    }
