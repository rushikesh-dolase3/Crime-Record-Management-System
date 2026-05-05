import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import java.io.File;
import com.toedter.calendar.JDateChooser;

public class AddCriminalPanel extends JPanel {

    Connection con;

    JLabel lblPhotoPreview;
    String selectedPhotoPath = null;

    JComboBox<String> cbStation, cbCrime, cbPrison, cbCourt;
    JDateChooser dcCrimeDate, dcDOB;
    JTextField txtCrimeTime;

    JTextField txtName, txtContact, txtHeight, txtWeight,
            txtEmail, txtCity, txtState, txtCountry, txtZip;
    JTextArea txtAddress;

    public AddCriminalPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(236,240,245));

        JLabel title = new JLabel("Add Criminal Detail");
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
        g.anchor = GridBagConstraints.NORTHWEST;

        int y = 0;

        // ===== CASE DETAILS =====
        addSection(card,"Case Details",g,y++);

        cbStation = new JComboBox<>();
        cbCrime = new JComboBox<>();
        cbPrison = new JComboBox<>();
        cbCourt = new JComboBox<>();

        dcCrimeDate = new JDateChooser();
        dcCrimeDate.setDateFormatString("dd-MM-yyyy");

        txtCrimeTime = new JTextField("10:30 AM");

        addRow(card,g,y++,"Police Station*",cbStation,"Crime Type*",cbCrime);
        addRow(card,g,y++,"Crime Date*",dcCrimeDate,"Crime Time*",txtCrimeTime);
        addRow(card,g,y++,"Select Prison*",cbPrison,"Select Court*",cbCourt);

        // ===== CRIMINAL DETAILS =====
        addSection(card,"Criminal's Detail",g,y++);

        txtName = new JTextField();
        txtContact = new JTextField();
        txtHeight = new JTextField();
        txtWeight = new JTextField();
        dcDOB = new JDateChooser();
        dcDOB.setDateFormatString("dd-MM-yyyy");
        txtEmail = new JTextField();

        txtAddress = new JTextArea(3,20);
        txtCity = new JTextField();
        txtState = new JTextField();
        txtCountry = new JTextField();
        txtZip = new JTextField();

        addRow(card,g,y++,"Name*",txtName,"Contact*",txtContact);
        addRow(card,g,y++,"Height*",txtHeight,"Weight*",txtWeight);
        addRow(card,g,y++,"DOB*",dcDOB,"Email",txtEmail);

        g.gridx=0; g.gridy=y;
        card.add(label("Address*"),g);
        g.gridx=1; g.gridwidth=3;
        card.add(new JScrollPane(txtAddress),g);
        g.gridwidth=1;
        y++;

        addRow(card,g,y++,"City*",txtCity,"State*",txtState);
        addRow(card,g,y++,"Country*",txtCountry,"Zip*",txtZip);

        // ===== PHOTO =====
        addSection(card,"Criminal Photo",g,y++);

        JButton btnChoosePhoto = new JButton("Choose Photo");
        btnChoosePhoto.setPreferredSize(new Dimension(160,40));
        btnChoosePhoto.setMinimumSize(new Dimension(160,40));
        btnChoosePhoto.setMaximumSize(new Dimension(160,40));
        btnChoosePhoto.setFocusPainted(false);
        btnChoosePhoto.setContentAreaFilled(true);
        btnChoosePhoto.setBorder(new LineBorder(Color.GRAY));

        lblPhotoPreview = new JLabel();
        lblPhotoPreview.setPreferredSize(new Dimension(120,120));
        lblPhotoPreview.setBorder(new LineBorder(Color.GRAY));

        g.gridx=1; g.gridy=y;
        card.add(btnChoosePhoto,g);

        g.gridx=2; g.gridwidth=2;
        card.add(lblPhotoPreview,g);
        g.gridwidth=1;
        y++;

        JButton btnSave = new JButton("Save Criminal");
        btnSave.setBackground(new Color(13,110,253));
        btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new Dimension(160,40));

        g.gridx=1; g.gridy=y;
        card.add(btnSave,g);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(236,240,245));
        center.setBorder(new EmptyBorder(10,20,10,20));
        center.add(card);

        JScrollPane scroll = new JScrollPane(center);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);

        // ===== DB =====
        connectDB();
        loadDropdowns();

        btnChoosePhoto.addActionListener(e -> choosePhoto());
        btnSave.addActionListener(e -> saveCriminal());
    }

    // ================= PHOTO =================
    void choosePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image Files", "jpg", "jpeg", "png"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            selectedPhotoPath = file.getAbsolutePath();

            ImageIcon icon = new ImageIcon(
                    new ImageIcon(selectedPhotoPath)
                            .getImage().getScaledInstance(120,120,Image.SCALE_SMOOTH));
            lblPhotoPreview.setIcon(icon);
        }
    }

    // ================= DB =================
    void connectDB() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/crime_db",
                    "postgres","1234");
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
                cbStation.setEnabled(false); // 🔒 lock dropdown
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    void loadCombo(JComboBox<String> cb,String sql) {
        try {
            ResultSet rs = con.prepareStatement(sql).executeQuery();
            while(rs.next()) cb.addItem(rs.getString(1));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    // ===== FOREIGN KEY SAFE FETCH =====
    int getIdByName(String table,String idCol,String nameCol,String value) throws Exception {
        PreparedStatement ps = con.prepareStatement(
                "SELECT "+idCol+" FROM "+table+" WHERE "+nameCol+"=?");
        ps.setString(1,value);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) return rs.getInt(1);
        throw new Exception("Invalid selection in "+table);
    }

    // ================= SAVE =================
    void saveCriminal() {
        try {
            String sql = """
            INSERT INTO criminal
            (station_id, crime_id, prison_id, court_id,
             crime_date, crime_time,
             name, contact_number, height, weight, dob, email,
             address, city, state, country, zipcode, photo_path)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, PoliceSession.policeStationId);
            ps.setInt(2, getIdByName("crime","crime_id","crime_name",
                    cbCrime.getSelectedItem().toString()));
            ps.setInt(3, getIdByName("prison","prison_id","prison_name",
                    cbPrison.getSelectedItem().toString()));
            ps.setInt(4, getIdByName("court","court_id","court_name",
                    cbCourt.getSelectedItem().toString()));

            ps.setDate(5, new java.sql.Date(dcCrimeDate.getDate().getTime()));
            ps.setTime(6, java.sql.Time.valueOf(convertTime(txtCrimeTime.getText())));

            ps.setString(7, txtName.getText());
            ps.setString(8, txtContact.getText());
            ps.setString(9, txtHeight.getText());
            ps.setString(10, txtWeight.getText());
            ps.setDate(11, new java.sql.Date(dcDOB.getDate().getTime()));
            ps.setString(12, txtEmail.getText());
            ps.setString(13, txtAddress.getText());
            ps.setString(14, txtCity.getText());
            ps.setString(15, txtState.getText());
            ps.setString(16, txtCountry.getText());
            ps.setString(17, txtZip.getText());
            ps.setString(18, selectedPhotoPath);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"Criminal Added Successfully ✅");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    String convertTime(String t) throws Exception {
        java.text.SimpleDateFormat f12 = new java.text.SimpleDateFormat("hh:mm a");
        java.text.SimpleDateFormat f24 = new java.text.SimpleDateFormat("HH:mm:ss");
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