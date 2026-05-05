import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class PoliceNewChargeSheetPanel extends JPanel {

    JTable table;
    DefaultTableModel model;
    Connection con;

    public PoliceNewChargeSheetPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(236,240,245));

        // ===== TITLE =====
        JLabel title = new JLabel("New Charge Sheet (Approved FIRs)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(15,20,15,20));
        add(title, BorderLayout.NORTH);

        // ===== TABLE MODEL =====
        model = new DefaultTableModel(
                new String[]{
                        "S.No",
                        "FIR ID",
                        "Applicant Name",
                        "Accused Name",
                        "Mobile",
                        "Police Station",
                        "FIR Date",
                        "Action"
                }, 0
        ) {
            public boolean isCellEditable(int row, int col) {
                return col == 7;
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        table.getColumn("Action").setCellRenderer(new ActionRenderer());
        table.getColumn("Action").setCellEditor(
                new ActionEditor(new JCheckBox())
        );

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
        loadApprovedFIRs();
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
    void loadApprovedFIRs() {
        try {
            model.setRowCount(0);
            int sn = 1;

            String sql = """
            SELECT
                f.fir_id,
                f.applicant_name,
                f.accused_name,
                f.contact_number,
                f.fir_date,
                ps.station_name
            FROM fir f
            JOIN police_station ps
                ON f.station_id = ps.station_id
            WHERE f.status = 'Approved'
              AND f.station_id = ?
              AND NOT EXISTS (
                  SELECT 1
                  FROM charge_sheet cs
                  WHERE cs.fir_id = f.fir_id
              )
            ORDER BY f.fir_id DESC
        """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, PoliceSession.policeStationId); // ✅ NOW VALID

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        sn++,
                        rs.getInt("fir_id"),
                        rs.getString("applicant_name"),
                        rs.getString("accused_name"),
                        rs.getString("contact_number"),
                        rs.getString("station_name"),
                        rs.getTimestamp("fir_date"),
                        "VIEW"
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= ACTION BUTTON =================
    class ActionRenderer extends JPanel implements TableCellRenderer {

        JButton btnView = new JButton("View Details");

        public ActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER,5,0));
            btnView.setBackground(new Color(13,110,253));
            btnView.setForeground(Color.WHITE);
            add(btnView);
        }

        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {
            return this;
        }
    }

    class ActionEditor extends DefaultCellEditor {

        JPanel panel;
        JButton btnView;
        int firId;

        public ActionEditor(JCheckBox cb) {
            super(cb);

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,0));
            btnView = new JButton("View Details");

            btnView.setBackground(new Color(13,110,253));
            btnView.setForeground(Color.WHITE);
            panel.add(btnView);

            btnView.addActionListener(e -> {
                PoliceFrame frame =
                        (PoliceFrame) SwingUtilities.getWindowAncestor(panel);

                // NEXT STEP PANEL (we will build next)
                frame.showPage(new ChargeSheetFormPanel(firId));

                fireEditingStopped();
            });
        }

        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int col) {

            firId = Integer.parseInt(table.getValueAt(row, 1).toString());
            return panel;
        }

        public Object getCellEditorValue() {
            return "VIEW";
        }
    }
}