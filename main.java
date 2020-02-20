import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;

class main {
    public static void main(String[] args) throws Exception {
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
                //add(args);
                break;
            case "delete":
                delete(args);
                break;
            default:
                System.out.println(args[0] + " is not supported!");
                break;
        }
    }

    private static Statement getStatement() throws FileNotFoundException, SQLException {
        File credentialsFile = new File("credentials.txt");
        Scanner scan = new Scanner(new FileInputStream(credentialsFile));
        String url = scan.nextLine();
        Connection connection = DriverManager.getConnection(url);
        return connection.createStatement();
    }

    private static void show(String[] args) throws SQLException, FileNotFoundException {
        String query = "select emp_no, first_name, last_name from employees natural join dept_emp natural join departments where dept_name = '";
        query += args[3];
        query += "'";

        System.out.println("Executing: " + query);

        ResultSet resultSet = getStatement().executeQuery(query);

        while (resultSet.next()) {
            int empNo = resultSet.getInt("emp_no");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            System.out.println(empNo + " " + firstName + " " + lastName);
        }
    }

    /*private static void add(String[] args) {
      Connection connect = DriverManager.getConnection("jdbc:mysql:3306/buzardh", "buzardh", "*u/aE7YzNf8/");
      String commands = "INSERT INTO employees (first_name, last_name, dept_name, birthdate, gender, salary) VALUES(?, ?, ?, ?, ?, ?)";
      PreparedStatement inserting = connect.prepareStatement(commands);
                  connect.setString(1, args[0]);
                  connect.setString(2, args[1]);
                  connect.setString(3, args[2]);
                  connect.setString(4, args[3]);
                  connect.setString(5, args[4]);
                  connect.setInt(6, args[5]);

      System.out.println("Employee successfully added to database. Employee: " + args[0] + " " + args[1] );
    }*/


    private static void delete(String[] args) {

    }
    
    private static void sum(String[] args) {

    }
}
