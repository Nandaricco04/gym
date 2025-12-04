import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class PendaftaranKelas extends JPanel {
    public PendaftaranKelas(JFrame parentFrame) {
        setLayout(null);

        // Label Member
        JLabel lblMember = new JLabel("Member Gym:");
        lblMember.setBounds(20, 20, 120, 25);
        add(lblMember);

        // ComboBox untuk memilih id_member, tampilkan nama-member dari member_gym
        JComboBox<String> cmbMember = new JComboBox<>();
        cmbMember.setBounds(150, 20, 230, 25);
        add(cmbMember);

        // Label Kelas
        JLabel lblKelas = new JLabel("Kelas:");
        lblKelas.setBounds(20, 60, 120, 25);
        add(lblKelas);

        // ComboBox untuk memilih id_kelas, tampilkan nama-kelas DAN nama instruktur dari jadwal_kelas + instruktur_gym
        JComboBox<String> cmbKelas = new JComboBox<>();
        cmbKelas.setBounds(150, 60, 230, 25);
        add(cmbKelas);

        // Label Tanggal Daftar
        JLabel lblTanggal = new JLabel("Tanggal Daftar:");
        lblTanggal.setBounds(20, 100, 120, 25);
        add(lblTanggal);

        JTextField txtTanggal = new JTextField();
        txtTanggal.setBounds(150, 100, 230, 25);
        txtTanggal.setText(java.time.LocalDate.now().toString());
        add(txtTanggal);

        // Label Catatan
        JLabel lblCatatan = new JLabel("Catatan:");
        lblCatatan.setBounds(20, 140, 120, 25);
        add(lblCatatan);

        JTextField txtCatatan = new JTextField();
        txtCatatan.setBounds(150, 140, 230, 25);
        add(txtCatatan);

        // Tombol Simpan
        JButton btnSimpan = new JButton("Daftarkan Kelas");
        btnSimpan.setBounds(20, 190, 150, 30);
        add(btnSimpan);

        // Tombol Reset
        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(180, 190, 100, 30);
        add(btnReset);

        // Tombol Update
        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(290, 190, 100, 30);
        add(btnUpdate);

        // Tombol Hapus
        JButton btnHapus = new JButton("Hapus");
        btnHapus.setBounds(400, 190, 100, 30);
        add(btnHapus);

        // Table
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable tablePendaftaran = new JTable(tableModel);
        JScrollPane scrollTable = new JScrollPane(tablePendaftaran);
        scrollTable.setBounds(20, 240, 600, 170);
        add(scrollTable);

        tableModel.addColumn("ID");
        tableModel.addColumn("Member");
        tableModel.addColumn("Kelas");
        tableModel.addColumn("Instruktur");
        tableModel.addColumn("Tanggal Daftar");
        tableModel.addColumn("Catatan");

        Runnable loadComboMember = () -> {
            try {
                cmbMember.removeAllItems();
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym", "root", "");
                String sql = "SELECT id_member, nama FROM member_gym";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    cmbMember.addItem(rs.getInt("id_member") + " - " + rs.getString("nama"));
                }
                rs.close(); stmt.close(); conn.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Gagal ambil member!\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        // ---- Load ComboBox KELAS ----
        Runnable loadComboKelas = () -> {
            try {
                cmbKelas.removeAllItems();
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym", "root", "");
                // ambil nama kelas & nama instruktur lewat JOIN
                String sql = "SELECT k.id_kelas, k.nama_kelas, i.nama as nama_instruktur " +
                             "FROM jadwal_kelas k " +
                             "JOIN instruktur_gym i ON k.id_instruktur=i.id_instruktur";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    // item format: id_kelas - nama_kelas (nama_instruktur)
                    cmbKelas.addItem(rs.getInt("id_kelas") + " - " +
                                     rs.getString("nama_kelas") + " (" +
                                     rs.getString("nama_instruktur") + ")");
                }
                rs.close(); stmt.close(); conn.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Gagal ambil jadwal kelas!\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        // ---- Load Table ----
        Runnable loadPendaftaranKelas = () -> {
            try {
                tableModel.setRowCount(0);
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym", "root", "");
                String sql = "SELECT pk.id_pendaftaran, m.nama as member_nama, k.nama_kelas, " +
                             "i.nama as instruktur_nama, pk.tanggal_daftar, pk.catatan " +
                             "FROM pendaftaran_kelas pk " +
                             "JOIN member_gym m ON pk.id_member=m.id_member " +
                             "JOIN jadwal_kelas k ON pk.id_kelas=k.id_kelas " +
                             "JOIN instruktur_gym i ON k.id_instruktur=i.id_instruktur";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    tableModel.addRow(new Object[] {
                            rs.getInt("id_pendaftaran"),
                            rs.getString("member_nama"),
                            rs.getString("nama_kelas"),
                            rs.getString("instruktur_nama"),
                            rs.getString("tanggal_daftar"),
                            rs.getString("catatan")
                    });
                }
                rs.close(); stmt.close(); conn.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame,
                        "Gagal mengambil data!\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        loadComboMember.run();
        loadComboKelas.run();
        loadPendaftaranKelas.run();

        tablePendaftaran.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = tablePendaftaran.getSelectedRow();
                if (i != -1) {
                    cmbMember.setSelectedItem(tableModel.getValueAt(i, 1).toString());
                    // kelas         : "id_kelas - nama_kelas (nama_instruktur)" => cari by nama_kelas & nama_instruktur
                    // Loop ke semua item untuk match dengan baris terpilih
                    String namaKelas = (String) tableModel.getValueAt(i, 2);
                    String namaInstruktur = (String) tableModel.getValueAt(i, 3);
                    for (int idx = 0; idx < cmbKelas.getItemCount(); ++idx) {
                        String s = cmbKelas.getItemAt(idx);
                        if (s.contains(namaKelas) && s.contains("(" + namaInstruktur + ")")) {
                            cmbKelas.setSelectedIndex(idx);
                            break;
                        }
                    }
                    txtTanggal.setText(tableModel.getValueAt(i, 4).toString());
                    txtCatatan.setText(tableModel.getValueAt(i, 5).toString());
                }
            }
        });

        btnSimpan.addActionListener(e -> {
            String member = (String) cmbMember.getSelectedItem();
            String kelas = (String) cmbKelas.getSelectedItem();
            String tanggalDaftar = txtTanggal.getText().trim();
            String catatan = txtCatatan.getText().trim();

            if (member == null || kelas == null || tanggalDaftar.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "Semua field wajib diisi!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int id_member = Integer.parseInt(member.split(" - ")[0]);
            int id_kelas = Integer.parseInt(kelas.split(" - ")[0]);

            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym", "root", "");
                String sql = "INSERT INTO pendaftaran_kelas (id_member, id_kelas, tanggal_daftar, catatan) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id_member);
                stmt.setInt(2, id_kelas);
                stmt.setString(3, tanggalDaftar);
                stmt.setString(4, catatan);
                stmt.executeUpdate();
                stmt.close(); conn.close();

                JOptionPane.showMessageDialog(parentFrame, "Pendaftaran kelas berhasil!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadPendaftaranKelas.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Gagal menyimpan ke database!\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnReset.addActionListener(e -> {
            cmbMember.setSelectedIndex(-1);
            cmbKelas.setSelectedIndex(-1);
            txtTanggal.setText(java.time.LocalDate.now().toString());
            txtCatatan.setText("");
        });

        btnUpdate.addActionListener(e -> {
            int row = tablePendaftaran.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(parentFrame, "Pilih data yang mau diupdate!");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);

            String member = (String) cmbMember.getSelectedItem();
            String kelas = (String) cmbKelas.getSelectedItem();
            String tanggalDaftar = txtTanggal.getText().trim();
            String catatan = txtCatatan.getText().trim();

            int id_member = Integer.parseInt(member.split(" - ")[0]);
            int id_kelas = Integer.parseInt(kelas.split(" - ")[0]);

            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym", "root", "");
                String sql = "UPDATE pendaftaran_kelas SET id_member=?, id_kelas=?, tanggal_daftar=?, catatan=? WHERE id_pendaftaran=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id_member);
                stmt.setInt(2, id_kelas);
                stmt.setString(3, tanggalDaftar);
                stmt.setString(4, catatan);
                stmt.setInt(5, id);

                stmt.executeUpdate();
                stmt.close(); conn.close();

                JOptionPane.showMessageDialog(parentFrame, "Data berhasil diupdate!");
                loadPendaftaranKelas.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Gagal update!\n" + ex.getMessage());
            }
        });

        btnHapus.addActionListener(e -> {
            int selectedRow = tablePendaftaran.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(parentFrame, "Pilih satu baris data yang ingin dihapus!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(parentFrame, "Yakin ingin menghapus pendaftaran kelas ini?",
                        "Hapus Pendaftaran", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            int idPendaftaran = (int) tableModel.getValueAt(selectedRow, 0);

            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gym", "root", "");
                String sql = "DELETE FROM pendaftaran_kelas WHERE id_pendaftaran=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, idPendaftaran);
                stmt.executeUpdate();
                stmt.close(); conn.close();

                JOptionPane.showMessageDialog(parentFrame, "Data berhasil dihapus!", "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
                loadPendaftaranKelas.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Gagal menghapus data!\n" + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}