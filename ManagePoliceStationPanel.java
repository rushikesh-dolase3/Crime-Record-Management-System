import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class ManagePoliceStationPanel extends JPanel {

    JTable table;
    DefaultTableModel model;
    Connection con;

    public ManagePoliceStationPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(63, 67, 73, 239));

        connectDB();

        model = new DefaultTableModel(
                new String[]{"ID", "Police Station", "Code", "Created At"}, 0
        );

        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDelete = new JButton("Delete");
        JButton btnEdit = new JButton("Edit");

        btnEdit.setBackground(new Color(13, 110, 253));
        btnEdit.setForeground(Color.WHITE);

        btnDelete.setBackground(Color.RED);
        btnDelete.setForeground(Color.WHITE);

        top.add(btnEdit);
        top.add(btnDelete);

        add(top, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);

        loadData();

        btnDelete.addActionListener(e -> deleteStation());
        btnEdit.addActionListener(e -> editStation());
    }

    private void connectDB() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres",
                    "1234"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT station_id, station_name, station_code, created_at FROM police_station"
            );

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("station_id"),
                        rs.getString("station_name"),
                        rs.getString("station_code"),
                        rs.getTimestamp("created_at")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void deleteStation() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first");
            return;
        }

        int id = (int) model.getValueAt(row, 0);

        try {
            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM police_station WHERE station_id=?"
            );
            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Deleted Successfully");
            loadData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void editStation() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String name = model.getValueAt(row, 1).toString();
        String code = model.getValueAt(row, 2).toString();

        JTextField txtName = new JTextField(name);
        JTextField txtCode = new JTextField(code);

        Object[] fields = {
                "Station Name", txtName,
                "Station Code", txtCode
        };

        int option = JOptionPane.showConfirmDialog(
                this, fields, "Edit Police Station", JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            try {
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE police_station SET station_name=?, station_code=? WHERE station_id=?"
                );
                ps.setString(1, txtName.getText());
                ps.setString(2, txtCode.getText());
                ps.setInt(3, id);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Updated Successfully");
                loadData();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }
}