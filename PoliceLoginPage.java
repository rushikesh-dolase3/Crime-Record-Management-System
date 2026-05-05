import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;

public class PoliceLoginPage extends JFrame {

    JTextField txtPoliceId;
    JPasswordField txtPassword;
    JButton btnEye;
    Connection conn;

    public PoliceLoginPage() {

        setTitle("Crime Record Management System - Police Login");
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

        // ===== BACK BUTTON (LEFT TOP FLOATING SAME AS USER LOGIN) =====
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
                screen.height/2 - 240,
                420, 480
        );

        glassCard.setLayout(new BoxLayout(glassCard, BoxLayout.Y_AXIS));
        glassCard.setOpaque(false);
        glassCard.setBorder(new EmptyBorder(40,40,40,40));

        // ===== TITLE =====
        JLabel title = new JLabel("Police Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter Police credentials to continue");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(220,220,220));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        glassCard.add(title);
        glassCard.add(Box.createVerticalStrut(8));
        glassCard.add(subtitle);
        glassCard.add(Box.createVerticalStrut(40));

        // ===== POLICE ID =====
        JLabel lblPoliceId = createLabel("Police ID");
        txtPoliceId = createTextField();

        // ===== PASSWORD =====
        JLabel lblPassword = createLabel("Password");

        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        passPanel.setOpaque(false);

        txtPassword = createPasswordField();

        btnEye = new JButton("👁");
        btnEye.setFocusPainted(false);
        btnEye.setBorder(null);
        btnEye.setContentAreaFilled(false);
        btnEye.setForeground(Color.WHITE);
        btnEye.setCursor(new Cursor(Cursor.HAND_CURSOR));

        passPanel.add(txtPassword, BorderLayout.CENTER);
        passPanel.add(btnEye, BorderLayout.EAST);

        JButton btnLogin = createModernButton("Login");

        glassCard.add(lblPoliceId);
        glassCard.add(Box.createVerticalStrut(5));
        glassCard.add(txtPoliceId);

        glassCard.add(Box.createVerticalStrut(20));
        glassCard.add(lblPassword);
        glassCard.add(Box.createVerticalStrut(5));
        glassCard.add(passPanel);

        glassCard.add(Box.createVerticalStrut(30));
        glassCard.add(btnLogin);

        background.add(glassCard);

        // ===== EVENTS =====
        btnEye.addActionListener(e -> {
            if (txtPassword.getEchoChar() == '•') {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });

        btnLogin.addActionListener(e -> policeLogin());

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
                    "postgres",
                    "1234"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void policeLogin() {
        try {
            String sql = "SELECT police_id, station_id FROM police WHERE police_id=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, txtPoliceId.getText());
            ps.setString(2, new String(txtPassword.getPassword()));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {


                PoliceSession.policeId = rs.getString("police_id");
                PoliceSession.policeStationId = rs.getInt("station_id");

                UserSession.userId = rs.getInt("station_id");
                UserSession.username = rs.getString("police_id");
                UserSession.role = "police";


                System.out.println(UserSession.username);
                System.out.println(UserSession.role);

                new PoliceFrame().setVisible(true);
                dispose();

            } else {
                JOptionPane.showMessageDialog(this, "Invalid Police ID or Password!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}