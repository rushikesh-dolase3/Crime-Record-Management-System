import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class PoliceCompletedChargeSheetPanel extends JPanel {

    JTable table;
    DefaultTableModel model;
    Connection con;

    public PoliceCompletedChargeSheetPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(236,240,245));

        JLabel title = new JLabel("Completed Charge Sheets");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(15,20,15,20));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{
                        "S.No",
                        "FIR ID",
                        "Section of Law",
                        "Investigation Officer",
                        "Status",
                        "Created Date"
                }, 0
        );

        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new EmptyBorder(10,20,20,20));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new EmptyBorder(10,10,10,10),
                new LineBorder(new Color(220,220,220))
        ));
        card.add(sp);

        add(card, BorderLayout.CENTER);

        connectDB();
        loadCompletedChargeSheets();
    }

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

    void loadCompletedChargeSheets() {
        try {
            model.setRowCount(0);
            int sn = 1;

            String sql = """
            SELECT 
                cs.fir_id,
                cs.section_of_law,
                cs.investigation_officer,
                cs.status,
                cs.created_at
            FROM charge_sheet cs
            JOIN fir f ON cs.fir_id = f.fir_id
            WHERE cs.status = 'Charge Sheet Completed'
              AND f.station_id = ?
            ORDER BY cs.created_at DESC
        """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, PoliceSession.policeStationId); // 🔥 MAIN FIX

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        sn++,
                        rs.getInt("fir_id"),
                        rs.getString("section_of_law"),
                        rs.getString("investigation_officer"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }
}