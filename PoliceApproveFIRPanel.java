import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java .sql.*;

public class PoliceApproveFIRPanel extends JPanel {

    JTable table;
    DefaultTableModel model;

    public PoliceApproveFIRPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(245,246,250));

        JLabel title = new JLabel("Approved FIR");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"FIR No","Applicant","Mobile","Date","Status"},0
        );

        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI",Font.PLAIN,13));
        table.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,13));

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadApprovedFIR();
    }

    private void loadApprovedFIR() {
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres","1234"
            );

            PreparedStatement ps = con.prepareStatement(
                    "SELECT fir_id, applicant_name, contact_number, created_at, status " +
                            "FROM fir " +
                            "WHERE status='Approved' AND station_id = ? " +
                            "ORDER BY fir_id DESC"
            );

            ps.setInt(1, PoliceSession.policeStationId);

            ResultSet rs = ps.executeQuery();

            model.setRowCount(0); // safety

            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("fir_id"),
                        rs.getString("applicant_name"),
                        rs.getString("contact_number"),
                        rs.getTimestamp("created_at"),
                        rs.getString("status")
                });
            }
            con.close();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }
}