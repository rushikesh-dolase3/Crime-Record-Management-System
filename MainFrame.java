import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    JPanel sidebar, header, mainPanel;

    public MainFrame() {

        setTitle("Crime Record Management - Admin Panel");
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

        JLabel appTitle = new JLabel("ADMIN PANEL", SwingConstants.CENTER);
        appTitle.setForeground(Color.WHITE);
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appTitle.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(appTitle);
        sidebar.add(Box.createVerticalStrut(10));

        // ---------- MAIN BUTTONS ----------
        JButton btnDashboard = sideButton("Dashboard");
        sidebar.add(btnDashboard);

        // ---------- POLICE STATION DROPDOWN ----------
        JButton btnPoliceStation = dropdownButton("Police Station ▼");
        JPanel policeStationPanel = dropdownPanel();

        JButton btnAddStation = subButton("Add Police Station");
        JButton btnManageStation = subButton("Manage Police Station");

        policeStationPanel.add(btnAddStation);
        policeStationPanel.add(btnManageStation);

        sidebar.add(btnPoliceStation);
        sidebar.add(policeStationPanel);

        // ---------- POLICE DROPDOWN ----------
        JButton btnPolice = dropdownButton("Police ▼");
        JPanel policePanel = dropdownPanel();

        JButton btnAddPolice = subButton("Add Police");
        JButton btnManagePolice = subButton("Manage Police");

        policePanel.add(btnAddPolice);
        policePanel.add(btnManagePolice);

        sidebar.add(btnPolice);
        sidebar.add(policePanel);

        // ---------- REPORTS DROPDOWN ----------
        JButton btnReports = dropdownButton("Reports ▼");
        JPanel reportsPanel = dropdownPanel();

        JButton btnCriminalReport = subButton("B/W Date Criminal Report");
        JButton btnFIRReport = subButton("B/W Date FIR Report");

        reportsPanel.add(btnCriminalReport);
        reportsPanel.add(btnFIRReport);


        // ---------- SEARCH DROPDOWN ----------
        JButton btnSearch = dropdownButton("Search ▼");
        JPanel searchPanel = dropdownPanel();

        JButton btnSearchCriminal = subButton("Search Criminal");
        JButton btnSearchFIR = subButton("Search FIR / ChargeSheet");

        searchPanel.add(btnSearchCriminal);
        searchPanel.add(btnSearchFIR);



        // ---------- OTHER BUTTONS ----------
        JButton btnCrimeCategory = sideButton("Crime Category");
        JButton btnViewCriminals = sideButton("View Criminals");
        JButton btnViewFIR = sideButton("View FIR");

        sidebar.add(btnCrimeCategory);
        sidebar.add(btnViewCriminals);
        sidebar.add(btnViewFIR);
        sidebar.add(btnReports);
        sidebar.add(reportsPanel);
        sidebar.add(btnSearch);
        sidebar.add(searchPanel);




        add(sidebar, BorderLayout.WEST);

        // ================= HEADER =================
        header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(1300, 60));
        header.setBackground(new Color(52, 58, 64));

        JLabel lblWelcome = new JLabel(" Welcome, Admin");
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JLabel lblProfile = new JLabel(" Admin ▼ ");
        lblProfile.setForeground(Color.WHITE);
        lblProfile.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblProfile.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        JPopupMenu profileMenu = new JPopupMenu();
        JMenuItem mProfile = new JMenuItem("Profile");
        JMenuItem mSettings = new JMenuItem("Settings");
        JMenuItem mLogout = new JMenuItem("Logout");

        profileMenu.add(mProfile);
        profileMenu.add(mSettings);

        mProfile.addActionListener(e -> showPage(new ProfilePanel()));
        mSettings.addActionListener(e->showPage(new SettingsPanel()));
        
        profileMenu.addSeparator();
        profileMenu.add(mLogout);

        lblProfile.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
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

        // ================= ACTIONS =================
        btnDashboard.addActionListener(e -> showPage(blankPage("Dashboard")));

        btnPoliceStation.addActionListener(e -> toggle(policeStationPanel));
        btnPolice.addActionListener(e -> toggle(policePanel));

        btnReports.addActionListener(e -> toggle(reportsPanel));
        btnSearch.addActionListener(e -> toggle(searchPanel));

        btnAddStation.addActionListener(e -> showPage(new AddPoliceStationPanel()));
        btnManageStation.addActionListener(e -> showPage(new ManagePoliceStationPanel()));

        btnAddPolice.addActionListener(e -> showPage(new AddPolicePanel()));
        btnManagePolice.addActionListener(e -> showPage(new ManagePolicePanel()));

        // REPORTS ACTION
        btnCriminalReport.addActionListener(e -> showPage(new AdminCriminalReportPanel()));
        btnFIRReport.addActionListener(e -> showPage(new AdminFIRReportPanel()));

// SEARCH ACTION
        btnSearchCriminal.addActionListener(e -> showPage(new AdminSearchCriminalPanel()));
        btnSearchFIR.addActionListener(e -> showPage(new AdminSearchFIRPanel()));


        btnCrimeCategory.addActionListener(e -> showPage(new CrimeCategoryPanel()));
        btnViewCriminals.addActionListener(e -> showPage(new AdminViewCriminalPanel()));
        btnViewFIR.addActionListener(e -> showPage(new AdminViewFIRPanel()));
        btnReports.addActionListener(e -> showPage(blankPage("Reports")));
        btnSearch.addActionListener(e -> showPage(blankPage("Search")));

        mLogout.addActionListener(e -> {
            dispose();
            new AdminLoginPage().setVisible(true);
        });
    }

    // ================= STYLES =================
    private JButton sideButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(260, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(33, 37, 41));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 10));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(52, 58, 64)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(new Color(33, 37, 41)); }
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
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 35, 8, 10));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(60, 65, 70)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(new Color(44, 48, 52)); }
        });
        return btn;
    }

    private void toggle(JPanel panel) {
        panel.setVisible(!panel.isVisible());
        sidebar.revalidate();
    }

    // ================= PAGE SWITCH =================
    public void showPage(JPanel panel) {
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
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            new MainFrame().setVisible(true);
//        });
//    }
}