import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import java.awt.Color;

public class Instruktur extends JPanel {
    public Instruktur(JFrame parentFrame) {
        setLayout(null);
        this.setBackground(new Color(250, 250, 250));

        // Label Nama
        JLabel lblNama = new JLabel("Nama Instruktur:");
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

        // Label Keahlian
        JLabel lblKeahlian = new JLabel("Keahlian:");
        lblKeahlian.setBounds(20, 100, 120, 25);
        add(lblKeahlian);

        // Input Keahlian
        JTextField txtKeahlian = new JTextField();
        txtKeahlian.setBounds(150, 100, 230, 25);
        add(txtKeahlian);

        // Label No Telp
        JLabel lblTelp = new JLabel("No. Telepon:");
        lblTelp.setBounds(20, 140, 120, 25);
        add(lblTelp);

        // Input No Telp
        JTextField txtTelp = new JTextField();
        txtTelp.setBounds(150, 140, 230, 25);
        add(txtTelp);

        // Tombol Simpan
        JButton btnSimpan = new JButton("Daftarkan Instruktur");
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

        // Tombol Update
        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(290, 190, 100, 30);
        btnUpdate.setBackground(new Color(102, 178, 255));
        btnUpdate.setForeground(Color.WHITE); 
        add(btnUpdate);

        // Tombol Hapus
        JButton btnHapus = new JButton("Hapus");
        btnHapus.setBounds(400, 190, 100, 30);
        btnHapus.setBackground(new Color(244, 67, 54));
        btnHapus.setForeground(Color.WHITE);
        add(btnHapus);

        // Table Model dan JTable
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable tableInstruktur = new JTable(tableModel);
        JScrollPane scrollTable = new JScrollPane(tableInstruktur);
        scrollTable.setBounds(20, 240, 600, 170);
        add(scrollTable);

        // Set kolom tabel
        tableModel.addColumn("ID");
        tableModel.addColumn("Nama");
        tableModel.addColumn("Usia");
        tableModel.addColumn("Keahlian");
        tableModel.addColumn("No. Telepon");

        // Fungsi untuk load data dari database ke tabel
        Runnable loadInstruktur = () -> {
            try {
                tableModel.setRowCount(0);
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_gym", "root", "");
                String sql = "SELECT * FROM instruktur_gym";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    tableModel.addRow(new Object[] {
                            rs.getInt("id_instruktur"),
                            rs.getString("nama"),
                            rs.getInt("usia"),
                            rs.getString("keahlian"),
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
        loadInstruktur.run();

        // TABLE CLICK (AUTO LOAD KE INPUT FIELD)
        tableInstruktur.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = tableInstruktur.getSelectedRow();
                if (i != -1) {
                    txtNama.setText(tableModel.getValueAt(i, 1).toString());
                    txtUsia.setText(tableModel.getValueAt(i, 2).toString());
                    txtKeahlian.setText(tableModel.getValueAt(i, 3).toString());
                    txtTelp.setText(tableModel.getValueAt(i, 4).toString());
                }
            }
        });

        // EVENT SIMPAN KE MYSQL
        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nama = txtNama.getText().trim();
                String usiaStr = txtUsia.getText().trim();
                String keahlian = txtKeahlian.getText().trim();
                String telepon = txtTelp.getText().trim();

                // Validasi input kosong
                if (nama.isEmpty() || usiaStr.isEmpty() || keahlian.isEmpty() || telepon.isEmpty()) {
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
                    String sql = "INSERT INTO instruktur_gym (nama, usia, keahlian, no_telepon) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, nama);
                    stmt.setInt(2, usia);
                    stmt.setString(3, keahlian);
                    stmt.setString(4, telepon);
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();

                    JOptionPane.showMessageDialog(parentFrame,
                            "Instruktur gym berhasil didaftarkan!",
                            "Sukses", JOptionPane.INFORMATION_MESSAGE);

                    loadInstruktur.run();
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
                txtKeahlian.setText("");
                txtTelp.setText("");
            }
        });

        // ===== EVENT UPDATE (FITUR BARU) =====
        btnUpdate.addActionListener(e -> {
            int row = tableInstruktur.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(parentFrame, "Pilih data yang mau diupdate!");
                return;
            }

            int id = (int) tableModel.getValueAt(row, 0);

            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_gym", "root", "");
                String sql = "UPDATE instruktur_gym SET nama=?, usia=?, keahlian=?, no_telepon=? WHERE id_instruktur=?";
                PreparedStatement stmt = conn.prepareStatement(sql);

                stmt.setString(1, txtNama.getText());
                stmt.setInt(2, Integer.parseInt(txtUsia.getText()));
                stmt.setString(3, txtKeahlian.getText());
                stmt.setString(4, txtTelp.getText());
                stmt.setInt(5, id);

                stmt.executeUpdate();
                stmt.close();
                conn.close();

                JOptionPane.showMessageDialog(parentFrame, "Data berhasil diupdate!");
                loadInstruktur.run();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Gagal update!\n" + ex.getMessage());
            }
        });

        // EVENT HAPUS DATA
        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableInstruktur.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(parentFrame, "Pilih satu baris data yang ingin dihapus!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(parentFrame, "Yakin ingin menghapus Instruktur ini?",
                        "Hapus Instruktur", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION)
                    return;

                int idMember = (int) tableModel.getValueAt(selectedRow, 0);

                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_gym", "root", "");
                    String sql = "DELETE FROM instruktur_gym WHERE id_instruktur=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, idMember);
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();

                    JOptionPane.showMessageDialog(parentFrame, "Data berhasil dihapus!", "Sukses",
                            JOptionPane.INFORMATION_MESSAGE);

                    loadInstruktur.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parentFrame, "Gagal menghapus data!\n" + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

    }
}
