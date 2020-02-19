import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;

class main {
    public static void main(String[] args) throws Exception {
        show();
    }

    private static void show() throws SQLException, FileNotFoundException {
        File credentialsFile = new File("credentials.txt");
        Scanner scan = new Scanner(new FileInputStream(credentialsFile));
        String url = scan.nextLine();
        Connection connection = DriverManager.getConnection(url);
        Statement statement = connection.createStatement();
        String testQuery = "select emp_no, first_name, last_name from employees where emp_no = 499999;";
        ResultSet resultSet = statement.executeQuery(testQuery);

        System.out.println("The records selected are:");
        int rowCount = 0;
        while (resultSet.next()) {
            int empNo = resultSet.getInt("emp_no");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            System.out.println(empNo + ", " + firstName + ", " + lastName);
            ++rowCount;
        }
        System.out.println("Total number of records = " + rowCount);
    }

    private static void add(String[] args) {

    }

    private static void delete(String[] args) {

    }
}
