import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {
      Scanner sc = new
              Scanner(System.in);
      UserDAO userDAO = new UserDAO();

      System.out.println("=== Crime Record Management System ===");
      System.out.println("Enter username: ");

      String uname = sc.nextLine();
      System.out.print("Enter password : ");

      String pass = sc.nextLine();

      User user = userDAO.login(uname, pass);
      if(user != null){
          System.out.println("Login successful ! welcome " + user.getName() +" ("+ user.getRole() +")");
      } else{
          System.out.println ("Invalid username or password !");
      }
    }
}