import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TakeActionDialog extends JDialog {

    JTextArea txtRemark;
    JComboBox<String> cbStatus;
    int firId;
    PoliceViewFIRDetailsPanel parent;
    Connection con;

    public TakeActionDialog(PoliceViewFIRDetailsPanel parent, int firId) {

        this.parent = parent;
        this.firId = firId;

        setTitle("Take Action");
        setSize(450,300);
        setLocationRelativeTo(parent);
        setModal(true);
        setLayout(new BorderLayout());
        connectDB();

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8,8,8,8);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtRemark = new JTextArea(5,20);
        txtRemark.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        cbStatus = new JComboBox<>(new String[]{
                "Approved",
                "Cancelled"
        });

        g.gridx=0; g.gridy=0;
        panel.add(new JLabel("Remark :"),g);
        g.gridx=1;
        panel.add(new JScrollPane(txtRemark),g);

        g.gridx=0; g.gridy=1;
        panel.add(new JLabel("Status :"),g);
        g.gridx=1;
        panel.add(cbStatus,g);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBackground(new Color(13,110,253));
        btnUpdate.setForeground(Color.WHITE);

        g.gridx=1; g.gridy=2;
        panel.add(btnUpdate,g);

        add(panel,BorderLayout.CENTER);

        btnUpdate.addActionListener(e -> updateStatus());
    }

    private void connectDB() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres","1234"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    private void updateStatus() {

        try {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE fir SET status=?, police_remark=?, police_remark_date=now() WHERE fir_id=?"
            );

            ps.setString(1,cbStatus.getSelectedItem().toString());
            ps.setString(2,txtRemark.getText());
            ps.setInt(3,firId);

            ps.executeUpdate();

            parent.setStatus(cbStatus.getSelectedItem().toString());

            JOptionPane.showMessageDialog(this,"Status Updated Successfully ✅");
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }
}