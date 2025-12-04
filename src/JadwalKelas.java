import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class JadwalKelas extends JPanel {
    public JadwalKelas(JFrame parentFrame) {
        setLayout(null);

        // Label Nama Kelas
        JLabel lblKelas = new JLabel("Nama Kelas:");
        lblKelas.setBounds(20, 20, 120, 25);
        add(lblKelas);

        // Input Nama Kelas
        JTextField txtKelas = new JTextField();
        txtKelas.setBounds(150, 20, 230, 25);
        add(txtKelas);

        // Label Hari
        JLabel lblHari = new JLabel("Hari:");
        lblHari.setBounds(20, 60, 120, 25);
        add(lblHari);

        // Combobox Hari
        String[] listhari = { "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu" };
        JComboBox<String> cmbHari = new JComboBox<>(listhari);
        cmbHari.setBounds(150, 60, 230, 25);
        add(cmbHari);

        // Label Jam Kelas
        JLabel lblJam = new JLabel("Jam Kelas:");
        lblJam.setBounds(20, 100, 120, 25);
        add(lblJam);

        // Input jam
        JTextField txtJam = new JTextField();
        txtJam.setBounds(150, 100, 230, 25);
        add(txtJam);

        // Label Instruktur
        JLabel lblInstruktur = new JLabel("Nama Instruktur:");
        lblInstruktur.setBounds(20, 140, 120, 25);
        add(lblInstruktur);

        // ComboBox Instruktur
        JComboBox<String> cmbInstruktur = new JComboBox<>();
        cmbInstruktur.setBounds(150, 140, 230, 25);
        add(cmbInstruktur);

        // Mengambil data Instruktur dari DB
        Runnable loadInstruktur = () -> {
            cmbInstruktur.removeAllItems();
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym", "root", "");
                String sql = "SELECT id_instruktur, nama FROM instruktur_gym";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    cmbInstruktur.addItem(rs.getInt("id_instruktur") + " - " + rs.getString("nama"));
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Gagal mengambil instruktur!\n" + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        };
        loadInstruktur.run();

        // Tombol Simpan
        JButton btnSimpan = new JButton("Tambah Kelas");
        btnSimpan.setBounds(20, 190, 150, 30);
        add(btnSimpan);

        // Tombol Reset
        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(180, 190, 100, 30);
        add(btnReset);

        // Tombol Hapus
        JButton btnHapus = new JButton("Hapus");
        btnHapus.setBounds(290, 190, 100, 30);
        add(btnHapus);

        // Tombol update
        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(400, 190, 100, 30);
        add(btnUpdate);

        // Table Model dan JTable
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable tableKelas = new JTable(tableModel);
        JScrollPane scrollTable = new JScrollPane(tableKelas);
        scrollTable.setBounds(20, 240, 600, 170);
        add(scrollTable);

        // Set kolom tabel
        tableModel.addColumn("ID Kelas");
        tableModel.addColumn("Nama Kelas");
        tableModel.addColumn("Hari");
        tableModel.addColumn("Jam Kelas");
        tableModel.addColumn("Nama Instruktur");

        // Fungsi untuk load data jadwal dari database
        Runnable loadKelas = () -> {
            try {
                tableModel.setRowCount(0);
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym", "root", "");
                String sql = "SELECT jc.id_kelas, jc.nama_kelas, jc.hari, jc.jam_kelas, ig.nama as instruktur " +
                        "FROM jadwal_kelas jc JOIN instruktur_gym ig ON jc.id_instruktur = ig.id_instruktur";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    tableModel.addRow(new Object[] {
                            rs.getInt("id_kelas"),
                            rs.getString("nama_kelas"),
                            rs.getString("hari"),
                            rs.getString("jam_kelas"),
                            rs.getString("instruktur")
                    });
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Gagal mengambil data jadwal!\n" + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        };
        // Initial load
        loadKelas.run();

        // Event simpan ke MYSQL
        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String namaKelas = txtKelas.getText().trim();
                String hari = (String) cmbHari.getSelectedItem();
                String jamKelas = txtJam.getText();
                String instrukturCombo = (String) cmbInstruktur.getSelectedItem();

                // Validasi input kosong
                if (namaKelas.isEmpty() || jamKelas.isEmpty() || instrukturCombo == null) {
                    JOptionPane.showMessageDialog(parentFrame, "Semua field wajib diisi!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Ambil id instruktur dari ComboBox
                int id_Instruktur;
                try {
                    id_Instruktur = Integer.parseInt(instrukturCombo.split(" - ")[0]);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parentFrame, "Instruktur tidak valid!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    Connection conn = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/gym",
                            "root",
                            "");
                    String sql = "INSERT INTO jadwal_kelas (nama_kelas, hari, jam_kelas, id_instruktur) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, namaKelas);
                    stmt.setString(2, hari);
                    stmt.setString(3, jamKelas);
                    stmt.setInt(4, id_Instruktur);
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();

                    JOptionPane.showMessageDialog(parentFrame, "Jadwal kelas gym berhasil ditambahkan!", "Sukses",
                            JOptionPane.INFORMATION_MESSAGE);

                    loadKelas.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parentFrame, "Gagal menyimpan ke database!\n" + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Event Reset Form
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtKelas.setText("");
                cmbHari.setSelectedIndex(0);
                txtJam.setText("");
                cmbInstruktur.setSelectedIndex(0);
            }
        });

        // EVENT HAPUS DATA
        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableKelas.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(parentFrame, "Pilih satu baris jadwal yang ingin dihapus!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(parentFrame, "Yakin ingin menghapus jadwal ini?",
                        "Hapus Jadwal", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION)
                    return;

                int idKelas = (int) tableModel.getValueAt(selectedRow, 0);

                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym", "root", "");
                    String sql = "DELETE FROM jadwal_kelas WHERE id_kelas=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, idKelas);
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();

                    JOptionPane.showMessageDialog(parentFrame, "Jadwal berhasil dihapus!", "Sukses",
                            JOptionPane.INFORMATION_MESSAGE);

                    loadKelas.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parentFrame, "Gagal menghapus data!\n" + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
