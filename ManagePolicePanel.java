import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class ManagePolicePanel extends JPanel {

    JTable table;
    DefaultTableModel model;
    Connection con;

    public ManagePolicePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 245));

        connectDB();

        JLabel title = new JLabel("Manage Police");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 0));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Police ID", "Station", "Name", "Email", "Mobile", "Actions"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return c == 5;
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);

        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ButtonEditor());

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadPolice();
    }

    // ================= DATABASE =================
    void connectDB() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres", "1234");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= LOAD POLICE =================
    void loadPolice() {
        model.setRowCount(0);
        try {
            String sql =
                    "SELECT p.police_id, s.station_name, p.name, p.email, p.mobile " +
                            "FROM police p JOIN police_station s ON p.station_id = s.station_id";

            ResultSet rs = con.createStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("police_id"),
                        rs.getString("station_name"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("mobile"),
                        "ACTIONS"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= BUTTON RENDERER =================
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");

        ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

            btnUpdate.setBackground(new Color(13, 110, 253));
            btnUpdate.setForeground(Color.WHITE);

            btnDelete.setBackground(Color.RED);
            btnDelete.setForeground(Color.WHITE);

            add(btnUpdate);
            add(btnDelete);
        }

        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {
            return this;
        }
    }

    // ================= BUTTON EDITOR =================
    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {

        JPanel panel;
        JButton btnUpdate, btnDelete;
        int row;

        ButtonEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

            btnUpdate = new JButton("Update");
            btnDelete = new JButton("Delete");

            btnUpdate.setBackground(new Color(13, 110, 253));
            btnUpdate.setForeground(Color.WHITE);

            btnDelete.setBackground(Color.RED);
            btnDelete.setForeground(Color.WHITE);

            btnUpdate.addActionListener(e -> updatePolice(row));
            btnDelete.addActionListener(e -> deletePolice(row));

            panel.add(btnUpdate);
            panel.add(btnDelete);
        }

        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int r, int c) {
            row = r;
            return panel;
        }

        public Object getCellEditorValue() {
            return "";
        }
    }

    // ================= UPDATE POLICE =================
    void updatePolice(int row) {
        String policeId = model.getValueAt(row, 0).toString();
        String name = model.getValueAt(row, 2).toString();
        String email = model.getValueAt(row, 3).toString();
        String mobile = model.getValueAt(row, 4).toString();

        JTextField txtName = new JTextField(name);
        JTextField txtEmail = new JTextField(email);
        JTextField txtMobile = new JTextField(mobile);
        JPasswordField txtPassword = new JPasswordField();

        Object[] form = {
                "Name:", txtName,
                "Email:", txtEmail,
                "Mobile:", txtMobile,
                "New Password:", txtPassword
        };

        int op = JOptionPane.showConfirmDialog(
                this, form, "Update Police", JOptionPane.OK_CANCEL_OPTION);

        if (op == JOptionPane.OK_OPTION) {
            try {
                String pass = new String(txtPassword.getPassword());

                PreparedStatement ps;
                if (pass.isEmpty()) {
                    ps = con.prepareStatement(
                            "UPDATE police SET name=?, email=?, mobile=? WHERE police_id=?");
                    ps.setString(1, txtName.getText());
                    ps.setString(2, txtEmail.getText());
                    ps.setString(3, txtMobile.getText());
                    ps.setString(4, policeId);
                } else {
                    ps = con.prepareStatement(
                            "UPDATE police SET name=?, email=?, mobile=?, password=? WHERE police_id=?");
                    ps.setString(1, txtName.getText());
                    ps.setString(2, txtEmail.getText());
                    ps.setString(3, txtMobile.getText());
                    ps.setString(4, pass);
                    ps.setString(5, policeId);
                }

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Police Updated");
                loadPolice();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    // ================= DELETE POLICE =================
    void deletePolice(int row) {
        String policeId = model.getValueAt(row, 0).toString();

        int op = JOptionPane.showConfirmDialog(
                this, "Delete this police?", "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (op == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement ps =
                        con.prepareStatement("DELETE FROM police WHERE police_id=?");
                ps.setString(1, policeId);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Police Deleted");
                loadPolice();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }
}