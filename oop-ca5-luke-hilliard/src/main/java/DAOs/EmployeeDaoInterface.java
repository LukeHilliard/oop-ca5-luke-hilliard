package DAOs;

import DTOs.Employee;
import Exceptions.DaoException;
import java.util.List;
public interface EmployeeDaoInterface {

    public List<Employee> getAllEmployees() throws DaoException;
    public Employee getEmployeeById(int id) throws DaoException;
    public void deleteEmployee(int id) throws DaoException;
    public void addEmployee(Employee employee) throws DaoException;
    public Employee updateEmployee(int id, Employee employee) throws DaoException;
    public  List<Employee> getEmployeesMatchingFilter(String filter, boolean order) throws DaoException;

}
