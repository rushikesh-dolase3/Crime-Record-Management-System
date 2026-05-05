import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class ManageCriminalPanel extends JPanel {

    JTable table;
    DefaultTableModel model;
    Connection con;

    public ManageCriminalPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(236,240,245));

        // ===== TITLE =====
        JLabel title = new JLabel("Manage Criminals");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(15,20,15,20));
        add(title, BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new String[]{"S.No","photo", "Police Station", "Criminal ID", "Name", "Mobile Number", "Actions"}, 0
        ) {
            public boolean isCellEditable(int row, int col) {
                return col == 6;
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        table.getColumn("Actions").setCellRenderer(new ActionRenderer());
        table.getColumn("Actions").setCellEditor(
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
        loadCriminals();
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

    void loadCriminals() {
        try {
            model.setRowCount(0);
            int sn = 1;

            String sql = """
    SELECT c.criminal_id,
           c.name,
           c.contact_number,
           c.photo_path,
           ps.station_name
    FROM criminal c
    JOIN police_station ps
         ON c.station_id = ps.station_id
    WHERE c.station_id = ?
    ORDER BY c.criminal_id DESC
""";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, PoliceSession.policeStationId); // 🔥 MAIN FIX
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String photoPath = rs.getString("photo_path");
                ImageIcon icon = null;


                model.addRow(new Object[]{
                        sn++,
                        icon,
                        rs.getString("station_name"),
                        rs.getInt("criminal_id"),
                        rs.getString("name"),
                        rs.getString("contact_number"),
                        "ACTION"
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    // ================= ACTION BUTTONS =================
    class ActionRenderer extends JPanel implements TableCellRenderer {

        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");

        public ActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER,5,0));
            btnEdit.setBackground(new Color(13,110,253));
            btnEdit.setForeground(Color.WHITE);
            btnDelete.setBackground(new Color(220,53,69));
            btnDelete.setForeground(Color.WHITE);
            add(btnEdit);
            add(btnDelete);
        }

        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {
            return this;
        }
    }

    class ActionEditor extends DefaultCellEditor {

        JPanel panel;
        JButton btnEdit, btnDelete;
        int criminalId;

        public ActionEditor(JCheckBox cb) {
            super(cb);

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,0));

            btnEdit = new JButton("Edit");
            btnDelete = new JButton("Delete");

            btnEdit.setBackground(new Color(13,110,253));
            btnEdit.setForeground(Color.WHITE);
            btnDelete.setBackground(new Color(220,53,69));
            btnDelete.setForeground(Color.WHITE);

            panel.add(btnEdit);
            panel.add(btnDelete);

            btnEdit.addActionListener(e -> {
                PoliceFrame frame =
                        (PoliceFrame) SwingUtilities.getWindowAncestor(panel);
                frame.showPage(new EditCriminalPanel(criminalId));
                fireEditingStopped();
            });

            btnDelete.addActionListener(e -> deleteCriminal());
        }

        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int col) {

            criminalId = Integer.parseInt(table.getValueAt(row, 3).toString());
            return panel;
        }

        public Object getCellEditorValue() {
            return "ACTION";
        }

        void deleteCriminal() {
            int confirm = JOptionPane.showConfirmDialog(
                    panel,
                    "Delete this criminal?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    PreparedStatement ps =
                            con.prepareStatement("DELETE FROM criminal WHERE criminal_id=?");
                    ps.setInt(1,criminalId);
                    ps.executeUpdate();
                    loadCriminals();
                    fireEditingStopped();
                    JOptionPane.showMessageDialog(panel,"Deleted Successfully ✅");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(panel,e.getMessage());
                }
            }
            fireEditingStopped();
        }
    }
}