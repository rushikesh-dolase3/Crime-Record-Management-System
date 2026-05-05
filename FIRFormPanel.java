import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class FIRFormPanel extends JPanel {

    int userId;   // 🔥 VERY IMPORTANT

    JComboBox<String> cbStation, cbCrime;
    JTextField txtAccused, txtName, txtParentage, txtContact, txtRelation, txtPurpose;
    JTextArea txtAddress;

    Connection con;

    public FIRFormPanel(int userId) {

        this.userId = userId;

        setLayout(new GridBagLayout());
        setBackground(new Color(35, 50, 69));
        connectDB();

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(950, 600));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("FIR Form");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        card.add(title, g);
        g.gridwidth = 1;

        int y = 1;

       cbStation = new JComboBox<>();
       cbStation.addItem("Select Police Station");
       loadPoliceStations();

       cbCrime = new JComboBox<>();
       loadCrimes();


        txtAccused = new JTextField();
        txtName = new JTextField();
        txtParentage = new JTextField();
        txtContact = new JTextField();
        txtRelation = new JTextField();
        txtPurpose = new JTextField();

        txtAddress = new JTextArea(3, 20);
        txtAddress.setLineWrap(true);

        addRow(card, g, y++, "Police Station *", cbStation);
        addRow(card, g, y++, "Crime Type *", cbCrime);
        addRow(card, g, y++, "Name of Accused *", txtAccused);

        JLabel victimLbl = new JLabel("Applicant's Detail (Victim)");
        victimLbl.setForeground(Color.RED);
        victimLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

        g.gridx = 0; g.gridy = y++; g.gridwidth = 2;
        card.add(victimLbl, g);
        g.gridwidth = 1;

        addRow(card, g, y++, "Name *", txtName);
        addRow(card, g, y++, "Parentage *", txtParentage);
        addRow(card, g, y++, "Contact Number *", txtContact);
        addRow(card, g, y++, "Address *", new JScrollPane(txtAddress));
        addRow(card, g, y++, "Relation with accused person *", txtRelation);
        addRow(card, g, y++, "Purpose of applying copy of FIR *", txtPurpose);

        JButton btnSubmit = new JButton("Submit");
        btnSubmit.setBackground(new Color(13,110,253));
        btnSubmit.setForeground(Color.WHITE);

        g.gridx = 1; g.gridy = y + 1;
        card.add(btnSubmit, g);

        add(card);

        btnSubmit.addActionListener(e -> submitFIR());
    }

    void loadCrimes() {
        try {
            cbCrime.removeAllItems();
            cbCrime.addItem("Select Crime Type");

            ResultSet rs = con.createStatement()
                    .executeQuery("SELECT crime_name FROM crime_type");

            while (rs.next()) {
                cbCrime.addItem(rs.getString("crime_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPoliceStations() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT station_id, station_name FROM police_station ORDER BY station_name"
            );
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cbStation.addItem(
                        rs.getInt("station_id") + " - " + rs.getString("station_name")
                );
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void addRow(JPanel p, GridBagConstraints g, int y, String lbl, Component field) {
        g.gridx = 0; g.gridy = y;
        p.add(new JLabel(lbl), g);
        g.gridx = 1;
        p.add(field, g);
    }

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

    private int getStationId() {
        String selected = cbStation.getSelectedItem().toString();
        if (selected.equals("Select Police Station")) return 0;
        return Integer.parseInt(selected.split(" - ")[0]);
    }

    private int getCrimeId() {
        try {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT crime_id FROM crime_type WHERE crime_name=?"
            );

            ps.setString(1, cbCrime.getSelectedItem().toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("crime_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    private void submitFIR() {

        if (cbCrime.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select Crime Type");
            return;
        }

        if (cbStation.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select Police Station");
            return;
        }

        if (txtName.getText().isEmpty() || txtAccused.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields");
            return;
        }

        try {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO fir (user_id, station_id, crime_id, accused_name, applicant_name, parentage, " +
                            "contact_number, address, relation_with_accused, purpose_of_fir, status, created_at) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())"
            );

            ps.setInt(1, userId);                 // from UserFrame
            ps.setInt(2, getStationId()); // 🔥 station_id
            ps. setInt(3,getCrimeId());

            ps.setString(4, txtAccused.getText());
            ps.setString(5, txtName.getText());
            ps.setString(6, txtParentage.getText());
            ps.setString(7, txtContact.getText());
            ps.setString(8, txtAddress.getText());
            ps.setString(9, txtRelation.getText());
            ps.setString(10, txtPurpose.getText());
            ps.setString(11, "Pending");          // police approval

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "FIR Submitted Successfully ✅");
            clearForm();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void clearForm() {
        txtAccused.setText("");
        txtName.setText("");
        txtParentage.setText("");
        txtContact.setText("");
        txtAddress.setText("");
        txtRelation.setText("");
        txtPurpose.setText("");
        cbStation.setSelectedIndex(0);
        cbCrime.setSelectedIndex(0);
    }
}