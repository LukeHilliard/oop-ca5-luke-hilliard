package DAOs;

import DTOs.Employee;
import Exceptions.DaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlEmployeeDao extends MySqlDao implements EmployeeDaoInterface {

    /**
     * Main author: Haroldas Tamosauskas
     * Other contributors: ...
     *
     */

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

            String query = "SELECT * FROM EMPLOYEE";
            preparedStatement = connection.prepareStatement(query);


            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                int employeeId = resultSet.getInt("EMPLOYEE_ID");
                int salary = resultSet.getInt("SALARY");
                String role = resultSet.getString("ROLE");
                String lastname = resultSet.getString("LAST_NAME");
                String firstname = resultSet.getString("FIRST_NAME");
                Employee u = new Employee(employeeId, firstname, lastname, salary, role);
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

            String query = "SELECT * FROM USER WHERE ID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                int userId = resultSet.getInt("USER_ID");
                int salary = resultSet.getInt("SALARY");
                String role = resultSet.getString("ROLE");
                String lastname = resultSet.getString("LAST_NAME");
                String firstname = resultSet.getString("FIRST_NAME");

                employee = new Employee(userId, firstname, lastname, salary, role);
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
