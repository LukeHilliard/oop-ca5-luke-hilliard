package DAOs;

import DTOs.Employee;
import Exceptions.DaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;


/**
 * Main author: Haroldas Tamosauskas
 * Other contributors:
 *
 */
public class MySqlEmployeeDao extends MySqlDao implements EmployeeDaoInterface {

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
                Date dob = resultSet.getDate("dob");
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
                Date dob = resultSet.getDate("dob");
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
