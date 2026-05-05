import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.io.File;

public class EditCriminalPanel extends JPanel {

    int criminalId;
    Connection con;

    JComboBox<String> cbStation, cbCrime, cbPrison, cbCourt;
    JTextField txtCrimeDate, txtCrimeTime;

    JTextField txtName, txtContact, txtHeight, txtWeight,
            txtDOB, txtEmail, txtCity, txtState, txtCountry, txtZip;
    JTextArea txtAddress;

    JLabel lblPhotoPreview;
    String selectedPhotoPath = null;

    public EditCriminalPanel(int criminalId) {

        this.criminalId = criminalId;

        setLayout(new BorderLayout());
        setBackground(new Color(236,240,245));

        JLabel title = new JLabel("Edit Criminal Detail");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(15,20,15,20));
        add(title, BorderLayout.NORTH);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new EmptyBorder(20,20,20,20),
                new LineBorder(new Color(220,220,220))
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8,8,8,8);
        g.fill = GridBagConstraints.HORIZONTAL;
        int y = 0;

        addSection(card,"Case Details",g,y++);

        cbStation = new JComboBox<>();
        cbCrime = new JComboBox<>();
        cbPrison = new JComboBox<>();
        cbCourt = new JComboBox<>();

        txtCrimeDate = new JTextField();
        txtCrimeTime = new JTextField();

        addRow(card,g,y++,"Police Station*",cbStation,"Crime Type*",cbCrime);
        addRow(card,g,y++,"Crime Date*",txtCrimeDate,"Crime Time*",txtCrimeTime);
        addRow(card,g,y++,"Select Prison*",cbPrison,"Select Court*",cbCourt);

        addSection(card,"Criminal's Detail",g,y++);

        txtName = new JTextField();
        txtContact = new JTextField();
        txtHeight = new JTextField();
        txtWeight = new JTextField();
        txtDOB = new JTextField();
        txtEmail = new JTextField();
        txtCity = new JTextField();
        txtState = new JTextField();
        txtCountry = new JTextField();
        txtZip = new JTextField();
        txtAddress = new JTextArea(3,20);

        addRow(card,g,y++,"Name*",txtName,"Contact*",txtContact);
        addRow(card,g,y++,"Height*",txtHeight,"Weight*",txtWeight);
        addRow(card,g,y++,"DOB*",txtDOB,"Email",txtEmail);

        g.gridx=0; g.gridy=y;
        card.add(label("Address*"),g);
        g.gridx=1; g.gridwidth=3;
        card.add(new JScrollPane(txtAddress),g);
        g.gridwidth=1;
        y++;

        addRow(card,g,y++,"City*",txtCity,"State*",txtState);
        addRow(card,g,y++,"Country*",txtCountry,"Zip*",txtZip);

        // PHOTO
        addSection(card,"Criminal Photo",g,y++);
        JButton btnChoosePhoto = new JButton("Choose Photo");
        lblPhotoPreview = new JLabel();
        lblPhotoPreview.setPreferredSize(new Dimension(120,120));
        lblPhotoPreview.setBorder(new LineBorder(Color.GRAY));

        g.gridx=1; g.gridy=y;
        card.add(btnChoosePhoto,g);
        g.gridx=2; g.gridwidth=2;
        card.add(lblPhotoPreview,g);
        g.gridwidth=1;
        y++;

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBackground(new Color(13,110,253));
        btnUpdate.setForeground(Color.WHITE);

        g.gridx=1; g.gridy=y;
        card.add(btnUpdate,g);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(new EmptyBorder(10,20,10,20));
        center.setBackground(new Color(236,240,245));
        center.add(card);

        JScrollPane scroll = new JScrollPane(center);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);


        connectDB();
        loadDropdowns();
        loadCriminalData();

        btnChoosePhoto.addActionListener(e -> choosePhoto());
        btnUpdate.addActionListener(e -> updateCriminal());
    }

    // ================= PHOTO =================
    void choosePhoto() {
        JFileChooser fc = new JFileChooser();
        if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
            File f = fc.getSelectedFile();
            selectedPhotoPath = f.getAbsolutePath();
            lblPhotoPreview.setIcon(new ImageIcon(
                    new ImageIcon(selectedPhotoPath)
                            .getImage().getScaledInstance(120,120,Image.SCALE_SMOOTH)
            ));
        }
    }

    // ================= DB =================
    void connectDB() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres","1234"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    void loadDropdowns() {
        loadLoggedInPoliceStation(); 
        loadCombo(cbCrime,"SELECT crime_name FROM crime_type");
        loadCombo(cbPrison,"SELECT prison_name FROM prison");
        loadCombo(cbCourt,"SELECT court_name FROM court");
    }

    void loadLoggedInPoliceStation() {
        try {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT station_name FROM police_station WHERE station_id=?"
            );
            ps.setInt(1, PoliceSession.policeStationId);

            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                cbStation.addItem(rs.getString("station_name"));
                cbStation.setSelectedIndex(0);
                cbStation.setEnabled(false);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    void loadCombo(JComboBox<String> cb, String sql) {
        try {
            ResultSet rs = con.prepareStatement(sql).executeQuery();
            while(rs.next()) cb.addItem(rs.getString(1));
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    // ================= PREFILL =================
    void loadCriminalData() {
        try {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM criminal WHERE criminal_id=?"
            );
            ps.setInt(1,criminalId);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {

                cbCrime.setSelectedIndex(rs.getInt("crime_id")-1);
                cbPrison.setSelectedIndex(rs.getInt("prison_id")-1);
                cbCourt.setSelectedIndex(rs.getInt("court_id")-1);

                txtCrimeDate.setText(rs.getDate("crime_date").toString());

                Time t = rs.getTime("crime_time");
                SimpleDateFormat f12 = new SimpleDateFormat("hh:mm a");
                txtCrimeTime.setText(f12.format(t));

                txtName.setText(rs.getString("name"));
                txtContact.setText(rs.getString("contact_number"));
                txtHeight.setText(rs.getString("height"));
                txtWeight.setText(rs.getString("weight"));
                txtDOB.setText(rs.getDate("dob").toString());
                txtEmail.setText(rs.getString("email"));
                txtAddress.setText(rs.getString("address"));
                txtCity.setText(rs.getString("city"));
                txtState.setText(rs.getString("state"));
                txtCountry.setText(rs.getString("country"));
                txtZip.setText(rs.getString("zipcode"));

                selectedPhotoPath = rs.getString("photo_path");
                if(selectedPhotoPath!=null){
                    lblPhotoPreview.setIcon(new ImageIcon(
                            new ImageIcon(selectedPhotoPath)
                                    .getImage().getScaledInstance(120,120,Image.SCALE_SMOOTH)
                    ));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    // ================= UPDATE =================
    void updateCriminal() {
        try {
            PreparedStatement ps = con.prepareStatement(
                    """
                    UPDATE criminal SET
                    crime_id=?, prison_id=?, court_id=?, crime_date=?, crime_time=?,
                    name=?, contact_number=?, height=?, weight=?, dob=?, email=?,
                    address=?, city=?, state=?, country=?, zipcode=?, photo_path=?
                    WHERE criminal_id=?
                    """
            );

            ps.setInt(1,cbCrime.getSelectedIndex()+1);
            ps.setInt(2,cbPrison.getSelectedIndex()+1);
            ps.setInt(3,cbCourt.getSelectedIndex()+1);
            ps.setDate(4,Date.valueOf(txtCrimeDate.getText()));
            ps.setTime(5,Time.valueOf(convertTime(txtCrimeTime.getText())));
            ps.setString(6,txtName.getText());
            ps.setString(7,txtContact.getText());
            ps.setString(8,txtHeight.getText());
            ps.setString(9,txtWeight.getText());
            ps.setDate(10,Date.valueOf(txtDOB.getText()));
            ps.setString(11,txtEmail.getText());
            ps.setString(12,txtAddress.getText());
            ps.setString(13,txtCity.getText());
            ps.setString(14,txtState.getText());
            ps.setString(15,txtCountry.getText());
            ps.setString(16,txtZip.getText());
            ps.setString(17,selectedPhotoPath);
            ps.setInt(18,criminalId);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"Criminal Updated Successfully ✅");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    String convertTime(String t) throws Exception {
        SimpleDateFormat f12 = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat f24 = new SimpleDateFormat("HH:mm:ss");
        return f24.format(f12.parse(t));
    }

    // ================= UI HELPERS =================
    void addSection(JPanel p,String t,GridBagConstraints g,int y){
        g.gridx=0; g.gridy=y; g.gridwidth=4;
        JLabel l=new JLabel(t);
        l.setFont(new Font("Segoe UI",Font.BOLD,14));
        l.setForeground(new Color(13,110,253));
        l.setBorder(new MatteBorder(0,0,1,0,new Color(220,220,220)));
        p.add(l,g);
        g.gridwidth=1;
    }

    void addRow(JPanel p,GridBagConstraints g,int y,String l1,Component c1,String l2,Component c2){
        g.gridx=0; g.gridy=y; p.add(label(l1),g);
        g.gridx=1; p.add(c1,g);
        g.gridx=2; p.add(label(l2),g);
        g.gridx=3; p.add(c2,g);
    }

    JLabel label(String t){
        JLabel l=new JLabel(t);
        l.setFont(new Font("Segoe UI",Font.BOLD,13));
        return l;
    }
}