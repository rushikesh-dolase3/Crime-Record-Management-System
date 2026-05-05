import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class PoliceFrame extends JFrame {

    JPanel sidebar, header, mainPanel;
    String policeId;
    int policeStationId;
    String policeStationName;

    public PoliceFrame() {

        setTitle("Crime Record Management - Police Panel");
        setSize(1300, 750);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================= LEFT SIDEBAR =================
        sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(260, 750));
        sidebar.setBackground(new Color(33, 37, 41));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel appTitle = new JLabel("POLICE PANEL", SwingConstants.CENTER);
        appTitle.setForeground(Color.WHITE);
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appTitle.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(appTitle);
        sidebar.add(Box.createVerticalStrut(15));

        JButton btnDashboard = sideButton("Dashboard");

        // ================= CRIMINALS DROPDOWN =================
        JButton btnCriminals = dropdownButton("Criminals ▼");
        JPanel criminalsPanel = dropdownPanel();

        JButton btnAddCriminal = subButton("Add Criminal");
        JButton btnManageCriminal = subButton("Manage Criminal");

        criminalsPanel.add(btnAddCriminal);
        criminalsPanel.add(btnManageCriminal);

        // ================= FIR DROPDOWN =================
        JButton btnFIR = dropdownButton("FIR ▼");
        JPanel firPanel = dropdownPanel();

        JButton btnNewFIR = subButton("New FIR");
        JButton btnApproveFIR = subButton("Approve FIR");
        JButton btnCancelledFIR = subButton("Cancelled FIR");
        JButton btnAllFIR = subButton("All FIR");

        firPanel.add(btnNewFIR);
        firPanel.add(btnApproveFIR);
        firPanel.add(btnCancelledFIR);
        firPanel.add(btnAllFIR);

        // ================= CHARGE SHEET DROPDOWN =================
        JButton btnChargeSheet = dropdownButton("Charge Sheet ▼");
        JPanel chargeSheetPanel = dropdownPanel();

        JButton btnNewChargeSheet = subButton("New Charge Sheet");
        JButton btnCompletedChargeSheet = subButton("Completed Charge Sheet");

        chargeSheetPanel.add(btnNewChargeSheet);
        chargeSheetPanel.add(btnCompletedChargeSheet);

        // ================= REPORTS DROPDOWN =================
        JButton btnReports = dropdownButton("Reports ▼");
        JPanel reportsPanel = dropdownPanel();

        JButton btnCriminalReport = subButton("B/W Date Report of Criminals");
        JButton btnFIRReport = subButton("B/W Date Report of FIR");

        reportsPanel.add(btnCriminalReport);
        reportsPanel.add(btnFIRReport);

        // ================= SEARCH DROPDOWN (ONLY CHANGE) =================
        JButton btnSearch = dropdownButton("Search ▼");
        JPanel searchPanel = dropdownPanel();

        JButton btnSearchCriminal = subButton("Search Criminals");
        JButton btnSearchFIR = subButton("Search FIR / ChargeSheet");

        searchPanel.add(btnSearchCriminal);
        searchPanel.add(btnSearchFIR);

        // ===== ADD TO SIDEBAR =====
        sidebar.add(btnDashboard);

        sidebar.add(btnCriminals);
        sidebar.add(criminalsPanel);

        sidebar.add(btnFIR);
        sidebar.add(firPanel);

        sidebar.add(btnChargeSheet);
        sidebar.add(chargeSheetPanel);

        sidebar.add(btnReports);
        sidebar.add(reportsPanel);

        sidebar.add(btnSearch);
        sidebar.add(searchPanel);

        add(sidebar, BorderLayout.WEST);

        // ================= HEADER =================
        header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(1300, 60));
        header.setBackground(new Color(52, 58, 64));

        JLabel lblWelcome = new JLabel(" Welcome, Police Officer");
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        loadPoliceName(lblWelcome);

        JLabel lblProfile = new JLabel(" Police ▼ ");
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

        mProfile.addActionListener(e-> {
            showPage(new ProfilePanel());
        });

        mSettings.addActionListener(e -> {
            showPage(new SettingsPanel());
        });

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

        // ================= ACTIONS =================
        btnCriminals.addActionListener(e -> toggle(criminalsPanel));
        btnFIR.addActionListener(e -> toggle(firPanel));
        btnChargeSheet.addActionListener(e -> toggle(chargeSheetPanel));
        btnReports.addActionListener(e -> toggle(reportsPanel));

        // 🔥 SEARCH TOGGLE
        btnSearch.addActionListener(e -> toggle(searchPanel));

        btnSearchCriminal.addActionListener(e ->
                showPage(new SearchCriminalPanel())
        );

        btnSearchFIR.addActionListener(e ->
                showPage(new SearchFIRPanel())
        );

        btnAddCriminal.addActionListener(e ->
                showPage(new AddCriminalPanel())
        );

        btnManageCriminal.addActionListener(e ->
                showPage(new ManageCriminalPanel())
        );

        btnNewFIR.addActionListener(e ->
                showPage(new PoliceNewFIRPanel())
        );

        btnApproveFIR.addActionListener(e ->
                showPage(new PoliceApproveFIRPanel())
        );

        btnCancelledFIR.addActionListener(e ->
                showPage(new PoliceCancelledFIRPanel())
        );

        btnNewChargeSheet.addActionListener(e ->
                showPage(new PoliceNewChargeSheetPanel())
        );

        btnCompletedChargeSheet.addActionListener(e ->
                showPage(new PoliceCompletedChargeSheetPanel())
        );

        btnCriminalReport.addActionListener(e ->
                showPage(new CriminalBetweenDatesPanel())
        );

        btnFIRReport.addActionListener(e ->
                showPage(new FIRBetweenDatesPanel())
        );

        mLogout.addActionListener(e -> {
            dispose();
            new PoliceLoginPage().setVisible(true);
        });
    }

    // ================= HELPERS =================
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

    private JButton dropdownButton(String text) {
        JButton btn = sideButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        return btn;
    }

    private JPanel dropdownPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(44, 48, 52));
        p.setVisible(false);
        return p;
    }

    private JButton subButton(String text) {
        JButton btn = new JButton("   • " + text);
        btn.setMaximumSize(new Dimension(260, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(44, 48, 52));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 35, 8, 10));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        return btn;
    }

    private void toggle(JPanel panel) {
        panel.setVisible(!panel.isVisible());
        sidebar.revalidate();
        sidebar.repaint();
    }

    void showPage(JPanel panel) {
        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void loadPoliceName(JLabel lblWelcome) {
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres", "1234"
            );

            PreparedStatement ps = con.prepareStatement(
                    "SELECT p.name AS police_name, ps.station_name " +
                            "FROM police p " +
                            "JOIN police_station ps ON p.police_station_id = ps.station_id " +
                            "WHERE p.police_id = ?"
            );

            ps.setString(1, PoliceSession.policeId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                lblWelcome.setText(
                        " Welcome, " +
                                rs.getString("police_name") +
                                " (" + rs.getString("station_name") + ")"
                );
            }
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}