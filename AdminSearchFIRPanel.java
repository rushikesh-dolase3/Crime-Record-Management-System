import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminSearchFIRPanel extends JPanel {

    private JTextField txtSearch;
    private JTable table;
    private DefaultTableModel model;

    public AdminSearchFIRPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(236,240,245));

        // ===== TITLE =====
        JLabel title = new JLabel("Search FIR (Admin)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20,20,10,10));
        add(title, BorderLayout.NORTH);

        // ===== SEARCH PANEL =====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,15,15));
        searchPanel.setBackground(Color.WHITE);

        searchPanel.add(new JLabel("Enter Applicant Name:"));

        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);

        JButton btnSearch = new JButton("Search");
        btnSearch.setBackground(new Color(0,123,255));
        btnSearch.setForeground(Color.WHITE);
        searchPanel.add(btnSearch);

        add(searchPanel, BorderLayout.CENTER);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new String[]{"FIR ID","Applicant Name","Mobile","Date","Status"},0
        );

        table = new JTable(model);
        table.setRowHeight(25);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));

        add(sp, BorderLayout.SOUTH);

        // ===== ACTION =====
        btnSearch.addActionListener(e -> searchFIR());
    }

    private void searchFIR() {

        model.setRowCount(0);

        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres","1234"
            );

            String sql = """
                SELECT fir_id, applicant_name, contact_number, created_at, status
                FROM fir
                WHERE LOWER(applicant_name) LIKE LOWER(?)
                ORDER BY fir_id DESC
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + txtSearch.getText().trim() + "%");

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("fir_id"),
                        rs.getString("applicant_name"),
                        rs.getString("contact_number"),
                        rs.getTimestamp("created_at"),
                        rs.getString("status")
                });
            }

            if(model.getRowCount()==0){
                JOptionPane.showMessageDialog(this,"No FIR Found");
            }

            con.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }
}