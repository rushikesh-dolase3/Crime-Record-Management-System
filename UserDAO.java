import java.sql.*;
public class UserDAO {
    public User login(String username, String password){
        Connection conn = DBConnection.getConnection();
        try {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt =
                        conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs =
                    stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }
            }catch (Exception e){
            e.printStackTrace();
        }
        return null;

        }

}
