package DTOs;

import DAOs.EmployeeDaoInterface;
import DAOs.MySqlEmployeeDao;
import Exceptions.DaoException;
import Exceptions.EmployeeNotFoundException;
import Utilities.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDate;

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

            // using GsonBuilder to apply the LocalDateAdapter created in the Utilities package
            Gson gsonParser = new GsonBuilder()
                    .setPrettyPrinting() // format json to have 'pretty' view
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();

            employeeJson = gsonParser.toJson(employee);


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
