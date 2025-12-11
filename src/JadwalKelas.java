import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import java.awt.Color;

public class JadwalKelas extends JPanel {
    public JadwalKelas(JFrame parentFrame) {
        setLayout(null);
        this.setBackground(new Color(250, 250, 250));

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

        // Combobox Hari dengan default
        String[] listhari = {
                "– Pilih Hari –",
                "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"
        };

        JComboBox<String> cmbHari = new JComboBox<>(listhari);
        cmbHari.setBounds(150, 60, 230, 25);
        cmbHari.setSelectedIndex(0);
        cmbHari.setBackground(new Color(250, 250, 250));
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
        cmbInstruktur.setBackground(new Color(250, 250, 250));
        add(cmbInstruktur);

        // Load Instruktur Dropdown
        Runnable loadInstruktur = () -> {
            cmbInstruktur.removeAllItems();

            cmbInstruktur.addItem("– Pilih Instruktur –");

            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_gym", "root", "");
                String sql = "SELECT id_instruktur, nama FROM instruktur_gym";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    cmbInstruktur.addItem(rs.getInt("id_instruktur") + " - " + rs.getString("nama"));
                }

                rs.close();
                stmt.close();
                conn.close();

                cmbInstruktur.setSelectedIndex(0);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Gagal mengambil instruktur!\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        loadInstruktur.run();

        // REFRESH saat panel dibuka lagi
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadInstruktur.run();
            }
        });

        // BUTTONS
        JButton btnSimpan = new JButton("Tambah Kelas");
        btnSimpan.setBounds(20, 190, 150, 30);
        btnSimpan.setBackground(new Color(76, 175, 80));
        btnSimpan.setForeground(Color.WHITE); 
        add(btnSimpan);

        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(180, 190, 100, 30);
        btnReset.setBackground(new Color(255, 193, 7));
        btnReset.setForeground(Color.WHITE); 
        add(btnReset);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(290, 190, 100, 30);
        btnUpdate.setBackground(new Color(102, 178, 255));
        btnUpdate.setForeground(Color.WHITE);
        add(btnUpdate);

        JButton btnHapus = new JButton("Hapus");
        btnHapus.setBounds(400, 190, 100, 30);
        btnHapus.setBackground(new Color(244, 67, 54));
        btnHapus.setForeground(Color.WHITE);
        add(btnHapus);

        // TABLE
        DefaultTableModel tableModel = new DefaultTableModel();
        JTable tableKelas = new JTable(tableModel);
        JScrollPane scrollTable = new JScrollPane(tableKelas);
        scrollTable.setBounds(20, 240, 600, 170);
        add(scrollTable);

        tableModel.addColumn("ID Kelas");
        tableModel.addColumn("Nama Kelas");
        tableModel.addColumn("Hari");
        tableModel.addColumn("Jam Kelas");
        tableModel.addColumn("Nama Instruktur");

        // Load data kelas
        Runnable loadKelas = () -> {
            try {
                tableModel.setRowCount(0);
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_gym", "root", "");
                String sql = "SELECT jc.id_kelas, jc.nama_kelas, jc.hari, jc.jam_kelas, ig.nama AS instruktur " +
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
                JOptionPane.showMessageDialog(parentFrame, "Gagal mengambil data jadwal!\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        loadKelas.run();

        // TABLE CLICK → isi form
        tableKelas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableKelas.getSelectedRow();
                if (row != -1) {

                    txtKelas.setText(tableModel.getValueAt(row, 1).toString());
                    cmbHari.setSelectedItem(tableModel.getValueAt(row, 2).toString());
                    txtJam.setText(tableModel.getValueAt(row, 3).toString());

                    String namaInstruktur = tableModel.getValueAt(row, 4).toString();

                    for (int i = 0; i < cmbInstruktur.getItemCount(); i++) {
                        if (cmbInstruktur.getItemAt(i).contains(namaInstruktur)) {
                            cmbInstruktur.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        // SIMPAN
        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String namaKelas = txtKelas.getText().trim();
                String hari = (String) cmbHari.getSelectedItem();
                String jamKelas = txtJam.getText();
                int selectedInstruktur = cmbInstruktur.getSelectedIndex();

                if (namaKelas.isEmpty() || jamKelas.isEmpty() || cmbHari.getSelectedIndex() == 0
                        || selectedInstruktur == 0) {
                    JOptionPane.showMessageDialog(parentFrame, "Semua field wajib diisi!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String instrukturCombo = cmbInstruktur.getSelectedItem().toString();
                int idInstruktur = Integer.parseInt(instrukturCombo.split("-")[0].trim());

                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_gym", "root", "");
                    String sql = "INSERT INTO jadwal_kelas (nama_kelas, hari, jam_kelas, id_instruktur) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);

                    stmt.setString(1, namaKelas);
                    stmt.setString(2, hari);
                    stmt.setString(3, jamKelas);
                    stmt.setInt(4, idInstruktur);
                    stmt.executeUpdate();

                    stmt.close();
                    conn.close();

                    JOptionPane.showMessageDialog(parentFrame, "Jadwal kelas berhasil ditambahkan!");
                    loadKelas.run();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parentFrame, "Gagal menyimpan!\n" + ex.getMessage());
                }
            }
        });

        // UPDATE
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableKelas.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(parentFrame, "Pilih data terlebih dahulu!");
                    return;
                }

                int idKelas = (int) tableModel.getValueAt(selectedRow, 0);
                String namaKelas = txtKelas.getText();
                String hari = (String) cmbHari.getSelectedItem();
                String jamKelas = txtJam.getText();

                if (cmbInstruktur.getSelectedIndex() == 0 || cmbHari.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(parentFrame, "Pilih hari dan instruktur!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String instrukturCombo = cmbInstruktur.getSelectedItem().toString();
                int idInstruktur = Integer.parseInt(instrukturCombo.split("-")[0].trim());

                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_gym", "root", "");
                    String sql = "UPDATE jadwal_kelas SET nama_kelas=?, hari=?, jam_kelas=?, id_instruktur=? WHERE id_kelas=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);

                    stmt.setString(1, namaKelas);
                    stmt.setString(2, hari);
                    stmt.setString(3, jamKelas);
                    stmt.setInt(4, idInstruktur);
                    stmt.setInt(5, idKelas);
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();

                    JOptionPane.showMessageDialog(parentFrame, "Jadwal berhasil diupdate!");
                    loadKelas.run();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parentFrame, "Gagal update!\n" + ex.getMessage());
                }
            }
        });

        // RESET
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtKelas.setText("");
                cmbHari.setSelectedIndex(0);
                txtJam.setText("");
                cmbInstruktur.setSelectedIndex(0);
            }
        });

        // HAPUS
        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableKelas.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(parentFrame, "Pilih baris yang ingin dihapus!");
                    return;
                }

                int idKelas = (int) tableModel.getValueAt(selectedRow, 0);

                int confirm = JOptionPane.showConfirmDialog(parentFrame,
                        "Yakin ingin menghapus jadwal ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);

                if (confirm != JOptionPane.YES_OPTION)
                    return;

                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_gym", "root", "");
                    String sql = "DELETE FROM jadwal_kelas WHERE id_kelas=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);

                    stmt.setInt(1, idKelas);
                    stmt.executeUpdate();

                    stmt.close();
                    conn.close();

                    JOptionPane.showMessageDialog(parentFrame, "Jadwal berhasil dihapus!");
                    loadKelas.run();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parentFrame, "Gagal menghapus!\n" + ex.getMessage());
                }
            }
        });
    }
}