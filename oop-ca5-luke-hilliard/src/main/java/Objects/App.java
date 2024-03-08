package Objects;

import DAOs.MySqlEmployeeDao;
import DAOs.EmployeeDaoInterface;
import DTOs.Employee;
import Exceptions.DaoException;
import java.util.List;
public class App {
    public static void main(String[] args) {
        /**
         * Main author: Haroldas Tamosauskas
         * Other contributors: ...
         *
         */
        EmployeeDaoInterface IEmployeeDao = new MySqlEmployeeDao();
        try
        {
            System.out.println("\nFinding all the Employees");
            List<Employee> employees = IEmployeeDao.getAllEmployees();

            if( employees.isEmpty() )
                System.out.println("There are no Employees");
            else {
                for (Employee employee : employees)
                    System.out.println("Employee: " + employee.toString());
            }
            // Testing to find the ID
            System.out.println("\nFinding all the Employees with ID");
            int id = 1;
            Employee employee = IEmployeeDao.getEmployeeById(id);

            if( employee != null )
                System.out.println("Employee found: " + employee);
            else
                System.out.println("Employee with that ID was not found");


    }
        catch( DaoException e)
        {
            e.printStackTrace();
        }
    }
}
