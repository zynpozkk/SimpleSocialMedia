package gui;

import model.Kullanici;
import service.VeriYoneticisi;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;

public class LoginEkrani extends JFrame {

    private final VeriYoneticisi veriYoneticisi;
    private final JLabel lblBaslik;
    private final JLabel lblHata;
    private final JTextField txtKullaniciAdi;
    private final JPasswordField txtSifre;
    private final JButton btnGiris;
    private final JButton btnKayitOl;

    public LoginEkrani(VeriYoneticisi veriYoneticisi) {
        super("Basit Sosyal Medya — Giriş");
        this.veriYoneticisi = veriYoneticisi;

        lblBaslik = new JLabel("Giriş Yap", SwingConstants.LEADING);
        lblBaslik.setFont(lblBaslik.getFont().deriveFont(lblBaslik.getFont().getSize() + 6f));
        lblBaslik.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));

        lblHata = new JLabel(" ");
        lblHata.setForeground(new Color(180, 0, 0));
        lblHata.setBorder(BorderFactory.createEmptyBorder(4, 0, 8, 0));

        txtKullaniciAdi = new JTextField(18);
        txtSifre = new JPasswordField(18);

        btnGiris = new JButton("Giriş");
        btnKayitOl = new JButton("Kayıt Ol");

        JPanel formPanel = olusturFormPaneli();
        formPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));

        btnGiris.addActionListener(e -> girisDenemesi());
        btnKayitOl.addActionListener(e -> kayitIslemi());

        JPanel altPanel = new JPanel(new BorderLayout(0, 0));
        altPanel.add(lblHata, BorderLayout.NORTH);
        JPanel btnSatir = new JPanel();
        btnSatir.add(btnGiris);
        btnSatir.add(btnKayitOl);
        altPanel.add(btnSatir, BorderLayout.CENTER);

        add(lblBaslik, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(altPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);

        SwingUtilities.invokeLater(() -> txtKullaniciAdi.requestFocusInWindow());
    }

    /** Varsayılan: proje klasöründeki {@code data} dizininden okur/yazar. */
    public LoginEkrani() {
        this(new VeriYoneticisi());
    }

    private JPanel olusturFormPaneli() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 0, 6, 10);

        JLabel lblKu = new JLabel("Kullanıcı adı");
        JLabel lblSf = new JLabel("Şifre");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblKu, gbc);

        gbc.gridy = 1;
        panel.add(lblSf, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        panel.add(txtKullaniciAdi, gbc);

        gbc.gridy = 1;
        panel.add(txtSifre, gbc);

        return panel;
    }

    private void girisDenemesi() {
        lblHata.setText(" ");

        String kullaniciAdi = txtKullaniciAdi.getText().trim();
        String sifre = String.valueOf(txtSifre.getPassword());

        if (kullaniciAdi.isEmpty() || sifre.isEmpty()) {
            lblHata.setText("Kullanıcı adı ve şifre gereklidir.");
            return;
        }

        try {
            veriYoneticisi.kullanicilarDosyadanOku();
        } catch (IOException ex) {
            lblHata.setText("Dosya okunurken bir hata oluştu.");
            JOptionPane.showMessageDialog(this,
                    ex.getMessage() != null ? ex.getMessage() : "Dosya okunurken bir hata oluştu.",
                    "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!veriYoneticisi.kullaniciGirisDogruMu(kullaniciAdi, sifre)) {
            lblHata.setText("Geçersiz kullanıcı adı veya şifre.");
            return;
        }

        setVisible(false);
        dispose();

        SwingUtilities.invokeLater(() -> new AnaSayfa(veriYoneticisi, kullaniciAdi).setVisible(true));
    }

    private void kayitIslemi() {
        lblHata.setText(" ");
        String kullaniciAdi = txtKullaniciAdi.getText().trim();
        String sifre = String.valueOf(txtSifre.getPassword());

        if (kullaniciAdi.isEmpty() || sifre.isEmpty()) {
            lblHata.setText("Kayıt için kullanıcı adı ve şifre girin.");
            return;
        }

        try {
            veriYoneticisi.kullanicilarDosyadanOku();
        } catch (IOException ex) {
            lblHata.setText("Dosya okunurken bir hata oluştu.");
            JOptionPane.showMessageDialog(this,
                    ex.getMessage() != null ? ex.getMessage() : "Dosya okunurken bir hata oluştu.",
                    "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Kullanici k : veriYoneticisi.getKullanicilar()) {
            if (k.getKullaniciAdi().equals(kullaniciAdi)) {
                lblHata.setText("Bu kullanıcı adı zaten kayıtlı.");
                return;
            }
        }

 String email = JOptionPane.showInputDialog(this, "E-posta:", "Kayıt — E-posta", JOptionPane.QUESTION_MESSAGE);
email = email != null ? email.trim() : "";
if (email.isEmpty()) {
    lblHata.setText("E-posta zorunludur.");
    return;
}
if (!email.contains("@") || !email.contains(".")) {
    lblHata.setText("Geçersiz e-posta adresi.");
    JOptionPane.showMessageDialog(this, "Lütfen geçerli bir e-posta girin. (ornek@mail.com)", "Hata", JOptionPane.ERROR_MESSAGE);
    return;
}

        String bio = JOptionPane.showInputDialog(this, "Kısa biyografi:", "Kayıt — Biyografi", JOptionPane.PLAIN_MESSAGE);
        bio = bio != null ? bio.trim() : "";

        Kullanici yeni = new Kullanici(kullaniciAdi, sifre, email, bio, 0);
        try {
            veriYoneticisi.kullaniciKaydet(yeni);
            lblHata.setText("");
            JOptionPane.showMessageDialog(this,
                    "Kayıt başarılı. Şimdi giriş yapabilirsiniz.",
                    "Tamamlandı", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            lblHata.setText("Kullanıcı kaydedilirken bir hata oluştu.");
            JOptionPane.showMessageDialog(this,
                    ex.getMessage() != null ? ex.getMessage() : "Kullanıcı kaydedilirken bir hata oluştu.",
                    "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Uygulamayı doğrudan bu pencereyle başlatır. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginEkrani().setVisible(true));
    }
}
