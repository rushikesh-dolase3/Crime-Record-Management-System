import javax.swing.*;
import java.awt.*;

public class UserFrame extends JFrame {

    int userId;
    String userName;

    JPanel sidebar, header, mainPanel;

    public UserFrame(int userId, String userName) {

        this.userId = userId;
        this.userName = userName;

        setTitle("Crime Record Management - User Panel");
        setSize(1200, 700);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================= LEFT SIDEBAR =================
        sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(260, 750));
        sidebar.setBackground(new Color(33, 37, 41));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel appTitle = new JLabel("USER PANEL", SwingConstants.CENTER);
        appTitle.setForeground(Color.WHITE);
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appTitle.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(appTitle);
        sidebar.add(Box.createVerticalStrut(15));

        JButton btnDashboard   = sideButton("Dashboard");
        JButton btnFIRForm     = sideButton("FIR Form");
        JButton btnFIRHistory  = sideButton("FIR History");
        JButton btnChargeSheet = sideButton("Charge Sheet");
        JButton btnSearchFIR   = sideButton("Search FIR");

        sidebar.add(btnDashboard);
        sidebar.add(btnFIRForm);
        sidebar.add(btnFIRHistory);
        sidebar.add(btnChargeSheet);
        sidebar.add(btnSearchFIR);

        add(sidebar, BorderLayout.WEST);

        // ================= HEADER =================
        header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(1300, 60));
        header.setBackground(new Color(52, 58, 64));

        JLabel lblWelcome = new JLabel(" Welcome, " + userName);
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JLabel lblProfile = new JLabel(" " + userName + " ▼ ");
        lblProfile.setForeground(Color.WHITE);
        lblProfile.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblProfile.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        JPopupMenu profileMenu = new JPopupMenu();
        JMenuItem mProfile = new JMenuItem("Profile");
        JMenuItem mSettings = new JMenuItem("Settings");
        JMenuItem mLogout = new JMenuItem("Logout");

        profileMenu.add(mProfile);
        profileMenu.add(mSettings);
        profileMenu.addSeparator();

        profileMenu.add(mLogout);

        lblProfile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                profileMenu.show(lblProfile, 0, lblProfile.getHeight());
            }
        });

        header.add(lblWelcome, BorderLayout.WEST);
        header.add(lblProfile, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ================= MAIN PANEL =================
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 245));
        add(mainPanel, BorderLayout.CENTER);

        showPage(blankPage("Dashboard"));

        // ================= BUTTON ACTIONS =================
        btnDashboard.addActionListener(e -> showPage(blankPage("Welcome")));

        // 🔥 IMPORTANT: userId pass ho raha hai
        btnFIRForm.addActionListener(e -> showPage(new FIRFormPanel(userId)));

        btnFIRHistory.addActionListener(e -> showPage(new UserFIRHistory(userId)));

        btnChargeSheet.addActionListener(e -> showPage(new UserChargeSheetListPanel(userId)));
        btnSearchFIR.addActionListener(e -> showPage(new UserSearchFIRPanel(userId)));

     mProfile.addActionListener(e-> {
         showPage(new ProfilePanel());
     });

        mSettings.addActionListener(e -> {
            showPage(new SettingsPanel());
        });

        mLogout.addActionListener(e -> {
            dispose();
            new UserLoginPage().setVisible(true);
        });
    }

    private JButton sideButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(260, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(33, 37, 41));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 10));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(52, 58, 64));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(33, 37, 41));
            }
        });
        return btn;
    }

    void showPage(JPanel panel) {
        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel blankPage(String title) {
        JPanel p = new JPanel(new GridBagLayout());
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        p.add(lbl);
        return p;
    }

}