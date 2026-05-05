import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class UserChargeSheetListPanel extends JPanel {

    JTable table;
    DefaultTableModel model;
    Connection con;
    int userId;

    public UserChargeSheetListPanel(int userId) {
        this.userId = userId;

        setLayout(new BorderLayout());
        setBackground(new Color(236,240,245));

        JLabel title = new JLabel("My Charge Sheets");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(15,20,15,20));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"S.No", "FIR ID", "Crime Type", "FIR Date", "Status", "Action"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return c == 5;
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        table.getColumn("Action").setCellRenderer(new ActionRenderer());
        table.getColumn("Action").setCellEditor(new ActionEditor(new JCheckBox()));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new EmptyBorder(10,20,20,20));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(220,220,220)));
        card.add(sp);

        add(card, BorderLayout.CENTER);

        connectDB();
        loadData();
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

    // 🔥 ONLY FIX IS HERE
    void loadData() {
        try {
            model.setRowCount(0);
            int sn = 1;

            String sql = """
                SELECT 
                    f.fir_id,
                    ct.crime_name,
                    f.fir_date,
                    cs.status
                FROM fir f
                JOIN charge_sheet cs 
                    ON f.fir_id = cs.fir_id
                JOIN crime_type ct
                    ON f.crime_id = ct.crime_id
                WHERE f.user_id = ?
                  AND cs.status = 'Charge Sheet Completed'
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        sn++,
                        rs.getInt("fir_id"),
                        rs.getString("crime_name"),
                        rs.getDate("fir_date"),
                        rs.getString("status"),
                        "VIEW"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    // ===== ACTION BUTTON =====
    class ActionRenderer extends JPanel implements TableCellRenderer {
        JButton btn = new JButton("View");

        public ActionRenderer() {
            btn.setBackground(new Color(25,135,84));
            btn.setForeground(Color.WHITE);
            add(btn);
        }

        public Component getTableCellRendererComponent(
                JTable t,Object v,boolean s,boolean f,int r,int c) {
            return this;
        }
    }

    class ActionEditor extends DefaultCellEditor {

        JPanel panel = new JPanel();
        JButton btn = new JButton("View");
        int firId;

        public ActionEditor(JCheckBox cb) {
            super(cb);
            btn.setBackground(new Color(25,135,84));
            btn.setForeground(Color.WHITE);
            panel.add(btn);

            btn.addActionListener(e -> {
                UserFrame frame =
                        (UserFrame) SwingUtilities.getWindowAncestor(panel);

                frame.showPage(new UserChargeSheetViewPanel(firId));
                fireEditingStopped();
            });
        }

        public Component getTableCellEditorComponent(
                JTable t,Object v,boolean s,int r,int c) {
            firId = Integer.parseInt(t.getValueAt(r,1).toString());
            return panel;
        }

        public Object getCellEditorValue() { return "VIEW"; }
    }
}