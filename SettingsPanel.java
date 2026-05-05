import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SettingsPanel extends JPanel {

    JPasswordField txtNewPass, txtConfirmPass;

    public SettingsPanel() {

        setLayout(new GridBagLayout());
        setBackground(new Color(236, 240, 245));

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(400, 300));
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Settings - Change Password");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtNewPass = new JPasswordField();
        txtConfirmPass = new JPasswordField();

        txtNewPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtConfirmPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton btnUpdate = new JButton("Update Password");
        btnUpdate.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(20));

        card.add(new JLabel("New Password"));
        card.add(txtNewPass);

        card.add(Box.createVerticalStrut(15));

        card.add(new JLabel("Confirm Password"));
        card.add(txtConfirmPass);

        card.add(Box.createVerticalStrut(20));
        card.add(btnUpdate);

        add(card);

        btnUpdate.addActionListener(e -> updatePassword());
    }

    private void updatePassword() {

        String newPass = new String(txtNewPass.getPassword());
        String confirmPass = new String(txtConfirmPass.getPassword());

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!");
            return;
        }

        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres", "1234"
            );

            String sql = "";

            if (UserSession.role.equals("admin") || UserSession.role.equals("user")) {
                sql = "UPDATE users SET password=? WHERE username=?";
            } else {
                sql = "UPDATE police SET password=? WHERE police_id=?";
            }

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, newPass);
            ps.setString(2, UserSession.username);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Password Updated Successfully!");

            con.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}