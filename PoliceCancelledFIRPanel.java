import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class PoliceCancelledFIRPanel extends JPanel {

    JTable table;
    DefaultTableModel model;

    public PoliceCancelledFIRPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 250));

        JLabel title = new JLabel("Cancelled FIR");
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

        loadCancelledFIRs();
    }

    // 🔴 LOAD ONLY CANCELLED FIRs
    private void loadCancelledFIRs() {

        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres", "1234"
            );

            PreparedStatement ps = con.prepareStatement(
                    "SELECT fir_id, applicant_name, contact_number, created_at, status " +
                            "FROM fir " +
                            "WHERE status = 'Cancelled' AND station_id = ? " +
                            "ORDER BY fir_id DESC"
            );

            ps.setInt(1, PoliceSession.policeStationId);

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

    // ===== BUTTON RENDERER =====
    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setText("View Details");
            setBackground(Color.RED);
            setForeground(Color.WHITE);
            setFocusPainted(false);
        }

        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // ===== BUTTON EDITOR =====
    class ButtonEditor extends DefaultCellEditor {

        JButton button;
        int firId;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);

            button = new JButton("View Details");
            button.setBackground(Color.RED);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);

            button.addActionListener(e -> {
                PoliceFrame frame =
                        (PoliceFrame) SwingUtilities.getWindowAncestor(PoliceCancelledFIRPanel.this);

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