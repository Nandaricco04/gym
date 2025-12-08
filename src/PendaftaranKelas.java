import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PendaftaranKelas extends JPanel {

    // === STATIC REGISTRY SO OTHER FORMS CAN TRIGGER REFRESH WITHOUT FRAMEREFERENCE
    // ===
    private static final List<PendaftaranKelas> INSTANCES = new ArrayList<>();

    // === METHOD BARU UNTUK REFRESH KOMBO ===
    public Runnable loadComboMember, loadComboKelas;

    public static void refreshAllMembers() {
        for (PendaftaranKelas p : INSTANCES) {
            if (p.loadComboMember != null)
                p.loadComboMember.run();
        }
    }

    public static void refreshAllKelas() {
        for (PendaftaranKelas p : INSTANCES) {
            if (p.loadComboKelas != null)
                p.loadComboKelas.run();
        }
    }

    public void refreshMember() {
        if (loadComboMember != null)
            loadComboMember.run();
    }

    public void refreshKelas() {
        if (loadComboKelas != null)
            loadComboKelas.run();
    }

    public PendaftaranKelas(JFrame parentFrame) {
        setLayout(null);

        // register instance so other forms can call static refresh
        INSTANCES.add(this);

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
        loadComboMember = () -> {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_gym", "root", "")) {
                SwingUtilities.invokeLater(() -> {
                    cmbMember.removeAllItems();
                    cmbMember.addItem("– Pilih Member –");
                });

                String sql = "SELECT id_member, nama FROM member_gym";
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        final String item = rs.getInt("id_member") + " - " + rs.getString("nama");
                        SwingUtilities.invokeLater(() -> cmbMember.addItem(item));
                    }
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(parentFrame,
                        "Gagal load member!\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE));
            }
        };

        // LOAD COMBO KELAS
        loadComboKelas = () -> {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_gym", "root", "")) {
                SwingUtilities.invokeLater(() -> {
                    cmbKelas.removeAllItems();
                    cmbKelas.addItem("– Pilih Kelas –");
                });

                String sql = "SELECT k.id_kelas, k.nama_kelas, i.nama AS instruktur " +
                        "FROM jadwal_kelas k " +
                        "JOIN instruktur_gym i ON k.id_instruktur = i.id_instruktur";

                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        final String item = rs.getInt("id_kelas") + " - " + rs.getString("nama_kelas") + " ("
                                + rs.getString("instruktur") + ")";
                        SwingUtilities.invokeLater(() -> cmbKelas.addItem(item));
                    }
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(parentFrame,
                        "Gagal load kelas!\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE));
            }
        };

        // LOAD TABLE
        Runnable loadPendaftaranKelas = () -> {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_gym", "root", "")) {
                SwingUtilities.invokeLater(() -> tableModel.setRowCount(0));

                String sql = "SELECT pk.id_pendaftaran, m.nama AS member_nama, k.nama_kelas, " +
                        "i.nama AS instruktur, pk.tanggal_daftar, pk.catatan " +
                        "FROM pendaftaran_kelas pk " +
                        "JOIN member_gym m ON pk.id_member=m.id_member " +
                        "JOIN jadwal_kelas k ON pk.id_kelas=k.id_kelas " +
                        "JOIN instruktur_gym i ON k.id_instruktur=i.id_instruktur";

                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        final Object[] row = new Object[] {
                                rs.getInt("id_pendaftaran"),
                                rs.getString("member_nama"),
                                rs.getString("nama_kelas"),
                                rs.getString("instruktur"),
                                rs.getString("tanggal_daftar"),
                                rs.getString("catatan")
                        };
                        SwingUtilities.invokeLater(() -> tableModel.addRow(row));
                    }
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(parentFrame,
                        "Gagal load tabel!\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE));
            }
        };

        // INITIAL LOAD
        loadComboMember.run();
        loadComboKelas.run();
        loadPendaftaranKelas.run();

        // TABLE CLICK (FILL FORM)
        tablePendaftaran.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = tablePendaftaran.getSelectedRow();
                if (i == -1)
                    return;

                String member = tableModel.getValueAt(i, 1).toString();
                String kelas = tableModel.getValueAt(i, 2).toString();
                String instruktur = tableModel.getValueAt(i, 3).toString();

                // Pilih member sesuai nama
                for (int a = 0; a < cmbMember.getItemCount(); a++) {
                    if (cmbMember.getItemAt(a).contains(member)) {
                        cmbMember.setSelectedIndex(a);
                        break;
                    }
                }

                // Pilih kelas sesuai nama + instruktur
                for (int a = 0; a < cmbKelas.getItemCount(); a++) {
                    String s = cmbKelas.getItemAt(a);
                    if (s.contains(kelas) && s.contains("(" + instruktur + ")")) {
                        cmbKelas.setSelectedIndex(a);
                        break;
                    }
                }

                txtTanggal.setText(tableModel.getValueAt(i, 4).toString());
                txtCatatan.setText(tableModel.getValueAt(i, 5).toString());
            }
        });

        // SIMPAN
        btnSimpan.addActionListener(e -> {
            if (cmbMember.getSelectedIndex() == 0 || cmbKelas.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(parentFrame,
                        "Pilih member dan kelas terlebih dahulu!",
                        "Error", JOptionPane.ERROR_MESSAGE);
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

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, id_member);
                    stmt.setInt(2, id_kelas);
                    stmt.setString(3, tanggal);
                    stmt.setString(4, catatan);

                    stmt.executeUpdate();
                }

                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(parentFrame, "Berhasil didaftarkan!"));
                loadPendaftaranKelas.run();

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(parentFrame,
                        "Gagal menyimpan!\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE));
            }
        });

        // RESET
        btnReset.addActionListener(e -> {
            cmbMember.setSelectedIndex(0);
            cmbKelas.setSelectedIndex(0);
            txtTanggal.setText(java.time.LocalDate.now().toString());
            txtCatatan.setText("");
        });

        // UPDATE
        btnUpdate.addActionListener(e -> {
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
        });

        // HAPUS
        btnHapus.addActionListener(e -> {
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
        });
    }
}
