import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Supplier;

public class WelcomeRoleSelectionFrame extends JFrame {

    public WelcomeRoleSelectionFrame() {

        setTitle("Crime Record Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ===== BACKGROUND =====
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

        background.setLayout(new GridBagLayout());
        add(background);

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

        glassCard.setPreferredSize(new Dimension(520, 500)); // Increased width
        glassCard.setLayout(new BoxLayout(glassCard, BoxLayout.Y_AXIS));
        glassCard.setOpaque(false);
        glassCard.setBorder(new EmptyBorder(50,50,50,50));

        // ===== TITLE =====
        JLabel title = new JLabel("Crime Record Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Select Your Profession");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(220,220,220));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        glassCard.add(title);
        glassCard.add(Box.createVerticalStrut(10));
        glassCard.add(subtitle);
        glassCard.add(Box.createVerticalStrut(50));

        // ===== BUTTONS =====
        JButton btnUser = createModernButton("Continue as User");
        JButton btnPolice = createModernButton("Continue as Police");
        JButton btnAdmin = createModernButton("Continue as Admin");

        // VERY IMPORTANT FIX → Proper Center Alignment
        btnUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPolice.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAdmin.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnUser.setMaximumSize(new Dimension(300, 50));
        btnPolice.setMaximumSize(new Dimension(300, 50));
        btnAdmin.setMaximumSize(new Dimension(300, 50));

        glassCard.add(btnUser);
        glassCard.add(Box.createVerticalStrut(20));
        glassCard.add(btnPolice);
        glassCard.add(Box.createVerticalStrut(20));
        glassCard.add(btnAdmin);

        background.add(glassCard);

        // ===== ACTIONS =====
        btnUser.addActionListener(e -> openSmooth(() -> new UserLoginPage()));
        btnPolice.addActionListener(e -> openSmooth(() -> new PoliceLoginPage()));
        btnAdmin.addActionListener(e -> openSmooth(() -> new AdminLoginPage()));
    }

    private JButton createModernButton(String text) {

        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0,0,getWidth(),getHeight(),25,25);
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(13,110,253));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(12,20,12,20));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(25,135,84));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(13,110,253));
            }
        });

        return btn;
    }

    private void openSmooth(Supplier<JFrame> frameSupplier) {

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<JFrame, Void> worker = new SwingWorker<>() {
            protected JFrame doInBackground() {
                return frameSupplier.get();
            }

            protected void done() {
                try {
                    JFrame frame = get();
                    frame.setVisible(true);
                    setCursor(Cursor.getDefaultCursor());
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        };

        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new WelcomeRoleSelectionFrame().setVisible(true));
    }
}