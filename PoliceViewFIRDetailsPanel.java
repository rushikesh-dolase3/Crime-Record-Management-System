import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class PoliceViewFIRDetailsPanel extends JPanel {

    int firId;
    JLabel lblStatus, lblRemark, lblRemarkDate;
    Connection con;

    public PoliceViewFIRDetailsPanel(int firId) {

        this.firId = firId;
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 245));
        connectDB();

        JLabel title = new JLabel("FIR Number: " + firId, SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.RED);
        title.setBorder(new EmptyBorder(15,10,15,10));
        add(title, BorderLayout.NORTH);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new EmptyBorder(15,20,15,20),
                new LineBorder(new Color(220,220,220))
        ));

        // ================= Applicant =================
        card.add(sectionTitle("Applicant Details"));

        JLabel lblName = new JLabel();
        JLabel lblMobile = new JLabel();
        JLabel lblParentage = new JLabel();
        JLabel lblAddress = new JLabel();

        card.add(dataRow("Name", lblName, "Mobile", lblMobile));
        card.add(dataRow("Parentage", lblParentage, "Address", lblAddress));

        // ================= FIR =================
        card.add(Box.createVerticalStrut(10));
        card.add(sectionTitle("FIR Details"));

        JLabel lblStation = new JLabel();
        JLabel lblCrime = new JLabel();
        JLabel lblAccused = new JLabel();
        JLabel lblRelation = new JLabel();
        JLabel lblPurpose = new JLabel();
        JLabel lblDate = new JLabel();

        lblStatus = new JLabel();
        lblStatus.setOpaque(true);
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setBorder(new EmptyBorder(3,8,3,8));

        card.add(dataRow("Police Station", lblStation, "Crime Type", lblCrime));
        card.add(dataRow("Accused Name", lblAccused, "Relation", lblRelation));
        card.add(dataRow("Purpose of FIR", lblPurpose, "FIR Date", lblDate));
        card.add(dataRow("Final Status", lblStatus, "", new JLabel()));

        // ================= Police Action =================
        card.add(Box.createVerticalStrut(10));
        card.add(sectionTitle("Police Action"));

        lblRemark = new JLabel();
        lblRemarkDate = new JLabel();

        card.add(dataRow("Police Remark", lblRemark, "Remark Date", lblRemarkDate));

        JButton btnAction = new JButton("Take Action");
        btnAction.setBackground(new Color(13,110,253));
        btnAction.setForeground(Color.WHITE);
        btnAction.setFocusPainted(false);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnAction);

        loadData(lblName,lblMobile,lblParentage,lblAddress,
                lblStation,lblCrime,lblAccused,lblRelation,
                lblPurpose,lblDate);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(236,240,245));
        center.setBorder(new EmptyBorder(10,20,10,20));
        center.add(card, BorderLayout.CENTER);
        center.add(btnPanel, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        btnAction.addActionListener(e ->
                new TakeActionDialog(this,firId).setVisible(true)
        );
    }

    // ================= Helpers =================

    private JLabel sectionTitle(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(new Color(13,110,253));
        l.setBorder(new MatteBorder(0,0,1,0,new Color(220,220,220)));
        return l;
    }

    private JPanel dataRow(String l1, JLabel v1, String l2, JLabel v2) {
        JPanel p = new JPanel(new GridLayout(1,4,10,5));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(8,0,8,0));
        p.add(new JLabel(l1));
        p.add(v1);
        p.add(new JLabel(l2));
        p.add(v2);
        return p;
    }

    // ================= DB =================

    private void connectDB() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres","1234"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    private void loadData(
            JLabel name, JLabel mob, JLabel parentage, JLabel address,
            JLabel station, JLabel crime, JLabel accused,
            JLabel relation, JLabel purpose, JLabel date
    ) {

        try {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT f.*, ps.station_name, ct.crime_name " +
                            "FROM fir f " +
                            "JOIN police_station ps ON f.station_id=ps.station_id " +
                            "JOIN crime_type ct ON f.crime_id=ct.crime_id " +
                            "WHERE f.fir_id=?"
            );
            ps.setInt(1, firId);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                name.setText(rs.getString("applicant_name"));
                mob.setText(rs.getString("contact_number"));
                parentage.setText(rs.getString("parentage"));
                address.setText(rs.getString("address"));
                station.setText(rs.getString("station_name"));
                crime.setText(rs.getString("crime_name"));
                accused.setText(rs.getString("accused_name"));
                relation.setText(rs.getString("relation_with_accused"));
                purpose.setText(rs.getString("purpose_of_fir"));
                date.setText(rs.getTimestamp("created_at").toString());

                lblRemark.setText(
                        rs.getString("police_remark") == null ?
                                "—" : rs.getString("police_remark")
                );

                lblRemarkDate.setText(
                        rs.getTimestamp("police_remark_date") == null ?
                                "—" : rs.getTimestamp("police_remark_date").toString()
                );

                setStatus(rs.getString("status"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    void setStatus(String status) {
        lblStatus.setText(status);
        if ("Approved".equalsIgnoreCase(status))
            lblStatus.setBackground(new Color(25,135,84));
        else if ("Cancelled".equalsIgnoreCase(status))
            lblStatus.setBackground(Color.RED);
        else
            lblStatus.setBackground(Color.GRAY);
    }
}