import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ForgotPasswordPage extends JFrame {

    JTextField txtEmail;
    JPasswordField txtNewPass;
    Connection conn;

    public ForgotPasswordPage() {

        setTitle("Forgot Password");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("RESET PASSWORD");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtEmail = new JTextField();
        txtNewPass = new JPasswordField();

        JButton btnReset = new JButton("Reset Password");
        btnReset.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(15));

        card.add(new JLabel("Registered Email"));
        card.add(txtEmail);

        card.add(Box.createVerticalStrut(10));
        card.add(new JLabel("New Password"));
        card.add(txtNewPass);

        card.add(Box.createVerticalStrut(20));
        card.add(btnReset);

        add(card, BorderLayout.CENTER);

        btnReset.addActionListener(e -> resetPassword());

        connectDB();
    }

    private void connectDB() {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres",
                    "1234"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void resetPassword() {

        String email = txtEmail.getText().trim();
        String newPass = new String(txtNewPass.getPassword()).trim();

        if (email.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        try {
            String checkSql = "SELECT id FROM users WHERE email=? AND role='user'";
            PreparedStatement checkPst = conn.prepareStatement(checkSql);
            checkPst.setString(1, email);

            ResultSet rs = checkPst.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Invalid Email!");
                return;
            }

            String updateSql = "UPDATE users SET password=? WHERE email=?";
            PreparedStatement pst = conn.prepareStatement(updateSql);
            pst.setString(1, newPass);
            pst.setString(2, email);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Password Reset Successfully!");

            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}