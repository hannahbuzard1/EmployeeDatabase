import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;

class main {
    public static void main(String[] args) throws Exception {
        String[] parameters = args[0].split(" ");
        if(parameters[0] == "show") {
          show(parameters);
        }
        if(parameters[0]== "add") {
          add(parameters);
        }
        if(parameters[0] == "delete") {
          delete(parameters);
        }
        if(parameters[2]=="sum") {
          sum(parameters);
        }
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
      
      PreparedStatement inserting = conn.prepareStatement("insert into employees values (null, null, null, null, null, null)");
                  connection.setString(1, args[0]);
                  connection.setString(2, args[1]);
                  connection.setString(3, args[2]);
                  connection.setString(4, args[3]);
                  connection.setString(5, args[4]);
                  connection.setInt(6, args[5]);
                  
    System.out.println("Employee successfully added to database. Employee: " + args[0] + " " + args[1] );
    }


    private static void delete(String[] args) {

    }
    
    private static void sum(String[] args) {

    }
}
