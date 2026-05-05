import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CrimeCategoryPanel extends JPanel {

    JTextField txtCrimeName;
    JTable table;
    DefaultTableModel model;

    Connection con;

    public CrimeCategoryPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(230, 235, 240));

        connectDB();

        // ===== HEADER =====
        JLabel header = new JLabel("Crime Category Management", JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        header.setForeground(new Color(33, 37, 41));
        add(header, BorderLayout.NORTH);

        // ===== MAIN CARD =====
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== TOP FORM =====
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel lbl = new JLabel("Crime Type Name:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtCrimeName = new JTextField();
        txtCrimeName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCrimeName.setPreferredSize(new Dimension(200, 30));

        JButton btnAdd = createButton("Add", new Color(40, 167, 69));
        JButton btnUpdate = createButton("Update", new Color(0, 123, 255));
        JButton btnDelete = createButton("Delete", new Color(220, 53, 69));

        g.gridx = 0; g.gridy = 0;
        form.add(lbl, g);

        g.gridx = 1;
        form.add(txtCrimeName, g);

        g.gridx = 0; g.gridy = 1;
        form.add(btnAdd, g);

        g.gridx = 1;
        form.add(btnUpdate, g);

        g.gridx = 2;
        form.add(btnDelete, g);

        card.add(form, BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Crime Type");

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 58, 64));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        card.add(sp, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        loadData();

        // ===== EVENTS =====
        btnAdd.addActionListener(e -> addCrime());
        btnUpdate.addActionListener(e -> updateCrime());
        btnDelete.addActionListener(e -> deleteCrime());

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.getSelectedRow();
                txtCrimeName.setText(model.getValueAt(row,1).toString());
            }
        });
    }

    // ===== BUTTON STYLE =====
    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 35));
        return btn;
    }

    void connectDB() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres", "1234"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loadData() {
        try {
            model.setRowCount(0);

            ResultSet rs = con.createStatement().executeQuery(
                    "SELECT * FROM crime_type ORDER BY crime_id"
            );

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("crime_id"),
                        rs.getString("crime_name")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void addCrime() {
        try {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO crime_type (crime_name) VALUES (?)"
            );

            ps.setString(1, txtCrimeName.getText());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Crime Type Added");

            txtCrimeName.setText("");
            loadData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateCrime() {
        try {
            int row = table.getSelectedRow();
            int id = (int) model.getValueAt(row,0);

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE crime_type SET crime_name=? WHERE crime_id=?"
            );

            ps.setString(1, txtCrimeName.getText());
            ps.setInt(2, id);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Updated");

            loadData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void deleteCrime() {
        try {
            int row = table.getSelectedRow();
            int id = (int) model.getValueAt(row,0);

            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM crime_type WHERE crime_id=?"
            );

            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Deleted");

            loadData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}