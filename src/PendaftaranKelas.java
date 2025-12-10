import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class PendaftaranKelas extends JPanel {

    public PendaftaranKelas(JFrame parentFrame) {
        setLayout(null);

        // LABEL & INPUT
        JLabel lblMember = new JLabel("Member Gym:");
        lblMember.setBounds(20, 20, 120, 25);
        add(lblMember);

        JComboBox<String> cmbMember = new JComboBox<>();
        cmbMember.setBounds(150, 20, 230, 25);
        add(cmbMember);

        JLabel lblKelas = new JLabel("Kelas:");
        lblKelas.setBounds(20, 60, 120, 25);
        add(lblKelas);

        JComboBox<String> cmbKelas = new JComboBox<>();
        cmbKelas.setBounds(150, 60, 230, 25);
        add(cmbKelas);

        JLabel lblTanggal = new JLabel("Tanggal Daftar:");
        lblTanggal.setBounds(20, 100, 120, 25);
        add(lblTanggal);

        JTextField txtTanggal = new JTextField();
        txtTanggal.setBounds(150, 100, 230, 25);
        txtTanggal.setText(java.time.LocalDate.now().toString());
        add(txtTanggal);

        JLabel lblCatatan = new JLabel("Catatan:");
        lblCatatan.setBounds(20, 140, 120, 25);
        add(lblCatatan);

        JTextField txtCatatan = new JTextField();
        txtCatatan.setBounds(150, 140, 230, 25);
        add(txtCatatan);

        // BUTTONS ======
        JButton btnSimpan = new JButton("Daftarkan Kelas");
        btnSimpan.setBounds(20, 190, 150, 30);
        add(btnSimpan);

        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(180, 190, 100, 30);
        add(btnReset);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(290, 190, 100, 30);
        add(btnUpdate);

        JButton btnHapus = new JButton("Hapus");
        btnHapus.setBounds(400, 190, 100, 30);
        add(btnHapus);

        // TABLE
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

        // LOAD COMBO MEMBER
        Runnable loadMember = () -> {
            cmbMember.removeAllItems();
            cmbMember.addItem("– Pilih Member –");
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_gym", "root", "")) {
                String sql = "SELECT id_member, nama FROM member_gym";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    cmbMember.addItem(rs.getInt("id_member") + " - " + rs.getString("nama"));
                }
                rs.close();
                stmt.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Gagal load member!\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            cmbMember.setSelectedIndex(0);
        };

        // LOAD COMBO KELAS
        Runnable loadKelas = () -> {
            cmbKelas.removeAllItems();
            cmbKelas.addItem("– Pilih Kelas –");
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_gym", "root", "")) {
                String sql = "SELECT k.id_kelas, k.nama_kelas, i.nama AS instruktur FROM jadwal_kelas k JOIN instruktur_gym i ON k.id_instruktur = i.id_instruktur";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    cmbKelas.addItem(rs.getInt("id_kelas") + " - " + rs.getString("nama_kelas") + " ("
                            + rs.getString("instruktur") + ")");
                }
                rs.close();
                stmt.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Gagal load kelas!\n" + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            cmbKelas.setSelectedIndex(0);
        };

        // LOAD TABLE PENDAFTARAN
        Runnable loadPendaftaranKelas = () -> {
            tableModel.setRowCount(0);
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_gym", "root", "")) {
                String sql = "SELECT pk.id_pendaftaran, m.nama AS member_nama, k.nama_kelas, i.nama AS instruktur, pk.tanggal_daftar, pk.catatan FROM pendaftaran_kelas pk JOIN member_gym m ON pk.id_member=m.id_member JOIN jadwal_kelas k ON pk.id_kelas=k.id_kelas JOIN instruktur_gym i ON k.id_instruktur=i.id_instruktur";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    tableModel.addRow(new Object[] {
                            rs.getInt("id_pendaftaran"),
                            rs.getString("member_nama"),
                            rs.getString("nama_kelas"),
                            rs.getString("instruktur"),
                            rs.getString("tanggal_daftar"),
                            rs.getString("catatan")
                    });
                }
                rs.close();
                stmt.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Gagal load tabel!\n" + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        };

        // LOAD AWAL
        loadMember.run();
        loadKelas.run();
        loadPendaftaranKelas.run();

        // REFRESH saat panel dibuka/dipanggil lagi
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadMember.run();
                loadKelas.run();
            }
        });

        // SINKRONISASI INPUT SAAT KLIK TABEL
        tablePendaftaran.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tablePendaftaran.getSelectedRow();
                if (row != -1) {
                    String memberNama = tableModel.getValueAt(row, 1).toString();
                    for (int i = 0; i < cmbMember.getItemCount(); i++) {
                        if (cmbMember.getItemAt(i).contains(memberNama)) {
                            cmbMember.setSelectedIndex(i);
                            break;
                        }
                    }

                    String kelasNama = tableModel.getValueAt(row, 2).toString();
                    for (int i = 0; i < cmbKelas.getItemCount(); i++) {
                        if (cmbKelas.getItemAt(i).contains(kelasNama)) {
                            cmbKelas.setSelectedIndex(i);
                            break;
                        }
                    }

                    txtTanggal.setText(tableModel.getValueAt(row, 4).toString());
                    txtCatatan.setText(tableModel.getValueAt(row, 5).toString());
                }
            }
        });

        // SIMPAN
        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cmbMember.getSelectedIndex() == 0 || cmbKelas.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(parentFrame, "Pilih member dan kelas terlebih dahulu!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String member = cmbMember.getSelectedItem().toString();
                String kelas = cmbKelas.getSelectedItem().toString();
                String tanggal = txtTanggal.getText().trim();
                String catatan = txtCatatan.getText().trim();

                int id_member = Integer.parseInt(member.split(" - ")[0]);
                int id_kelas = Integer.parseInt(kelas.split(" - ")[0]);

                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_gym", "root", "")) {
                    String sql = "INSERT INTO pendaftaran_kelas (id_member, id_kelas, tanggal_daftar, catatan) VALUES (?, ?, ?, ?)";

                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, id_member);
                    stmt.setInt(2, id_kelas);
                    stmt.setString(3, tanggal);
                    stmt.setString(4, catatan);
                    stmt.executeUpdate();
                    stmt.close();

                    JOptionPane.showMessageDialog(parentFrame, "Berhasil didaftarkan!");
                    loadPendaftaranKelas.run();

                    loadMember.run();
                    loadKelas.run();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parentFrame, "Gagal menyimpan!\n" + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // RESET
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cmbMember.setSelectedIndex(0);
                cmbKelas.setSelectedIndex(0);
                txtTanggal.setText(java.time.LocalDate.now().toString());
                txtCatatan.setText("");
            }
        });

        // UPDATE
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tablePendaftaran.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(parentFrame, "Pilih data terlebih dahulu!");
                    return;
                }

                int id = (int) tableModel.getValueAt(row, 0);

                String member = cmbMember.getSelectedItem().toString();
                String kelas = cmbKelas.getSelectedItem().toString();

                int id_member = Integer.parseInt(member.split(" - ")[0]);
                int id_kelas = Integer.parseInt(kelas.split(" - ")[0]);

                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_gym", "root", "")) {
                    String sql = "UPDATE pendaftaran_kelas SET id_member=?, id_kelas=?, tanggal_daftar=?, catatan=? WHERE id_pendaftaran=?";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, id_member);
                        stmt.setInt(2, id_kelas);
                        stmt.setString(3, txtTanggal.getText());
                        stmt.setString(4, txtCatatan.getText());
                        stmt.setInt(5, id);

                        stmt.executeUpdate();
                    }

                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(parentFrame, "Update berhasil!"));
                    loadPendaftaranKelas.run();

                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(parentFrame,
                            "Gagal update!\n" + ex.getMessage()));
                }
            }
        });

        // HAPUS
        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tablePendaftaran.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Pilih data yang ingin dihapus!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(parentFrame,
                        "Yakin ingin menghapus data ini?", "Hapus Data",
                        JOptionPane.YES_NO_OPTION);

                if (confirm != JOptionPane.YES_OPTION)
                    return;

                int id = (int) tableModel.getValueAt(row, 0);

                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_gym", "root", "")) {
                    String sql = "DELETE FROM pendaftaran_kelas WHERE id_pendaftaran=?";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, id);
                        stmt.executeUpdate();
                    }

                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(parentFrame, "Berhasil dihapus!"));
                    loadPendaftaranKelas.run();

                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(parentFrame,
                            "Gagal hapus!\n" + ex.getMessage()));
                }
            }
        });
    }
}