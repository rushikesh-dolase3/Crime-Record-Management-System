import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddPolicePanel extends JPanel {

    JComboBox<String> cbStation;
    JTextField txtPoliceId, txtName, txtEmail, txtMobile;
    JTextArea txtAddress;
    JPasswordField txtPassword;

    Connection con;

    public AddPolicePanel() {

        setLayout(new GridBagLayout());
        setBackground(new Color(35, 50, 69));
        connectDB();

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(700, 450));
        card.setBorder(BorderFactory.createTitledBorder("Add Police Detail"));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        cbStation = new JComboBox<>();
        loadStations();

        txtPoliceId = new JTextField();
        txtName = new JTextField();
        txtEmail = new JTextField();
        txtMobile = new JTextField();
        txtAddress = new JTextArea(3, 20);
        txtPassword = new JPasswordField();

        JButton btnAdd = new JButton("Add");
        btnAdd.setBackground(new Color(13,110,253));
        btnAdd.setForeground(Color.WHITE);

        int y = 0;
        addRow(card, g, y++, "Police Station *", cbStation);
        addRow(card, g, y++, "Police ID *", txtPoliceId);
        addRow(card, g, y++, "Name *", txtName);
        addRow(card, g, y++, "Email *", txtEmail);
        addRow(card, g, y++, "Mobile Number *", txtMobile);
        addRow(card, g, y++, "Address *", new JScrollPane(txtAddress));
        addRow(card, g, y++, "Password *", txtPassword);

        g.gridx = 1;
        g.gridy = y;
        card.add(btnAdd, g);

        add(card);

        btnAdd.addActionListener(e -> addPolice());
    }

    // ================= ADD FORM ROW =================
    void addRow(JPanel p, GridBagConstraints g, int y, String label, Component c) {
        g.gridx = 0;
        g.gridy = y;
        p.add(new JLabel(label), g);
        g.gridx = 1;
        p.add(c, g);
    }

    // ================= DB CONNECTION =================
    void connectDB() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres",
                    "1234"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
        }
    }

    // ================= LOAD POLICE STATIONS =================
    void loadStations() {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT station_id, station_name FROM police_station"
            );

            while (rs.next()) {
                cbStation.addItem(
                        rs.getInt("station_id") + " - " +
                                rs.getString("station_name")
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= ADD POLICE =================
    void addPolice() {
        try {
            String policeId = txtPoliceId.getText().trim();

            // ✅ Validation: letters + numbers only
            if (!policeId.matches("[A-Za-z0-9]+")) {
                JOptionPane.showMessageDialog(this,
                        "Police ID must contain only letters and numbers");
                return;
            }

            String station = cbStation.getSelectedItem().toString();
            int stationId = Integer.parseInt(station.split(" - ")[0]);

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO police " +
                            "(police_id, station_id, name, email, mobile, address, password) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)"
            );

            ps.setString(1, policeId);
            ps.setInt(2, stationId);
            ps.setString(3, txtName.getText().trim());
            ps.setString(4, txtEmail.getText().trim());
            ps.setString(5, txtMobile.getText().trim());
            ps.setString(6, txtAddress.getText().trim());
            ps.setString(7, new String(txtPassword.getPassword()));

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Police Added Successfully!");

            // Clear fields
            txtPoliceId.setText("");
            txtName.setText("");
            txtEmail.setText("");
            txtMobile.setText("");
            txtAddress.setText("");
            txtPassword.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}