import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public final class main {

    private static Connection connection = null;
    private static PreparedStatement showStatement = null;
    private static long sum = -1;
    private static int nextEmpNum = -1;
    private static PreparedStatement depNumStatement = null;
    private static PreparedStatement addEmpStatement = null;
    private static PreparedStatement addDepStatement = null;
    private static PreparedStatement addSalStatement = null;
    private static PreparedStatement empNumStatement = null;
    private static PreparedStatement nameStatement = null;
    private static PreparedStatement delEmpStatement = null;
    private static PreparedStatement delDepStatement = null;
    private static PreparedStatement delSalStatement = null;

    static {
        try {
            // Attempt to initialize connection
            connection = DriverManager.getConnection(new Scanner(new FileInputStream(new File(
                    "credentials.txt"))).nextLine());

            // Attempt to initialize showStatement
            showStatement = connection.prepareStatement("SELECT emp_no, first_name, last_name "
                    + "FROM employees NATURAL JOIN dept_emp NATURAL JOIN departments "
                    + "WHERE dept_name = ?");

            // Attempt to initialize sum
            final ResultSet sumSet = connection.createStatement().executeQuery("SELECT SUM"
                    + "(salary) FROM salaries WHERE YEAR(to_date) > YEAR(CURDATE())");
            if (sumSet.next()) {
                sum = Long.parseLong(sumSet.getString("SUM(salary)"));
            }
            sumSet.close();

            // Attempt to initialize nextEmployeeNumber
            final ResultSet nextEmployeeNumberSet = connection.createStatement().executeQuery(
                    "SELECT MAX(emp_no) FROM employees");
            if (nextEmployeeNumberSet.next()) {
                nextEmpNum = Integer.parseInt(nextEmployeeNumberSet.getString("MAX"
                        + "(emp_no)")) + 1;
            }
            nextEmployeeNumberSet.close();

            // Attempt to initialize departmentNumberStatement
            depNumStatement = connection.prepareStatement("SELECT dept_no FROM "
                    + "departments WHERE dept_name = ?");

            // Attempt to initialize addEmployeesTableStatement
            addEmpStatement = connection.prepareStatement("INSERT INTO employees (emp_no,"
                    + " birth_date, first_name, last_name, gender, hire_date) VALUES(?, ?, ?, ?, "
                    + "?, CURDATE())");

            // Attempt to initialize addDepartmentEmployeesTableStatement
            addDepStatement = connection.prepareStatement("INSERT INTO dept_emp"
                    + " (emp_no, dept_no, from_date, to_date) VALUES(?, ?, CURDATE(), "
                    + "'9999-01-01')");

            // Attempt to initialize addSalariesTableStatement
            addSalStatement = connection.prepareStatement("INSERT INTO salaries (emp_no, "
                    + "salary, from_date, to_date) VALUES(?, ?, CURDATE(), '9999-01-01')");

            // Attempt to initialize employeeNumberStatement
            empNumStatement = connection.prepareStatement("SELECT * FROM employees "
                    + "where emp_no = ?");

            // Attempt to initialize firstLastNameStatement
            nameStatement = connection.prepareStatement("SELECT first_name, last_name"
                    + " FROM employees WHERE emp_no = ?");

            // Attempt to initialize deleteEmployeesTableStatement
            delEmpStatement = connection.prepareStatement("DELETE FROM employees where "
                    + "emp_no = ?");

            // Attempt to initialize deleteDepartmentEmployeesTableStatement
            delDepStatement = connection.prepareStatement("DELETE FROM dept_emp where "
                    + "emp_no = ?");

            // Attempt to initialize deleteSalariesTableStatement
            delSalStatement = connection.prepareStatement("DELETE FROM salaries where "
                    + "emp_no = ?");
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private main() {
    }

    public static void main(final String[] args) {
        // Check if any arguments were given
        if (args.length > 1) {
            // Determine which function to run
            switch (args[0]) {
                case "show":
                    if (args.length > 2 && args[2].equals("sum")) {
                        sum(args);
                    } else {
                        show(args);
                    }
                    break;
                case "add":
                    add(args);
                    break;
                case "delete":
                    delete(args);
                    break;
                default:
                    System.out.println(args[0] + " is not supported!");
                    break;
            }
        } else {
            System.out.println("No arguments given!");
        }

        try {
            // Attempt to close showStatement
            showStatement.close();
            // Attempt to close departmentNumberStatement
            depNumStatement.close();
            // Attempt to close addEmployeesTableStatement
            addEmpStatement.close();
            // Attempt to close addDepartmentEmployeesTableStatement
            addDepStatement.close();
            // Attempt to close addSalariesTableStatement
            addSalStatement.close();
            // Attempt to close employeeNumberStatement
            empNumStatement.close();
            // Attempt to close firstLastNameStatement
            nameStatement.close();
            // Attempt to close deleteEmployeesTableStatement
            delEmpStatement.close();
            // Attempt to close deleteDepartmentEmployeesTableStatement
            delDepStatement.close();
            // Attempt to close deleteSalariesTableStatement
            delSalStatement.close();
            // Attempt to close connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException ignored) {
        }
    }

    private static void show(final String[] args) {
        if (args.length > 3) {
            // Declare and initialize departmentName based on input size
            final String departmentName;
            if (args.length == 4) {
                departmentName = args[3];
            } else {
                departmentName = args[3] + " " + args[4];
            }

            try {
                // Attempt to insert departmentName into statement
                showStatement.setString(1, departmentName);
                // Attempt to execute query and store it
                final ResultSet showSet = showStatement.executeQuery();

                // Attempt to iterate through every row of showSet and print all column values
                while (showSet.next()) {
                    final int employeeNumber = showSet.getInt("emp_no");
                    final String firstName = showSet.getString("first_name");
                    final String lastName = showSet.getString("last_name");
                    System.out.println(employeeNumber + " " + firstName + " " + lastName);
                }

                // Attempt to close showSet
                showSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Not enough arguments for show!");
        }
    }

    private static void sum(final String[] args) {
        if (args.length > 2) {
            System.out.println("$" + sum);
        } else {
            System.out.println("Not enough arguments for sum!");
        }
    }

    private static void add(final String[] args) {
        if (args.length > 7) {
            // Declare and initialize departmentName based on input size
            String departmentName;
            if (args.length == 8) {
                departmentName = args[4];
            } else {
                departmentName = args[4] + " " + args[5];
            }

            try {
                // Attempt to execute query and store it
                depNumStatement.setString(1, departmentName);
                final ResultSet departmentNumberSet = depNumStatement.executeQuery();

                // If a row exists in departmentNumberSet, assign its value to departmentNumber
                if (departmentNumberSet.next()) {
                    final String departmentNumber = departmentNumberSet.getString("dept_no");

                    // Attempt to insert into employees table
                    addEmpStatement.setInt(1, nextEmpNum);
                    if (args.length == 8) {
                        addEmpStatement.setString(2, args[5]);
                    } else {
                        addEmpStatement.setString(2, args[6]);
                    }
                    addEmpStatement.setString(3, args[2]);
                    addEmpStatement.setString(4, args[3]);
                    if (args.length == 8) {
                        addEmpStatement.setString(5, args[6]);
                    } else {
                        addEmpStatement.setString(5, args[7]);
                    }
                    addEmpStatement.execute();

                    // Attempt to insert into dept_emp table
                    addDepStatement.setInt(1, nextEmpNum);
                    addDepStatement.setString(2, departmentNumber);
                    addDepStatement.execute();

                    // Attempt to insert into salaries table
                    addSalStatement.setInt(1, nextEmpNum);
                    if (args.length == 8) {
                        addSalStatement.setInt(2, Integer.parseInt(args[7]));
                    } else {
                        addSalStatement.setInt(2, Integer.parseInt(args[8]));
                    }
                    addSalStatement.execute();

                    // Print success message
                    System.out.println("Employee: " + args[2] + " " + args[3] + " added!");
                } else {
                    System.out.println("Department " + departmentName + " doesn't exist.");
                }

                // Attempt to close departmentNumberSet
                departmentNumberSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Not enough arguments for add!");
        }
    }

    private static void delete(final String[] args) {
        if (args.length > 2) {
            try {
                // Attempt to execute query and store it
                empNumStatement.setInt(1, Integer.parseInt(args[2]));
                final ResultSet employeeNumberSet = empNumStatement.executeQuery();

                // If a row exists in employeeNumberSet, then proceed
                if (employeeNumberSet.next()) {
                    // Attempt to execute query and store it
                    nameStatement.setInt(1, Integer.parseInt(args[2]));
                    final ResultSet firstLastNameSet = nameStatement.executeQuery();

                    // If a row exists in firstLastNameSet, then proceed
                    if (firstLastNameSet.next()) {
                        String firstName = firstLastNameSet.getString("first_name");
                        String lastName = firstLastNameSet.getString("last_name");

                        // Attempt to delete from employees table
                        delEmpStatement.setInt(1, Integer.parseInt(args[2]));
                        delEmpStatement.execute();

                        // Attempt to delete from dept_emp table
                        delDepStatement.setInt(1,
                                Integer.parseInt(args[2]));
                        delDepStatement.execute();

                        // Attempt to delete from salaries table
                        delSalStatement.setInt(1, Integer.parseInt(args[2]));
                        delSalStatement.execute();

                        // Print success message
                        System.out.println("Employee " + firstName + " " + lastName + " deleted!");
                    }

                    // Attempt to close firstLastNameSet
                    firstLastNameSet.close();
                } else {
                    throw new NumberFormatException();
                }

                // Attempt to close employeeNumberSet
                employeeNumberSet.close();
            } catch (NumberFormatException e) {
                System.out.println("Employee with id " + args[2] + " does not exist.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Not enough arguments for delete!");
        }
    }

}