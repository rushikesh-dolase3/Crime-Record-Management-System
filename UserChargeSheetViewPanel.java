import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class UserChargeSheetViewPanel extends JPanel {

    Connection con;
    int firId;

    public UserChargeSheetViewPanel(int firId) {
        this.firId = firId;

        setLayout(new BorderLayout());
        setBackground(new Color(236,240,245));

        // ===== TITLE =====
        JLabel title = new JLabel("Charge Sheet Details");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(15,20,5,20));
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(10,20,20,20));
        content.setBackground(new Color(236,240,245));

        // FIR NUMBER
        JLabel firLabel = new JLabel("FIR Number : " + firId);
        firLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        firLabel.setForeground(Color.RED);
        firLabel.setBorder(new EmptyBorder(5,0,15,0));
        content.add(firLabel);

        // ===== PANELS =====
        JPanel complainantPanel = createSection("Complainant Details");
        JPanel firPanel = createSection("FIR Details");
        JPanel chargePanel = createSection("Charge Sheet Details");

        content.add(complainantPanel);
        content.add(Box.createVerticalStrut(15));
        content.add(firPanel);
        content.add(Box.createVerticalStrut(15));
        content.add(chargePanel);

        add(new JScrollPane(content), BorderLayout.CENTER);

        connectDB();
        loadData(complainantPanel, firPanel, chargePanel);
    }

    // ================= DB =================
    void connectDB() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres","1234"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    // ================= LOAD DATA =================
    void loadData(JPanel complainant, JPanel fir, JPanel charge) {
        try {
            String sql = """
                SELECT 
                    u.name AS user_name,
                    u.email,
                    f.contact_number,
                    ps.station_name,
                    ct.crime_name,
                    f.accused_name,
                    f.applicant_name,
                    f.parentage,
                    f.address,
                    f.relation_with_accused,
                    f.purpose_of_fir,
                    f.fir_date,
                    f.status,
                    f.police_remark,
                    f.police_remark_date,
                    cs.section_of_law,
                    cs.investigation_officer,
                    cs.investigation_detail,
                    cs.created_at
                FROM fir f
                JOIN users u ON f.user_id = u.id
                JOIN police_station ps ON f.station_id = ps.station_id
                JOIN crime_type ct ON f.crime_id = ct.crime_id
                JOIN charge_sheet cs ON f.fir_id = cs.fir_id
                WHERE f.fir_id = ?
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, firId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                // ===== COMPLAINANT =====
                addRow(complainant,"Name",rs.getString("user_name"),
                        "Mobile",rs.getString("contact_number"),
                        "Email",rs.getString("email"));

                // ===== FIR DETAILS =====
                addRow(fir,"Police Station",rs.getString("station_name"),
                        "Crime Type",rs.getString("crime_name"),
                        "Accused Name",rs.getString("accused_name"));

                addRow(fir,"Applicant Name",rs.getString("applicant_name"),
                        "Parentage",rs.getString("parentage"),
                        "Contact",rs.getString("contact_number"));

                addFullRow(fir,"Address",rs.getString("address"));

                addRow(fir,"Relation with Accused",rs.getString("relation_with_accused"),
                        "Purpose of FIR",rs.getString("purpose_of_fir"),
                        "Date of FIR",rs.getTimestamp("fir_date").toString());

                addRow(fir,"Order Final Status",rs.getString("status"),
                        "Police Remark",rs.getString("police_remark"),
                        "Remark Date",rs.getTimestamp("police_remark_date")+"");

                // ===== CHARGE SHEET =====
                addRow(charge,"Section of Law",rs.getString("section_of_law"),
                        "Investigation Officer",rs.getString("investigation_officer"),
                        "Charge Sheet Date",rs.getTimestamp("created_at").toString());

                addFullRow(charge,"Investigation Detail",
                        rs.getString("investigation_detail"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    // ================= UI HELPERS =================
    JPanel createSection(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new LineBorder(new Color(220,220,220)));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setBorder(new EmptyBorder(8,10,8,10));
        lbl.setForeground(new Color(13,110,253));

        p.add(lbl, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(0,6,10,10));
        body.setBorder(new EmptyBorder(10,10,10,10));
        body.setBackground(Color.WHITE);

        p.add(body, BorderLayout.CENTER);
        return p;
    }

    void addRow(JPanel section,String l1,String v1,String l2,String v2,String l3,String v3) {
        JPanel body = (JPanel)section.getComponent(1);

        body.add(label(l1)); body.add(value(v1));
        body.add(label(l2)); body.add(value(v2));
        body.add(label(l3)); body.add(value(v3));
    }

    void addFullRow(JPanel section,String label,String value) {
        JPanel body = (JPanel)section.getComponent(1);
        body.add(this.label(label));
        body.add(this.value(value));
        body.add(new JLabel()); body.add(new JLabel());
        body.add(new JLabel()); body.add(new JLabel());
    }

    JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI",Font.BOLD,13));
        return l;
    }

    JLabel value(String t) {
        JLabel l = new JLabel(t==null?"":t);
        l.setFont(new Font("Segoe UI",Font.PLAIN,13));
        return l;
    }
}