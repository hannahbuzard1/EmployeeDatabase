import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;

class main {
    private main() {
    }

    public static void main(final String[] args) throws FileNotFoundException, SQLException {
        if (args.length < 1) {
            System.out.println("No arguments given!");
            return;
        }
        switch (args[0]) {
            case "show":
                if (args[2].equals("sum")) {
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
    }

    private static Connection getConnection() throws FileNotFoundException, SQLException {
        final File credentialsFile = new File("credentials.txt");
        final Scanner scan = new Scanner(new FileInputStream(credentialsFile));
        final String url = scan.nextLine();
        return DriverManager.getConnection(url);
    }

    private static void show(final String[] args) throws SQLException, FileNotFoundException {
        String query = "SELECT emp_no, first_name, last_name FROM employees NATURAL JOIN dept_emp NATURAL JOIN departments WHERE dept_name = ?";
        final PreparedStatement preparedStatement = getConnection().prepareStatement(query);
        String deptName;
        if (args.length == 4) {
            deptName = args[3];
        } else {
            deptName = args[3] + " " + args[4];
        }
        preparedStatement.setString(1, deptName);
        final ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            final int empNo = resultSet.getInt("emp_no");
            final String firstName = resultSet.getString("first_name");
            final String lastName = resultSet.getString("last_name");
            System.out.println(empNo + " " + firstName + " " + lastName);
        }
        resultSet.close();
    }

    private static void add(final String[] args) throws FileNotFoundException, SQLException {
        // Check for valid input size
        if (args.length < 8) {
            System.out.println("Not enough arguments for add!");
            return;
        }

        final Connection connection = getConnection();
        // Find new emp_no
        final ResultSet empNoSet = connection.createStatement().executeQuery("SELECT MAX(emp_no) FROM employees");
        int empNo = -1;
        if (empNoSet.next()) {
            empNo = Integer.parseInt(empNoSet.getString("MAX(emp_no)")) + 1;
        }
        empNoSet.close();
        // Find correct dept_no
        String deptName;
        if (args.length == 8) {
            deptName = args[4];
        } else {
            deptName = args[4] + " " + args[5];
        }
        final ResultSet deptNoSet = connection.createStatement().executeQuery("SELECT dept_no FROM departments WHERE dept_name = '" + deptName + "'");
        String deptNo = "";
        if (deptNoSet.next()) {
            deptNo= deptNoSet.getString("dept_no");
        }
        deptNoSet.close();

        // Begin with employees table
        String command = "INSERT INTO employees (emp_no, birth_date, first_name, last_name, gender, hire_date) VALUES(?, ?, ?, ?, ?, CURDATE())";
        PreparedStatement statement = connection.prepareStatement(command);
        statement.setInt(1, empNo);
        if (args.length == 8) {
            statement.setString(2, args[5]);
        } else {
            statement.setString(2, args[6]);
        }
        statement.setString(3, args[2]);
        statement.setString(4, args[3]);
        if (args.length == 8) {
            statement.setString(5, args[6]);
        } else {
            statement.setString(5, args[7]);
        }
        statement.execute();
        statement.close();

        // Now the dept_emp table
        command = "INSERT INTO dept_emp (emp_no, dept_no, from_date, to_date) VALUES(?, ?, CURDATE(), '9999-01-01')";
        statement = connection.prepareStatement(command);
        statement.setInt(1, empNo);
        statement.setString(2, deptNo);
        statement.execute();
        statement.close();

        // Finally, the salaries table
        command = "INSERT INTO salaries (emp_no, salary, from_date, to_date) VALUES(?, ?, CURDATE(), '9999-01-01')";
        statement = connection.prepareStatement(command);
        statement.setInt(1, empNo);
        if (args.length == 8) {
            statement.setInt(2, Integer.parseInt(args[7]));
        } else {
            statement.setInt(2, Integer.parseInt(args[8]));
        }
        statement.execute();
        statement.close();
        connection.close();

        System.out.println("Employee: " + args[2] + " " + args[3] + " added!");
    }


    private static void delete(final String[] args) throws FileNotFoundException, SQLException {
        // Check for valid input size
        if (args.length < 3) {
            System.out.println("Not enough arguments for delete!");
            return;
        }

        // Check if emp_no exists
        String query = "SELECT * FROM employees where emp_no = ?";
        PreparedStatement preparedStatement = getConnection().prepareStatement(query);
        try {
            preparedStatement.setInt(1, Integer.parseInt(args[2]));
        } catch (NumberFormatException e) {
            System.out.println("Employee with id " + args[2] + " does not exist.");
            return;
        }
        final ResultSet empNoSet = preparedStatement.executeQuery();
        boolean exists = false;
        while (empNoSet.next()) {
            exists = true;
        }
        if (!exists) {
            System.out.println("Employee with id " + args[2] + " does not exist.");
            return;
        }
        empNoSet.close();

        // Get first name and last name for response
        query = "SELECT first_name, last_name FROM employees WHERE emp_no = ?";
        preparedStatement = getConnection().prepareStatement(query);
        preparedStatement.setInt(1, Integer.parseInt(args[2]));
        final ResultSet name = preparedStatement.executeQuery();
        String firstName = "", lastName = "";
        if (name.next()) {
            firstName = name.getString("first_name");
            lastName = name.getString("last_name");
        }
        name.close();

        // Delete from employees table
        String command = "DELETE FROM employees where emp_no = ?";
        preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, Integer.parseInt(args[2]));
        preparedStatement.execute();

        // Delete from dept_emp table
        command = "DELETE FROM dept_emp where emp_no = ?";
        preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, Integer.parseInt(args[2]));
        preparedStatement.execute();

        // Delete from salaries table
        command = "DELETE FROM salaries where emp_no = ?";
        preparedStatement = getConnection().prepareStatement(command);
        preparedStatement.setInt(1, Integer.parseInt(args[2]));
        preparedStatement.execute();

        System.out.println("Employee " + firstName + " " + lastName + " deleted!");
    }

    private static void sum(final String[] args) throws FileNotFoundException, SQLException {
        // Check for valid input size
        if (args.length < 3) {
            System.out.println("Not enough arguments for add!");
            return;
        }

        final ResultSet salarySumSet = getConnection().createStatement().executeQuery("SELECT SUM(salary) FROM salaries WHERE YEAR(to_date) > YEAR(CURDATE())");
        if (salarySumSet.next()) {
            System.out.println("$" + Long.parseLong(salarySumSet.getString("SUM(salary)")));
        }
        salarySumSet.close();
    }
}
