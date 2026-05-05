import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ProfilePanel extends JPanel {

    JLabel lblName, lblUsername, lblRole;

    public ProfilePanel() {

        setLayout(new GridBagLayout());
        setBackground(new Color(236,240,245));

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(400,300));
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));

        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblName = new JLabel();
        lblUsername = new JLabel();
        lblRole = new JLabel();

        card.add(title);
        card.add(Box.createVerticalStrut(20));
        card.add(lblName);
        card.add(Box.createVerticalStrut(10));
        card.add(lblUsername);
        card.add(Box.createVerticalStrut(10));
        card.add(lblRole);

        add(card);

        loadProfile();
    }

    private void loadProfile() {

        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres","1234"
            );

            if(UserSession.role.equals("admin") || UserSession.role.equals("user")) {

                String sql = "SELECT name, username, role FROM users WHERE username=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, UserSession.username);

                ResultSet rs = ps.executeQuery();

                if(rs.next()){
                    lblName.setText("Name: " + rs.getString("name"));
                    lblUsername.setText("Username: " + rs.getString("username"));
                    lblRole.setText("Role: " + rs.getString("role"));
                }

            } else if(UserSession.role.equals("police")) {

                String sql = "SELECT police_id, station_id FROM police WHERE police_id=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, UserSession.username);

                ResultSet rs = ps.executeQuery();

                if(rs.next()){
                    lblName.setText("Police ID: " + rs.getString("police_id"));
                    lblUsername.setText("Station ID: " + rs.getInt("station_id"));
                    lblRole.setText("Role: Police");
                }
            }

            con.close();

        } catch(Exception e){
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }
}