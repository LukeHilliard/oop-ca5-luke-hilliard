package DAOs;

import DTOs.Employee;
import Exceptions.DaoException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
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
            throw new DaoException("addEmployee() " + e.getMessage());
        } finally {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
                if (preparedStatement != null)
                {
                    preparedStatement.close();
                }
                if (connection != null)
                {
                    freeConnection(connection);
                }
            } catch (SQLException e)
            {
                throw new DaoException("addEmployee() " + e.getMessage());
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

        try
        {

            connection = this.getConnection();

            String query = "SELECT * FROM Employees";
            preparedStatement = connection.prepareStatement(query);


            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                int employeeId = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String gender = resultSet.getString("gender");
                LocalDate dob = resultSet.getDate("dob").toLocalDate();
                double salary = resultSet.getInt("salary");
                String role = resultSet.getString("role");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");

                Employee u = new Employee(employeeId, firstName, lastName, gender, dob, salary, role, username, password);
                EmployeesList.add(u);
            }
        } catch (SQLException e)
        {
            throw new DaoException("findAllEmployeeresultSet() " + e.getMessage());
        } finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
                if (preparedStatement != null)
                {
                    preparedStatement.close();
                }
                if (connection != null)
                {
                    freeConnection(connection);
                }
            } catch (SQLException e)
            {
                throw new DaoException("getAllEmployees() " + e.getMessage());
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
        try
        {
            connection = this.getConnection();

            String query = "SELECT * FROM Employees WHERE id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                int employeeId = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String gender = resultSet.getString("gender");
                LocalDate dob = resultSet.getDate("dob").toLocalDate();
                double salary = resultSet.getInt("salary");
                String role = resultSet.getString("role");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");

                new Employee(employeeId, firstName, lastName, gender, dob, salary, role, username, password);
            }
        } catch (SQLException e)
        {
            throw new DaoException("findUserByUsernamePassword() " + e.getMessage());
        } finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
                if (preparedStatement != null)
                {
                    preparedStatement.close();
                }
                if (connection != null)
                {
                    freeConnection(connection);
                }
            } catch (SQLException e)
            {
                throw new DaoException("findUserByUsernamePassword() " + e.getMessage());
            }
        }
        return employee;     // reference to User object, or null value
    }
}
