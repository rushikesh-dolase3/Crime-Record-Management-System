import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminCriminalDetailsPanel extends JPanel {

    public AdminCriminalDetailsPanel(int criminalId) {

        setLayout(new BorderLayout());
        setBackground(new Color(236,240,245));

        JLabel title = new JLabel("Criminal Full Details");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20,20,10,10));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0,2,15,15));
        form.setBorder(BorderFactory.createEmptyBorder(20,40,20,40));
        form.setBackground(Color.WHITE);

        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres","1234"
            );

            String sql = """
                SELECT c.*, 
                       ps.station_name,
                       ct.crime_name,
                       pr.prison_name,
                       co.court_name
                FROM criminal c
                LEFT JOIN police_station ps 
                    ON c.station_id = ps.station_id
                LEFT JOIN crime_type ct 
                    ON c.crime_id = ct.crime_id
                LEFT JOIN prison pr 
                    ON c.prison_id = pr.prison_id
                LEFT JOIN court co 
                    ON c.court_id = co.court_id
                WHERE c.criminal_id = ?
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, criminalId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                addField(form,"Police Station", rs.getString("station_name"));
                addField(form,"Crime Type", rs.getString("crime_name"));
                addField(form,"Crime Date", rs.getDate("crime_date")+"");
                addField(form,"Crime Time", rs.getTime("crime_time")+"");
                addField(form,"Prison", rs.getString("prison_name"));
                addField(form,"Court", rs.getString("court_name"));

                addField(form,"Name", rs.getString("name"));
                addField(form,"Contact", rs.getString("contact_number"));
                addField(form,"Height", rs.getString("height"));
                addField(form,"Weight", rs.getString("weight"));
                addField(form,"DOB", rs.getDate("dob")+"");
                addField(form,"Email", rs.getString("email"));

                addField(form,"Address", rs.getString("address"));
                addField(form,"City", rs.getString("city"));
                addField(form,"State", rs.getString("state"));
                addField(form,"Country", rs.getString("country"));
                addField(form,"Zipcode", rs.getString("zipcode"));

                // PHOTO
                String path = rs.getString("photo_path");
                if(path != null && !path.isEmpty()) {
                    Image img = new ImageIcon(path)
                            .getImage()
                            .getScaledInstance(150,180,Image.SCALE_SMOOTH);

                    JLabel photoLabel = new JLabel(new ImageIcon(img));
                    photoLabel.setBorder(BorderFactory.createTitledBorder("Photo"));

                    JPanel photoPanel = new JPanel();
                    photoPanel.setBackground(Color.WHITE);
                    photoPanel.add(photoLabel);

                    add(photoPanel, BorderLayout.EAST);
                }
            }

            con.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }

        add(form, BorderLayout.CENTER);
    }

    private void addField(JPanel panel, String label, String value) {
        panel.add(new JLabel(label + ":"));
        JTextField tf = new JTextField(value);
        tf.setEditable(false);
        panel.add(tf);
    }
}