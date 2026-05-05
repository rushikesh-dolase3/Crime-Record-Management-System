import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ViewFIRDetails extends JPanel {

    int firId;
    Connection con;

    JLabel lblFirNo, lblStatus;

    JLabel lblName, lblMobile;
    JLabel lblStation, lblCrimeType;
    JLabel lblAccused, lblApplicant;
    JLabel lblParentage, lblContact;
    JLabel lblAddress, lblPurpose;
    JLabel lblRelation, lblDate;
    JLabel lblRemark, lblRemarkDate;

    public ViewFIRDetails(int firId) {

        this.firId = firId;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 248));

        // ===== TITLE =====
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblFirNo = new JLabel("FIR Number: ");
        lblFirNo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblFirNo.setForeground(Color.RED);
        lblFirNo.setHorizontalAlignment(JLabel.CENTER);

        top.add(lblFirNo, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // ===== MAIN FORM =====
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(Color.WHITE);
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        main.add(sectionTitle("Complainant Details"));
        main.add(formRow("Name", lblName = new JLabel(), "Mobile Number", lblMobile = new JLabel()));

        main.add(Box.createVerticalStrut(15));

        main.add(sectionTitle("FIR Details"));
        main.add(formRow("Police Station", lblStation = new JLabel(), "Crime Type", lblCrimeType = new JLabel()));
        main.add(formRow("Name of Accused", lblAccused = new JLabel(), "Name of Applicant", lblApplicant = new JLabel()));
        main.add(formRow("Parentage", lblParentage = new JLabel(), "Contact Number", lblContact = new JLabel()));
        main.add(formRow("Address", lblAddress = new JLabel(), "Purpose of FIR", lblPurpose = new JLabel()));
        main.add(formRow("Relation with Accused", lblRelation = new JLabel(), "Date of FIR", lblDate = new JLabel()));
        main.add(formRow("Police Remark", lblRemark = new JLabel(), "Remark Date", lblRemarkDate = new JLabel()));

        main.add(Box.createVerticalStrut(20));

        lblStatus = new JLabel();
        lblStatus.setOpaque(true);
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        main.add(lblStatus);

        add(new JScrollPane(main), BorderLayout.CENTER);

        connectDB();
        loadDetails();
    }

    // ===== UI HELPERS =====
    private JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(new Color(40, 70, 130));
        l.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return l;
    }

    private JPanel formRow(String l1, JLabel v1, String l2, JLabel v2) {
        JPanel row = new JPanel(new GridLayout(1, 4, 10, 5));
        row.setBackground(Color.WHITE);

        row.add(fieldLabel(l1));
        row.add(fieldValue(v1));
        row.add(fieldLabel(l2));
        row.add(fieldValue(v2));

        return row;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return l;
    }

    private JLabel fieldValue(JLabel l) {
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    // ===== DATABASE =====
    private void connectDB() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres", "1234"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void loadDetails() {
        try {
            String sql = """
            SELECT f.*,
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

                lblFirNo.setText("FIR Number: " + rs.getInt("fir_id"));

                lblName.setText(rs.getString("applicant_name"));
                lblMobile.setText(rs.getString("contact_number"));

                lblStation.setText(rs.getString("station_name"));
                lblCrimeType.setText(rs.getString("crime_name"));
                lblAccused.setText(rs.getString("accused_name"));
                lblApplicant.setText(rs.getString("applicant_name"));
                lblParentage.setText(rs.getString("parentage"));
                lblContact.setText(rs.getString("contact_number"));
                lblAddress.setText(rs.getString("address"));
                lblPurpose.setText(rs.getString("purpose_of_fir"));
                lblRelation.setText(rs.getString("relation_with_accused"));
                lblDate.setText(String.valueOf(rs.getTimestamp("fir_date")));

                lblRemark.setText(rs.getString("police_remark"));

                Timestamp rd = rs.getTimestamp("police_remark_date");
                lblRemarkDate.setText(rd == null ? "-" : rd.toString());

                // 🔥 IMPORTANT FIX HERE
                String status = rs.getString("status");
                lblStatus.setText(status);
                lblStatus.setOpaque(true);

                if ("Cancelled".equalsIgnoreCase(status)) {
                    lblStatus.setBackground(new Color(221, 4, 4));
                }
                else if ("Approved".equalsIgnoreCase(status)) {
                    lblStatus.setBackground(new Color(4, 221, 65));
                }
                else {
                    lblStatus.setBackground(new Color(229, 253, 0, 255));
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}