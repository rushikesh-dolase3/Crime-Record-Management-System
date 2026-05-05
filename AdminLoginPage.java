import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;

public class AdminLoginPage extends JFrame {

    JTextField txtUsername;
    JPasswordField txtPassword;
    Connection conn;

    public AdminLoginPage() {

        setTitle("Crime Record Management System - Admin Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ===== PREMIUM GRADIENT BACKGROUND =====
        JPanel background = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(20,30,48),
                        getWidth(), getHeight(),
                        new Color(36,59,85)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        background.setLayout(null);
        add(background);

        // ===== BACK BUTTON =====
        JButton btnBack = new JButton("← Back");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnBack.setForeground(Color.WHITE);
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setBounds(20, 20, 80, 30);

        btnBack.addActionListener(e -> {
            new WelcomeRoleSelectionFrame().setVisible(true);
            dispose();
        });

        btnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnBack.setForeground(new Color(180,220,255));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnBack.setForeground(Color.WHITE);
            }
        });

        background.add(btnBack);

        // ===== GLASS CARD =====
        JPanel glassCard = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(255,255,255,40));
                g2.fill(new RoundRectangle2D.Double(
                        0,0,getWidth(),getHeight(),30,30));
            }
        };

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        glassCard.setBounds(
                screen.width/2 - 210,
                screen.height/2 - 220,
                420, 440
        );

        glassCard.setLayout(new BoxLayout(glassCard, BoxLayout.Y_AXIS));
        glassCard.setOpaque(false);
        glassCard.setBorder(new EmptyBorder(40,40,40,40));

        // ===== TITLE =====
        JLabel title = new JLabel("Admin Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter Admin credentials to continue");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(220,220,220));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        glassCard.add(title);
        glassCard.add(Box.createVerticalStrut(8));
        glassCard.add(subtitle);
        glassCard.add(Box.createVerticalStrut(40));

        // ===== USERNAME =====
        JLabel lblUsername = createLabel("Admin Username");
        txtUsername = createTextField();

        // ===== PASSWORD =====
        JLabel lblPassword = createLabel("Password");
        txtPassword = createPasswordField();

        JButton btnLogin = createModernButton("Login");

        glassCard.add(lblUsername);
        glassCard.add(Box.createVerticalStrut(5));
        glassCard.add(txtUsername);

        glassCard.add(Box.createVerticalStrut(20));
        glassCard.add(lblPassword);
        glassCard.add(Box.createVerticalStrut(5));
        glassCard.add(txtPassword);

        glassCard.add(Box.createVerticalStrut(30));
        glassCard.add(btnLogin);

        background.add(glassCard);

        // ===== EVENTS =====
        btnLogin.addActionListener(e -> adminLogin());

        connectDB();
    }

    private JLabel createLabel(String text){
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return lbl;
    }

    private JTextField createTextField(){
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE,45));
        field.setFont(new Font("Segoe UI", Font.PLAIN,14));
        field.setBorder(new EmptyBorder(10,10,10,10));
        return field;
    }

    private JPasswordField createPasswordField(){
        JPasswordField field = new JPasswordField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE,45));
        field.setFont(new Font("Segoe UI", Font.PLAIN,14));
        field.setBorder(new EmptyBorder(10,10,10,10));
        field.setEchoChar('•');
        return field;
    }

    private JButton createModernButton(String text){
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.BOLD,15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(13,110,253));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE,50));

        btn.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseEntered(java.awt.event.MouseEvent e){
                btn.setBackground(new Color(25,135,84));
            }
            public void mouseExited(java.awt.event.MouseEvent e){
                btn.setBackground(new Color(13,110,253));
            }
        });

        return btn;
    }

    private void connectDB() {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres", "1234"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void adminLogin() {
        try {
            String sql = "SELECT id, username FROM users WHERE username=? AND password=? AND role='admin'";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, txtUsername.getText());
            ps.setString(2, new String(txtPassword.getPassword()));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                // 🔥 SESSION SET KARO
                UserSession.userId = rs.getInt("id");
                UserSession.username = rs.getString("username");
                UserSession.role = "admin";

                // 🔥 DEBUG (optional)
                System.out.println(UserSession.username);
                System.out.println(UserSession.role);

                new MainFrame().setVisible(true);
                dispose();

            } else {
                JOptionPane.showMessageDialog(this, "Invalid Admin Credentials!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}