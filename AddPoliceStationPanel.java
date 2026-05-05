import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddPoliceStationPanel extends JPanel {

    JTextField txtName, txtCode;
    JButton btnAdd;

    Connection con;

    public AddPoliceStationPanel() {

        setLayout(new GridBagLayout());
        setBackground(new Color(35, 50, 69));

        connectDB();

        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(600, 250));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Add Police Station Detail"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblName = new JLabel("Police Station Name *");
        JLabel lblCode = new JLabel("Police Station Code *");

        txtName = new JTextField(20);
        txtCode = new JTextField(20);

        btnAdd = new JButton("Add");
        btnAdd.setBackground(new Color(13, 110, 253));
        btnAdd.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 0;
        card.add(lblName, gbc);

        gbc.gridx = 1;
        card.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        card.add(lblCode, gbc);

        gbc.gridx = 1;
        card.add(txtCode, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        card.add(btnAdd, gbc);

        add(card);

        btnAdd.addActionListener(e -> addStation());
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

    private void addStation() {
        try {
            String sql = "INSERT INTO police_station (station_name, station_code) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, txtName.getText());
            ps.setString(2, txtCode.getText());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Police Station Added Successfully!");
            txtName.setText("");
            txtCode.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}