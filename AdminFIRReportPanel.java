import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminFIRReportPanel extends JPanel {

    private JSpinner fromDateSpinner, toDateSpinner;
    private JTable table;
    private DefaultTableModel model;

    public AdminFIRReportPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(236,240,245));

        JLabel title = new JLabel("FIR Report Between Dates (Admin)");
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
        btnGenerate.setBackground(new Color(0,123,255));
        btnGenerate.setForeground(Color.WHITE);
        filterPanel.add(btnGenerate);

        add(filterPanel, BorderLayout.CENTER);

        model = new DefaultTableModel(new String[]{
                "FIR ID", "Applicant Name", "Mobile", "FIR Date", "Status"
        },0);

        table = new JTable(model);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));
        add(scrollPane, BorderLayout.SOUTH);

        btnGenerate.addActionListener(e -> loadReport());
    }

    private void loadReport() {

        model.setRowCount(0);

        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres","1234"
            );

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            String from = sdf.format((Date) fromDateSpinner.getValue());
            String to = sdf.format((Date) toDateSpinner.getValue());

            String sql =
                    "SELECT fir_id, applicant_name, contact_number, created_at, status " +
                            "FROM fir " +
                            "WHERE DATE(created_at) BETWEEN ? AND ? " +
                            "ORDER BY fir_id DESC";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDate(1, java.sql.Date.valueOf(from));
            ps.setDate(2, java.sql.Date.valueOf(to));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("fir_id"),
                        rs.getString("applicant_name"),
                        rs.getString("contact_number"),
                        rs.getTimestamp("created_at"),
                        rs.getString("status")
                });
            }

            con.close();

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "No FIR records found between selected dates.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}