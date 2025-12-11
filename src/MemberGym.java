import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import java.awt.Color;

public class MemberGym extends JPanel {
    public MemberGym(JFrame parentFrame) {
        setLayout(null);
        this.setBackground(new Color(250, 250, 250));

        // Label Nama
        JLabel lblNama = new JLabel("Nama Member:");
        lblNama.setBounds(20, 20, 120, 25);
        add(lblNama);

        // Input Nama
        JTextField txtNama = new JTextField();
        txtNama.setBounds(150, 20, 230, 25);
        add(txtNama);

        // Label Usia
        JLabel lblUsia = new JLabel("Usia:");
        lblUsia.setBounds(20, 60, 120, 25);
        add(lblUsia);

        // Input Usia
        JTextField txtUsia = new JTextField();
        txtUsia.setBounds(150, 60, 230, 25);
        add(txtUsia);

        // Label Alamat
        JLabel lblAlamat = new JLabel("Alamat:");
        lblAlamat.setBounds(20, 100, 120, 25);
        add(lblAlamat);

        // Input Alamat
        JTextField txtAlamat = new JTextField();
        txtAlamat.setBounds(150, 100, 230, 25);
        add(txtAlamat);

        // Label No Telp
        JLabel lblTelp = new JLabel("No. Telepon:");
        lblTelp.setBounds(20, 140, 120, 25);
        add(lblTelp);

        // Input No Telp
        JTextField txtTelp = new JTextField();
        txtTelp.setBounds(150, 140, 230, 25);
        add(txtTelp);

        // Tombol Simpan
        JButton btnSimpan = new JButton("Daftar Member");
        btnSimpan.setBounds(20, 190, 150, 30);
        btnSimpan.setBackground(new Color(76, 175, 80));
        btnSimpan.setForeground(Color.WHITE); 
        add(btnSimpan);

        // Tombol Reset
        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(180, 190, 100, 30);
        btnReset.setBackground(new Color(255, 193, 7));
        btnReset.setForeground(Color.WHITE); 
        add(btnReset);

        // Tombol Hapus
        JButton btnHapus = new JButton("Hapus");
        btnHapus.setBounds(290, 190, 100, 30);
        btnHapus.setBackground(new Color(244, 67, 54));
        btnHapus.setForeground(Color.WHITE);
        add(btnHapus);

        // Table Model dan JTable
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable tableMember = new JTable(tableModel);
        JScrollPane scrollTable = new JScrollPane(tableMember);
        scrollTable.setBounds(20, 240, 600, 170);
        add(scrollTable);

        // Set kolom tabel
        tableModel.addColumn("ID");
        tableModel.addColumn("Nama");
        tableModel.addColumn("Usia");
        tableModel.addColumn("Alamat");
        tableModel.addColumn("No. Telepon");

        // Fungsi untuk load data dari database ke tabel
        Runnable loadMember = () -> {
            try {
                tableModel.setRowCount(0);
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_gym", "root", "");
                String sql = "SELECT * FROM member_gym";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    tableModel.addRow(new Object[] {
                            rs.getInt("id_member"),
                            rs.getString("nama"),
                            rs.getInt("usia"),
                            rs.getString("alamat"),
                            rs.getString("no_telepon")
                    });
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame,
                        "Gagal mengambil data!\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        // Initial load
        loadMember.run();

        // EVENT SIMPAN KE MYSQL
        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nama = txtNama.getText().trim();
                String usiaStr = txtUsia.getText().trim();
                String alamat = txtAlamat.getText().trim();
                String telepon = txtTelp.getText().trim();

                // Validasi input kosong
                if (nama.isEmpty() || usiaStr.isEmpty() || alamat.isEmpty() || telepon.isEmpty()) {
                    JOptionPane.showMessageDialog(parentFrame, "Semua field wajib diisi!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Validasi usia harus angka positif
                int usia;
                try {
                    usia = Integer.parseInt(usiaStr);
                    if (usia <= 0)
                        throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(parentFrame, "Usia harus berupa angka & positif!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    Connection conn = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/db_gym",
                            "root",
                            "");
                    String sql = "INSERT INTO member_gym (nama, usia, alamat, no_telepon) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, nama);
                    stmt.setInt(2, usia);
                    stmt.setString(3, alamat);
                    stmt.setString(4, telepon);
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();

                    JOptionPane.showMessageDialog(parentFrame,
                            "Member gym berhasil didaftarkan!",
                            "Sukses", JOptionPane.INFORMATION_MESSAGE);

                    loadMember.run();
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Gagal menyimpan ke database!\n" + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // EVENT RESET FORM
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtNama.setText("");
                txtUsia.setText("");
                txtAlamat.setText("");
                txtTelp.setText("");
            }
        });

        // EVENT HAPUS DATA
        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableMember.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(parentFrame, "Pilih satu baris data yang ingin dihapus!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(parentFrame, "Yakin ingin menghapus member ini?",
                        "Hapus Member", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION)
                    return;

                int idMember = (int) tableModel.getValueAt(selectedRow, 0);

                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_gym", "root", "");
                    String sql = "DELETE FROM member_gym WHERE id_member=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, idMember);
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();

                    JOptionPane.showMessageDialog(parentFrame, "Data berhasil dihapus!", "Sukses",
                            JOptionPane.INFORMATION_MESSAGE);

                    loadMember.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parentFrame, "Gagal menghapus data!\n" + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}