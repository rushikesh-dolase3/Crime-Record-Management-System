import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class AdminViewCriminalPanel extends JPanel {

    JTable table;
    DefaultTableModel model;

    public AdminViewCriminalPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(236,240,245));

        JLabel title = new JLabel("All Criminals");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(15,20,15,20));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"ID","Name","Station","Crime Date","City","Action"},0) {

            public boolean isCellEditable(int row, int col){
                return col == 5;
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);

        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);

        loadCriminals();
    }

    private void loadCriminals(){
        try{
            Connection con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres","1234");

            PreparedStatement ps = con.prepareStatement(
                    "SELECT c.criminal_id, c.name, ps.station_name, c.crime_date, c.city " +
                            "FROM criminal c " +
                            "JOIN police_station ps ON c.station_id = ps.station_id " +
                            "ORDER BY c.criminal_id DESC");

            ResultSet rs = ps.executeQuery();
            model.setRowCount(0);

            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getInt("criminal_id"),
                        rs.getString("name"),
                        rs.getString("station_name"),
                        rs.getDate("crime_date"),
                        rs.getString("city"),
                        "View Details"
                });
            }

            con.close();

        }catch(Exception e){
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    // 🔹 Button Renderer
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("View Details");
            setForeground(Color.WHITE);
            setBackground(new Color(13,110,253));
        }

        public Component getTableCellRendererComponent(
                JTable table,Object value,boolean isSelected,
                boolean hasFocus,int row,int col){
            return this;
        }
    }

    // 🔹 Button Editor
    class ButtonEditor extends DefaultCellEditor {

        JButton button;
        int criminalId;

        public ButtonEditor(JCheckBox checkBox){
            super(checkBox);

            button = new JButton("View Details");
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(13,110,253));

            button.addActionListener(e -> {

                MainFrame frame =
                        (MainFrame) SwingUtilities.getWindowAncestor(AdminViewCriminalPanel.this);

                frame.showPage(new AdminCriminalDetailsPanel(criminalId));
                fireEditingStopped();
            });
        }

        public Component getTableCellEditorComponent(
                JTable table,Object value,
                boolean isSelected,int row,int column){

            criminalId = Integer.parseInt(table.getValueAt(row,0).toString());
            return button;
        }
    }
}