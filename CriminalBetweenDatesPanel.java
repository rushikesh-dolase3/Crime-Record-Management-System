import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Date;

public class CriminalBetweenDatesPanel extends JPanel {

    private JSpinner fromDateSpinner, toDateSpinner;
    private JTable table;
    private DefaultTableModel model;
    private Connection con;

    public CriminalBetweenDatesPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(236,240,245));

        JLabel title = new JLabel("Criminal Report Between Dates");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20,20,10,10));
        add(title, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));

        filterPanel.add(new JLabel("From Date:"));
        fromDateSpinner = new JSpinner(new SpinnerDateModel());
        fromDateSpinner.setEditor(new JSpinner.DateEditor(fromDateSpinner, "yyyy-MM-dd"));
        filterPanel.add(fromDateSpinner);

        filterPanel.add(new JLabel("To Date:"));
        toDateSpinner = new JSpinner(new SpinnerDateModel());
        toDateSpinner.setEditor(new JSpinner.DateEditor(toDateSpinner, "yyyy-MM-dd"));
        filterPanel.add(toDateSpinner);

        JButton btnGenerate = new JButton("Generate Report");
        btnGenerate.setBackground(new Color(13,110,253));
        btnGenerate.setForeground(Color.WHITE);
        filterPanel.add(btnGenerate);

        add(filterPanel, BorderLayout.CENTER);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "Criminal ID", "Name", "Crime Name", "Crime Date", "City"
        });

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));
        add(scrollPane, BorderLayout.SOUTH);

        connectDB();

        btnGenerate.addActionListener(e -> loadReport());
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

    private void loadReport() {

        model.setRowCount(0);

        try {

            java.util.Date fromUtil = (Date) fromDateSpinner.getValue();
            java.util.Date toUtil = (Date) toDateSpinner.getValue();

            java.sql.Date fromDate = new java.sql.Date(fromUtil.getTime());
            java.sql.Date toDate = new java.sql.Date(toUtil.getTime());

            String sql = """
                    SELECT c.criminal_id,
                           c.name,
                           cr.crime_name,
                           c.crime_date,
                           c.city
                    FROM criminal c
                    JOIN crime cr
                        ON c.crime_id = cr.crime_id
                    WHERE c.crime_date BETWEEN ? AND ?
                    AND c.station_id = ?
                    ORDER BY c.crime_date DESC
                    """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDate(1, fromDate);
            ps.setDate(2, toDate);
            ps.setInt(3, PoliceSession.policeStationId); // ✅ SAME as Manage panel

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("criminal_id"),
                        rs.getString("name"),
                        rs.getString("crime_name"),
                        rs.getDate("crime_date"),
                        rs.getString("city")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "No records found for this station between selected dates.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}