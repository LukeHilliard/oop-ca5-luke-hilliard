package DTOs;

import DAOs.EmployeeDaoInterface;
import DAOs.MySqlEmployeeDao;
import Exceptions.DaoException;
import Exceptions.EmployeeNotFoundException;

public class JsonConverter {
    public String employeeToJsonByKey(int key) {
        String employeeJson = "";

        try {
            EmployeeDaoInterface IEmployeeDao = new MySqlEmployeeDao();
                Employee employee = IEmployeeDao.getEmployeeById(key);

                // if there is no employee in the DB with this ID
                if(employee == null) {
                    employeeJson = null;
                    throw new EmployeeNotFoundException("No employee with ID: " + key + "\n\n");
                }



            //TODO -> conversion
            System.out.println("key: " + key);

        }
        catch(EmployeeNotFoundException e) {
            System.out.println(e.getMessage());

        }
        catch(DaoException e) {
            System.out.println("---* Error connecting to the database *---");
        }
        return employeeJson;
    }
}
