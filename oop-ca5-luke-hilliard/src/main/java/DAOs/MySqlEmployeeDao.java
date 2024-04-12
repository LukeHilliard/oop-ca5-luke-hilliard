package DAOs;

import DTOs.DobComparator;
import DTOs.Employee;
import DTOs.FirstnameComparator;
import DTOs.SalaryComparator;
import Exceptions.DaoException;
import Exceptions.EmployeeNotFoundException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Main author: Haroldas Tamosauskas
 * Other contributors: Luke Hilliard
 *
 */
public class MySqlEmployeeDao extends MySqlDao implements EmployeeDaoInterface {
    /**
     * Author: Luke Hilliard
     * Takes an Employee Object as a parameter and posts the data to the database
     * @param newEmployeeData this will be added to the database
     * @throws DaoException catch the exception
     */
    @Override
    public void addEmployee(Employee newEmployeeData) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.getConnection();
            String query = "INSERT INTO Employees (id, first_name, last_name, gender, dob, salary, role, username, password) " +
                    "VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);

            // build the new employee table entry
            preparedStatement.setString(1, newEmployeeData.getFirstName());
            preparedStatement.setString(2, newEmployeeData.getLastName());
            preparedStatement.setString(3, newEmployeeData.getGender());
            preparedStatement.setDate(4, Date.valueOf(newEmployeeData.getDob()));
            preparedStatement.setDouble(5, newEmployeeData.getSalary());
            preparedStatement.setString(6, newEmployeeData.getRole());
            preparedStatement.setString(7, newEmployeeData.getUsername());
            preparedStatement.setString(8, newEmployeeData.getPassword());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException("addEmployee()\t" + e.getMessage());

        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();

                if (preparedStatement != null)
                    preparedStatement.close();

                if (connection != null)
                    freeConnection(connection);

            } catch (SQLException e) {
                System.out.print("addEmployee()\t" + e.getMessage());
            }
        }
    }

    /**
     * Author: Katie Lynch
     * Takes in user input for employee ID and deletes corresponding employee
     * @throws DaoException catch the exception
     */
    @Override
    public void deleteEmployee(int id) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Employee employee;
        try{

            connection = this.getConnection();
            //creates query to delete a row of employee data using the ID parameter passed in
            String query = "DELETE FROM Employees WHERE id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);

            //goes to ID entered and executes the query to delete the row and updates the Employee table to show the record was removed
            int deletedRows = preparedStatement.executeUpdate();

            //makes sure the ID entered is above 0 as there won't be a record 0 and checks if the ID entered corresponds with an employee record that exists in the database
            if(deletedRows > 0)
                System.out.println("Successfully deleted employee");
            else
                throw new EmployeeNotFoundException("No employee found with ID: " + id + "\n");


        } catch (SQLException ex){
            throw new DaoException("deleteEmployee(): " + ex.getMessage());
        }  catch (EmployeeNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();

                if (preparedStatement != null)
                    preparedStatement.close();

                if (connection != null)
                    freeConnection(connection);

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    @Override
    public List<Employee> getAllEmployees() throws DaoException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Employee> EmployeesList = new ArrayList<>();

        try {
            connection = this.getConnection();

            String query = "SELECT * FROM Employees";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int employeeId = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String gender = resultSet.getString("gender");
                LocalDate dob = resultSet.getDate("dob").toLocalDate();
                double salary = resultSet.getInt("salary");
                String role = resultSet.getString("role");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");

                Employee employee = new Employee(employeeId, firstName, lastName, gender, dob, salary, role, username, password);
                EmployeesList.add(employee);
            }
        } catch (SQLException e) {
            throw new DaoException("findAllEmployees() " + e.getMessage());
        }

        finally {
            try {
                if (resultSet != null)
                    resultSet.close();

                if (preparedStatement != null)
                    preparedStatement.close();

                if (connection != null)
                    freeConnection(connection);

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return EmployeesList;
    }


    @Override
    public Employee getEmployeeById(int id) throws DaoException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Employee employee = null;
        try {
            connection = this.getConnection();

            String query = "SELECT * FROM Employees WHERE id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int employeeId = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String gender = resultSet.getString("gender");
                LocalDate dob = resultSet.getDate("dob").toLocalDate();
                double salary = resultSet.getInt("salary");
                String role = resultSet.getString("role");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");

                employee = new Employee(employeeId, firstName, lastName, gender, dob, salary, role, username, password);
            } else {
                throw new EmployeeNotFoundException("No employee found with ID: " + id + "\n");
            }

        } catch(EmployeeNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("findUserByUsernamePassword() " + e.getMessage());

        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();

                if (preparedStatement != null)
                    preparedStatement.close();

                if (connection != null)
                    freeConnection(connection);

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return employee;     // reference to User object, or null value
    }

    /**
     * Author: Katie Lynch
     * Takes an Employee Object as a parameter and posts the new data to the database
     *
     * @param updatedEmployee this will update the new data add it to the database
     * @throws DaoException catch the exception
     */
    @Override
    public void updateEmployee(int id, Employee updatedEmployee) throws DaoException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Employee employee = null;

        try{
            connection = this.getConnection();
            //creates query to get one row of data based off of the ID
            String query = "UPDATE Employees SET  first_name = ?, last_name = ?, gender = ?, dob = ?, salary = ?, role = ?, username = ?,  password = ? WHERE id =?";
            preparedStatement = connection.prepareStatement(query);

            //updates employee information with new data
            preparedStatement.setString(1, updatedEmployee.getFirstName());
            preparedStatement.setString(2, updatedEmployee.getLastName());
            preparedStatement.setString(3, updatedEmployee.getGender());
            preparedStatement.setDate(4, Date.valueOf(updatedEmployee.getDob()));
            preparedStatement.setDouble(5, updatedEmployee.getSalary());
            preparedStatement.setString(6, updatedEmployee.getRole());
            preparedStatement.setString(7, updatedEmployee.getUsername());
            preparedStatement.setString(8, updatedEmployee.getPassword());
            preparedStatement.setInt(9, id);


            //updates one employee that the ID entered matches
            int updated = preparedStatement.executeUpdate();
            if(updated > 0){
                System.out.println("Employee " + id + " was updated");
            }else{
                throw new EmployeeNotFoundException("No employee found with ID: " + id + "\n");
            }

        } catch(SQLException e) {
            throw new DaoException("Error At updateEmployee(): " + e.getMessage());
        } catch(EmployeeNotFoundException e) {
            System.out.println(e.getMessage() );
        }

        finally {
            try {
                if (resultSet != null)
                    resultSet.close();

                if (preparedStatement != null)
                    preparedStatement.close();

                if (connection != null)
                    freeConnection(connection);

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    /**
     * Author: Luke Hilliard
     * Gets an ArrayList of all employees, based on the value of filter, a comparator is used to apply the filter,
     *  value of order passed ot each comparator to set ASC or DESC
     *
     * @param filter field to apply filter to
     * @param order ascending or descending
     * @return filtered list of employees
     */
    @Override
    public List<Employee> getEmployeesMatchingFilter(String filter, boolean order) {
        List<Employee> employeesList = new ArrayList<>();
        try{
           employeesList = getAllEmployees();
        }catch(DaoException e) {
            System.out.println("----* Error retrieving all employees *----");
        }

        switch (filter) {
            case "fName":
                Collections.sort(employeesList, new FirstnameComparator(order));
                break;

            case "dob":
                Collections.sort(employeesList, new DobComparator(order));
                break;

            case "salary":
                Collections.sort(employeesList, new SalaryComparator(order));
                break;

            default:
                System.out.println("*---* Invalid filter selected *---*");
                break;
        }

        return employeesList;
    }

}
