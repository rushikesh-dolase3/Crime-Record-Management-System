import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class UserFIRHistory extends JPanel {

    int userId;
    JTable table;
    DefaultTableModel model;
    Connection con;

    public UserFIRHistory(int userId) {

        this.userId = userId;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== TITLE =====
        JLabel title = new JLabel("FIR History", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // ===== TABLE MODEL =====
        model = new DefaultTableModel(
                new String[]{"FIR ID", "Police Station", "Crime Type", "Status", "Date", "Action"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return column == 5; // ONLY button column editable
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);

        // ===== BUTTON COLUMN =====
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(
                new ButtonEditor(new JCheckBox(), table)
        );

        connectDB();
        loadFIRHistory();
    }

    // ===== DATABASE CONNECTION =====
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

    // ===== LOAD FIR DATA =====
    private void loadFIRHistory() {
        try {
            model.setRowCount(0);

            String sql = """
                SELECT f.fir_id,
                       ps.station_name,
                       ct.crime_name,
                       f.status,
                       f.created_at
                FROM fir f
                JOIN police_station ps ON f.station_id = ps.station_id
                JOIN crime_type ct ON f.crime_id = ct.crime_id
                WHERE f.user_id = ?
                ORDER BY f.created_at DESC
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("fir_id"),
                        rs.getString("station_name"),
                        rs.getString("crime_name"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        "View Details"
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= BUTTON RENDERER =================
    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setText("View Details");
            setBackground(new Color(0, 123, 255));
            setForeground(Color.WHITE);
            setFocusPainted(false);
        }

        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            return this;
        }
    }

    // ================= BUTTON EDITOR =================
    class ButtonEditor extends DefaultCellEditor {

        JButton button;
        int firId;

        public ButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);

            button = new JButton("View Details");
            button.setBackground(new Color(0, 123, 255));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);

            button.addActionListener(e -> {
                UserFrame frame =
                        (UserFrame) SwingUtilities.getWindowAncestor(UserFIRHistory.this);

                frame.showPage(new ViewFIRDetails(firId));
            });
        }

        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {

            firId = Integer.parseInt(table.getValueAt(row, 0).toString());
            return button;
        }

        public Object getCellEditorValue() {
            return "View Details";
        }
    }
}