import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SearchCriminalPanel extends JPanel {

    private JTextField txtSearch;
    private JTable table;
    private DefaultTableModel model;

    public SearchCriminalPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(236,240,245));

        // ===== TITLE =====
        JLabel title = new JLabel("Search Criminal");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20,20,10,10));
        add(title, BorderLayout.NORTH);

        // ===== SEARCH PANEL =====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,15,15));
        searchPanel.setBackground(Color.WHITE);

        searchPanel.add(new JLabel("Enter Name:"));

        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);

        JButton btnSearch = new JButton("Search");
        btnSearch.setBackground(new Color(0,123,255));
        btnSearch.setForeground(Color.WHITE);
        searchPanel.add(btnSearch);

        add(searchPanel, BorderLayout.CENTER);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new String[]{"Criminal ID","Name","Mobile","City","Crime Date"},0
        );

        table = new JTable(model);
        table.setRowHeight(25);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));

        add(sp, BorderLayout.SOUTH);

        // ===== BUTTON ACTION =====
        btnSearch.addActionListener(e -> searchCriminal());
    }

    private void searchCriminal() {

        model.setRowCount(0);

        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres","1234"
            );

            String sql = """
                SELECT criminal_id, name, contact_number, city, crime_date
                FROM criminal
                WHERE station_id = ?
                AND LOWER(name) LIKE LOWER(?)
                ORDER BY criminal_id DESC
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, PoliceSession.policeStationId);
            ps.setString(2, "%" + txtSearch.getText().trim() + "%");

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("criminal_id"),
                        rs.getString("name"),
                        rs.getString("contact_number"),
                        rs.getString("city"),
                        rs.getDate("crime_date")
                });
            }

            if(model.getRowCount()==0){
                JOptionPane.showMessageDialog(this,"No Criminal Found");
            }

            con.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }
}