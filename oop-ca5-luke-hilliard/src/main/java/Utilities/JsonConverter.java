package Utilities;

import DAOs.EmployeeDaoInterface;
import DAOs.MySqlEmployeeDao;
import DTOs.Employee;
import Exceptions.DaoException;
import Exceptions.EmployeeNotFoundException;
import Utilities.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDate;
import java.util.List;

public class JsonConverter {

    /**
     * Author: Luke Hilliard
     * Other contributors: Luke Hilliard
     * Gson Parser takes one employee and parses its information before it outputs as a Json String. Added .setPrettyPrinting to separate each Json String to the next line
     */
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

    /**
     * Author: Katie Lynch
     * Other contributors: Luke Hilliard
     *
     */
    public String jsonEmployeeList() throws DaoException{
        //String stores the employee information for each employee in the database
        String jsonEmployees = "";
        try {
            //connects to the database and stores all the employees there in a list
            EmployeeDaoInterface IEmployeeDao = new MySqlEmployeeDao();
            List<Employee> employee = IEmployeeDao.getAllEmployees();

            //creating the gsonParser that will display the Json Strings in a readable format
            Gson gsonParser = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();
            //takes each individual employee, applies the parser to the employees information and stores all the new formatted employees
            jsonEmployees = gsonParser.toJson(employee);

        } catch(DaoException e) {
            System.out.println("Encountered An Error Displaying All Employees As A Json String: " + e.getMessage());
        }
        return jsonEmployees;

    }
}
