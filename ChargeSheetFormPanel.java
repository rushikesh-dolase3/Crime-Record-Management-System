import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class ChargeSheetFormPanel extends JPanel {

    int firId;
    Connection con;

    // FIR labels
    JLabel lblApplicant, lblAccused, lblMobile,
            lblAddress, lblCrimeType, lblPoliceStation, lblStatus;

    // Charge sheet form fields
    JTextField txtSection;
    JTextField txtOfficer;
    JTextArea txtInvestigation;
    JTextArea txtRemark;
    JComboBox<String> cmbStatus;
    JButton btnUpdate;

    public ChargeSheetFormPanel(int firId) {
        this.firId = firId;

        setLayout(new BorderLayout());
        setBackground(new Color(236,240,245));

        connectDB();

        // ================= TITLE =================
        JLabel title = new JLabel(
                "FIR Number : " + firId,
                SwingConstants.CENTER
        );
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(220,53,69));
        title.setBorder(new EmptyBorder(15,10,15,10));
        add(title, BorderLayout.NORTH);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(new Color(236,240,245));
        wrapper.setBorder(new EmptyBorder(10,20,20,20));

        wrapper.add(createFirDetailsCard());
        wrapper.add(Box.createVerticalStrut(15));
        wrapper.add(createChargeSheetForm());

        add(new JScrollPane(wrapper), BorderLayout.CENTER);

        loadFirDetails();
    }

    // =====================================================
    // DB CONNECTION
    // =====================================================
    void connectDB() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres", "1234"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // =====================================================
    // FIR DETAILS CARD
    // =====================================================
    private JPanel createFirDetailsCard() {

        JPanel card = new JPanel(new GridLayout(0,4,10,10));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new EmptyBorder(15,15,15,15),
                new LineBorder(new Color(220,220,220))
        ));

        card.add(section("Complainer Details"));
        card.add(new JLabel()); card.add(new JLabel()); card.add(new JLabel());

        card.add(label("Applicant Name"));
        lblApplicant = value();
        card.add(lblApplicant);

        card.add(label("Mobile"));
        lblMobile = value();
        card.add(lblMobile);

        card.add(section("FIR Details"));
        card.add(new JLabel()); card.add(new JLabel()); card.add(new JLabel());

        card.add(label("Police Station"));
        lblPoliceStation = value();
        card.add(lblPoliceStation);

        card.add(label("Crime Type"));
        lblCrimeType = value();
        card.add(lblCrimeType);

        card.add(label("Accused Name"));
        lblAccused = value();
        card.add(lblAccused);

        card.add(label("Address"));
        lblAddress = value();
        card.add(lblAddress);

        card.add(label("Status"));
        lblStatus = value();
        card.add(lblStatus);
        card.add(new JLabel());

        return card;
    }

    // =====================================================
    // CHARGE SHEET FORM
    // =====================================================
    private JPanel createChargeSheetForm() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new EmptyBorder(15,15,15,15),
                new LineBorder(new Color(220,220,220))
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel heading = new JLabel("Fill Chargesheet Detail (Only for officer)");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 16));
        heading.setForeground(new Color(220,53,69));

        c.gridx=0; c.gridy=0; c.gridwidth=2;
        panel.add(heading, c);
        c.gridwidth=1;

        c.gridy++;
        panel.add(label("Section of Law"), c);
        c.gridx=1;
        txtSection = new JTextField();
        panel.add(txtSection, c);

        c.gridx=0; c.gridy++;
        panel.add(label("Investigation Officer"), c);
        c.gridx=1;
        txtOfficer = new JTextField();
        panel.add(txtOfficer, c);

        c.gridx=0; c.gridy++;
        panel.add(label("Investigation Detail"), c);
        c.gridx=1;
        txtInvestigation = new JTextArea(5,20);
        panel.add(new JScrollPane(txtInvestigation), c);

        c.gridx=0; c.gridy++;
        panel.add(label("Remark"), c);
        c.gridx=1;
        txtRemark = new JTextArea(3,20);
        panel.add(txtRemark, c);

        c.gridx=0; c.gridy++;
        panel.add(label("Status"), c);
        c.gridx=1;
        cmbStatus = new JComboBox<>(new String[]{"Charge Sheet Completed"});
        panel.add(cmbStatus, c);

        c.gridx=1; c.gridy++;
        btnUpdate = new JButton("Update");
        btnUpdate.setBackground(new Color(13,110,253));
        btnUpdate.setForeground(Color.WHITE);
        panel.add(btnUpdate, c);

        btnUpdate.addActionListener(e -> saveChargeSheet());

        return panel;
    }

    // =====================================================
    // LOAD FIR DETAILS
    // =====================================================
    void loadFirDetails() {
        try {
            String sql = """
            SELECT
                f.applicant_name,
                f.accused_name,
                f.contact_number,
                f.address,
                f.status,
                ps.station_name,
                ct.crime_name
            FROM fir f
            JOIN police_station ps ON f.station_id = ps.station_id
            JOIN crime_type ct ON f.crime_id = ct.crime_id
            WHERE f.fir_id = ?
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, firId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                lblApplicant.setText(rs.getString("applicant_name"));
                lblAccused.setText(rs.getString("accused_name"));
                lblMobile.setText(rs.getString("contact_number"));
                lblAddress.setText(rs.getString("address"));
                lblStatus.setText(rs.getString("status"));
                lblPoliceStation.setText(rs.getString("station_name"));
                lblCrimeType.setText(rs.getString("crime_name"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // =====================================================
    // SAVE CHARGE SHEET
    // =====================================================
    void saveChargeSheet() {
        try {
            String sql = """
            INSERT INTO charge_sheet
            (fir_id, section_of_law, investigation_officer,
             investigation_detail, remark, status)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, firId);
            ps.setString(2, txtSection.getText());
            ps.setString(3, txtOfficer.getText());
            ps.setString(4, txtInvestigation.getText());
            ps.setString(5, txtRemark.getText());
            ps.setString(6, cmbStatus.getSelectedItem().toString());

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "✅ Charge Sheet Saved Successfully");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // =====================================================
    // HELPERS
    // =====================================================
    JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    JLabel value() {
        JLabel l = new JLabel("-");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l.setForeground(new Color(80,80,80));
        return l;
    }

    JLabel section(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(new Color(13,110,253));
        return l;
    }
}