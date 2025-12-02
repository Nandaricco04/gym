import javax.swing.*;

public class Main extends JFrame {
    public Main() {
        setTitle("Aplikasi Gym");
        setSize(700, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(10, 10, 660, 460);

        tabbedPane.addTab("Daftar Member Gym", new MemberGym(this));
        tabbedPane.addTab("Data Instruktur Gym", new Instruktur(this));
        tabbedPane.addTab("Daftar Kelas Gym", new JadwalKelas(this));
        tabbedPane.addTab("Pendaftaran Kelas Gym", new PendaftaranKelas(this));

        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}