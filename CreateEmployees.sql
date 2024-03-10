
/**
 * Main author: Luke Hilliard
 * Other contributors: ...
 *
 */
 /*//// with username & password ////*/
DROP DATABASE IF EXISTS `employees_database`;
CREATE DATABASE `employees_database`;
USE `employees_database`;

DROP TABLE IF EXISTS Employees;
CREATE TABLE Employees (
   `id` INT AUTO_INCREMENT PRIMARY KEY,
   `first_name` varchar(50) NOT NULL,
   `last_name` varchar(50) NOT NULL,
   `gender` varchar(6) NOT NULL,
   `dob` DATE NOT NULL,
   `salary` DOUBLE NOT NULL,
   `role` VARCHAR(50) NOT NULL,
   `username` VARCHAR(20) NOT NULL,
   `password` VARCHAR(20) NOT NULL
);

INSERT INTO Employees (id, first_name, last_name, gender, dob, salary, role, username, password)
VALUES
    (NULL, 'John', 'Doe', 'Male', '1990-05-15', 50000.00, 'Software Engineer', 'john_doe', 'password1'),
    (NULL, 'Jane', 'Smith', 'Female', '1988-09-20', 60000.00, 'Project Manager', 'jane_smith', 'password2'),
    (NULL, 'Michael', 'Johnson', 'Male', '1995-03-10', 70000.00, 'Data Analyst', 'michael_johnson', 'password3'),
    (NULL, 'Emily', 'Brown', 'Female', '1992-07-25', 55000.00, 'Graphic Designer', 'emily_brown', 'password4'),
    (NULL, 'David', 'Lee', 'Male', '1986-12-05', 48000.00, 'Customer Service', 'david_lee', 'password5'),
    (NULL, 'Sarah', 'Williams', 'Female', '1998-02-18', 52000.00, 'Marketing Specialist', 'sarah_williams', 'password6'),
    (NULL, 'Matthew', 'Taylor', 'Male', '1984-11-30', 75000.00, 'Financial Analyst', 'matthew_taylor', 'password7'),
    (NULL, 'Jessica', 'Anderson', 'Female', '1993-04-12', 67000.00, 'Human Resources Manager', 'jessica_anderson', 'password8'),
    (NULL, 'Christopher', 'Martinez', 'Male', '1991-08-07', 63000.00, 'Sales Representative', 'christopher_martinez', 'password9'),
    (NULL, 'Amanda', 'Garcia', 'Female', '1989-06-22', 59000.00, 'Software Developer', 'amanda_garcia', 'password10');

/*//// without username & password ////

DROP TABLE IF EXISTS Employees;
CREATE TABLE Employees (
                           `id` INT AUTO_INCREMENT PRIMARY KEY,
                           `first_name` varchar(50) NOT NULL,
                           `last_name` varchar(50) NOT NULL,
                           `gender` varchar(6) NOT NULL,
                           `dob` DATE NOT NULL,
                           `salary` DOUBLE NOT NULL,
                           `role` VARCHAR(50) NOT NULL
);

INSERT INTO Employees (id, first_name, last_name, gender, dob, salary, role)
VALUES
    (NULL, 'John', 'Doe', 'Male', '1990-05-15', 50000.00, 'Software Engineer'),
    (NULL, 'Jane', 'Smith', 'Female', '1988-09-20', 60000.00, 'Project Manager'),
    (NULL, 'Michael', 'Johnson', 'Male', '1995-03-10', 70000.00, 'Data Analyst'),
    (NULL, 'Emily', 'Brown', 'Female', '1992-07-25', 55000.00, 'Graphic Designer'),
    (NULL, 'David', 'Lee', 'Male', '1986-12-05', 48000.00, 'Customer Service'),
    (NULL, 'Sarah', 'Williams', 'Female', '1998-02-18', 52000.00, 'Marketing Specialist'),
    (NULL, 'Matthew', 'Taylor', 'Male', '1984-11-30', 75000.00, 'Financial Analyst'),
    (NULL, 'Jessica', 'Anderson', 'Female', '1993-04-12', 67000.00, 'Human Resources Manager'),
    (NULL, 'Christopher', 'Martinez', 'Male', '1991-08-07', 63000.00, 'Sales Representative'),
    (NULL, 'Amanda', 'Garcia', 'Female', '1989-06-22', 59000.00, 'Software Developer');*/