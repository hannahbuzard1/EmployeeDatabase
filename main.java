import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class main {

    private static Connection connection = null;
    private static PreparedStatement showStatement = null;
    private static long sum = -1;
    private static int nextEmployeeNumber = -1;
    private static PreparedStatement departmentNumberStatement = null;
    private static PreparedStatement addEmployeesTableStatement = null;
    private static PreparedStatement addDepartmentEmployeesTableStatement = null;
    private static PreparedStatement addSalariesTableStatement = null;
    private static PreparedStatement employeeNumberStatement = null;
    private static PreparedStatement firstLastNameStatement = null;
    private static PreparedStatement deleteEmployeesTableStatement = null;
    private static PreparedStatement deleteDepartmentEmployeesTableStatement = null;
    private static PreparedStatement deleteSalariesTableStatement = null;

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
                nextEmployeeNumber = Integer.parseInt(nextEmployeeNumberSet.getString("MAX"
                        + "(emp_no)")) + 1;
            }
            nextEmployeeNumberSet.close();

            // Attempt to initialize departmentNumberStatement
            departmentNumberStatement = connection.prepareStatement("SELECT dept_no FROM "
                    + "departments WHERE dept_name = ?");

            // Attempt to initialize addEmployeesTableStatement
            addEmployeesTableStatement = connection.prepareStatement("INSERT INTO employees (emp_no,"
                    + " birth_date, first_name, last_name, gender, hire_date) VALUES(?, ?, ?, ?, "
                    + "?, CURDATE())");

            // Attempt to initialize addDepartmentEmployeesTableStatement
            addDepartmentEmployeesTableStatement = connection.prepareStatement("INSERT INTO dept_emp"
                    + " (emp_no, dept_no, from_date, to_date) VALUES(?, ?, CURDATE(), "
                    + "'9999-01-01')");

            // Attempt to initialize addSalariesTableStatement
            addSalariesTableStatement = connection.prepareStatement("INSERT INTO salaries (emp_no, "
                    + "salary, from_date, to_date) VALUES(?, ?, CURDATE(), '9999-01-01')");

            // Attempt to initialize employeeNumberStatement
            employeeNumberStatement = connection.prepareStatement("SELECT * FROM employees "
                    + "where emp_no = ?");

            // Attempt to initialize firstLastNameStatement
            firstLastNameStatement = connection.prepareStatement("SELECT first_name, last_name"
                    + " FROM employees WHERE emp_no = ?");

            // Attempt to initialize deleteEmployeesTableStatement
            deleteEmployeesTableStatement = connection.prepareStatement("DELETE FROM employees where "
                    + "emp_no = ?");

            // Attempt to initialize deleteDepartmentEmployeesTableStatement
            deleteDepartmentEmployeesTableStatement = connection.prepareStatement("DELETE FROM dept_emp where "
                    + "emp_no = ?");

            // Attempt to initialize deleteSalariesTableStatement
            deleteSalariesTableStatement = connection.prepareStatement("DELETE FROM salaries where "
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
            departmentNumberStatement.close();
            // Attempt to close addEmployeesTableStatement
            addEmployeesTableStatement.close();
            // Attempt to close addDepartmentEmployeesTableStatement
            addDepartmentEmployeesTableStatement.close();
            // Attempt to close addSalariesTableStatement
            addSalariesTableStatement.close();
            // Attempt to close employeeNumberStatement
            employeeNumberStatement.close();
            // Attempt to close firstLastNameStatement
            firstLastNameStatement.close();
            // Attempt to close deleteEmployeesTableStatement
            deleteEmployeesTableStatement.close();
            // Attempt to close deleteDepartmentEmployeesTableStatement
            deleteDepartmentEmployeesTableStatement.close();
            // Attempt to close deleteSalariesTableStatement
            deleteSalariesTableStatement.close();
            // Attempt to close connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException ignored) {
        }
    }

    private static void show(String[] args) {
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

    private static void sum(String[] args) {
        if (args.length > 2) {
            System.out.println("$" + sum);
        } else {
            System.out.println("Not enough arguments for sum!");
        }
    }

    private static void add(String[] args) {
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
                departmentNumberStatement.setString(1, departmentName);
                final ResultSet departmentNumberSet = departmentNumberStatement.executeQuery();

                // If a row exists in departmentNumberSet, assign its value to departmentNumber
                if (departmentNumberSet.next()) {
                    final String departmentNumber = departmentNumberSet.getString("dept_no");

                    // Attempt to insert into employees table
                    addEmployeesTableStatement.setInt(1, nextEmployeeNumber);
                    if (args.length == 8) {
                        addEmployeesTableStatement.setString(2, args[5]);
                    } else {
                        addEmployeesTableStatement.setString(2, args[6]);
                    }
                    addEmployeesTableStatement.setString(3, args[2]);
                    addEmployeesTableStatement.setString(4, args[3]);
                    if (args.length == 8) {
                        addEmployeesTableStatement.setString(5, args[6]);
                    } else {
                        addEmployeesTableStatement.setString(5, args[7]);
                    }
                    addEmployeesTableStatement.execute();

                    // Attempt to insert into dept_emp table
                    addDepartmentEmployeesTableStatement.setInt(1, nextEmployeeNumber);
                    addDepartmentEmployeesTableStatement.setString(2, departmentNumber);
                    addDepartmentEmployeesTableStatement.execute();

                    // Attempt to insert into salaries table
                    addSalariesTableStatement.setInt(1, nextEmployeeNumber);
                    if (args.length == 8) {
                        addSalariesTableStatement.setInt(2, Integer.parseInt(args[7]));
                    } else {
                        addSalariesTableStatement.setInt(2, Integer.parseInt(args[8]));
                    }
                    addSalariesTableStatement.execute();

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

    private static void delete(String[] args) {
        if (args.length > 2) {
            try {
                // Attempt to execute query and store it
                employeeNumberStatement.setInt(1, Integer.parseInt(args[2]));
                final ResultSet employeeNumberSet = employeeNumberStatement.executeQuery();

                // If a row exists in employeeNumberSet, then proceed
                if (employeeNumberSet.next()) {
                    // Attempt to execute query and store it
                    firstLastNameStatement.setInt(1, Integer.parseInt(args[2]));
                    final ResultSet firstLastNameSet = firstLastNameStatement.executeQuery();

                    // If a row exists in firstLastNameSet, then proceed
                    if (firstLastNameSet.next()) {
                        String firstName = firstLastNameSet.getString("first_name");
                        String lastName = firstLastNameSet.getString("last_name");

                        // Attempt to delete from employees table
                        deleteEmployeesTableStatement.setInt(1, Integer.parseInt(args[2]));
                        deleteEmployeesTableStatement.execute();

                        // Attempt to delete from dept_emp table
                        deleteDepartmentEmployeesTableStatement.setInt(1,
                                Integer.parseInt(args[2]));
                        deleteDepartmentEmployeesTableStatement.execute();

                        // Attempt to delete from salaries table
                        deleteSalariesTableStatement.setInt(1, Integer.parseInt(args[2]));
                        deleteSalariesTableStatement.execute();

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