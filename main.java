import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;

class main {
    public static void main(String[] args) throws FileNotFoundException, SQLException {
        if (args.length < 1) {
            System.out.println("No arguments given!");
            return;
        }
        switch (args[0]) {
            case "show":
                if (args[1].equals("sum")) {
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
        File credentialsFile = new File("credentials.txt");
        Scanner scan = new Scanner(new FileInputStream(credentialsFile));
        String url = scan.nextLine();
        return DriverManager.getConnection(url);
    }

    private static void show(String[] args) throws SQLException, FileNotFoundException {
        StringBuilder query = new StringBuilder("SELECT emp_no, first_name, last_name FROM employees NATURAL JOIN dept_emp NATURAL JOIN departments WHERE dept_name = '");
        for (int i = 3; i < args.length; i++) {
            query.append(args[i]).append(" ");
        }
        query.append("'");

        ResultSet resultSet = getConnection().createStatement().executeQuery(query.toString());

        while (resultSet.next()) {
            int empNo = resultSet.getInt("emp_no");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            System.out.println(empNo + " " + firstName + " " + lastName);
        }
    }

    private static void add(String[] args) throws FileNotFoundException, SQLException {
        // Check for valid input size
        if (args.length < 8) {
            System.out.println("Not enough arguments for add!");
            return;
        }

        Connection connection = getConnection();
        // Find new emp_no
        ResultSet empNoSet = connection.createStatement().executeQuery("SELECT MAX(emp_no) FROM employees");
        empNoSet.next();
        int empNo = Integer.parseInt(empNoSet.getString("MAX(emp_no)")) + 1;
        // Find correct dept_no
        String deptName;
        if (args.length == 8) {
            deptName = args[4];
        } else {
            deptName = args[4] + " " + args[5];
        }
        ResultSet deptNoSet = connection.createStatement().executeQuery("SELECT dept_no FROM departments WHERE dept_name = '" + deptName + "'");
        deptNoSet.next();
        String dept_no = deptNoSet.getString("dept_no");

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

        // Now the dept_emp table
        command = "INSERT INTO dept_emp (emp_no, dept_no, from_date, to_date) VALUES(?, ?, CURDATE(), '9999-01-01')";
        statement = connection.prepareStatement(command);
        statement.setInt(1, empNo);
        statement.setString(2, dept_no);
        statement.execute();

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

        System.out.println("Employee: " + args[2] + " " + args[3] + " added!");
    }


    private static void delete(String[] args) throws FileNotFoundException, SQLException {
        // Check for valid input size
        if (args.length < 3) {
            System.out.println("Not enough arguments for delete!");
            return;
        }

        // Check if emp_no exists
        ResultSet empNoSet = getConnection().createStatement().executeQuery("SELECT * FROM employees where emp_no = " + args[2]);
        boolean exists = false;
        while (empNoSet.next()) {
            exists = true;
        }
        if (!exists) {
            System.out.println("Employee with id " + args[2] + " does not exist.");
            return;
        }

        // Get first name and last name for response
        String query = "SELECT first_name, last_name FROM employees WHERE emp_no = " + args[2];
        ResultSet name = getConnection().createStatement().executeQuery(query);
        name.next();
        String firstName = name.getString("first_name");
        String lastName = name.getString("last_name");

        // Delete from employees table
        String command = "DELETE FROM employees where emp_no = " + args[2];
        getConnection().createStatement().execute(command);

        // Delete from dept_emp table
        command = "DELETE FROM dept_emp where emp_no = " + args[2];
        getConnection().createStatement().execute(command);

        // Delete from salaries table
        command = "DELETE FROM salaries where emp_no = " + args[2];
        getConnection().createStatement().execute(command);

        System.out.println("Employee " + firstName + " " + lastName + " deleted!");
    }

    private static void sum(String[] args) throws FileNotFoundException, SQLException {
        // Check for valid input size
        if (args.length < 3) {
            System.out.println("Not enough arguments for add!");
            return;
        }

        ResultSet salarySumSet = getConnection().createStatement().executeQuery("SELECT SUM(salary) FROM salaries WHERE YEAR(to_date) > YEAR(CURDATE())");
        salarySumSet.next();
        System.out.println("$" + Long.parseLong(salarySumSet.getString("SUM(salary)")));
    }
}
