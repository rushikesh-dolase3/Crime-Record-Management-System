import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class PoliceNewFIRPanel extends JPanel {

    JTable table;
    DefaultTableModel model;

    public PoliceNewFIRPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 250));

        JLabel title = new JLabel("New FIR");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{
                        "FIR No",
                        "Name",
                        "Mobile Number",
                        "FIR Date",
                        "Status",
                        "Action"
                }, 0
        ) {
            public boolean isCellEditable(int row, int col) {
                return col == 5;
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        add(sp, BorderLayout.CENTER);

        loadNewFIRs();
    }

    // 🔹 ONLY logged-in police station FIRs
    private void loadNewFIRs() {

        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres", "1234"
            );

            PreparedStatement ps = con.prepareStatement(
                    "SELECT fir_id, applicant_name, contact_number, created_at, status " +
                            "FROM fir " +
                            "WHERE status='Pending' AND station_id=? " +
                            "ORDER BY fir_id DESC"
            );

            ps.setInt(1, PoliceSession.policeStationId); // 🔥 MAIN FIX

            ResultSet rs = ps.executeQuery();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("fir_id"),
                        rs.getString("applicant_name"),
                        rs.getString("contact_number"),
                        rs.getTimestamp("created_at"),
                        rs.getString("status"),
                        "View Details"
                });
            }

            con.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // 🔹 Button Renderer
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("View Details");
            setForeground(Color.WHITE);
            setBackground(new Color(13, 110, 253));
            setFocusPainted(false);
        }

        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // 🔹 Button Editor
    class ButtonEditor extends DefaultCellEditor {

        JButton button;
        int firId;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);

            button = new JButton("View Details");
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(13, 110, 253));
            button.setFocusPainted(false);

            button.addActionListener(e -> {
                PoliceFrame frame =
                        (PoliceFrame) SwingUtilities.getWindowAncestor(PoliceNewFIRPanel.this);

                frame.showPage(new PoliceViewFIRDetailsPanel(firId));
                fireEditingStopped();
            });
        }

        public Component getTableCellEditorComponent(
                JTable table, Object value,
                boolean isSelected, int row, int column) {

            firId = Integer.parseInt(table.getValueAt(row, 0).toString());
            return button;
        }
    }
}