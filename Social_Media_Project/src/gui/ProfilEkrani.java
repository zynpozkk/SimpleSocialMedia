package gui;

import model.Kullanici;
import service.VeriYoneticisi;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ProfilEkrani extends JFrame {

    private static final Color BUTON_MAVI    = new Color(59, 130, 246);
    private static final Color BUTON_YESIL   = new Color(34, 197, 94);
    private static final Color BUTON_TURUNCU = new Color(249, 115, 22);
    private static final Color BUTON_KIRMIZI = new Color(239, 68, 68);
    private static final Color BUTON_MOR     = new Color(139, 92, 246);
    private static final Color ARKAPLAN      = new Color(245, 247, 250);
    private static final Color PANEL_RENK    = new Color(255, 255, 255);

    private final VeriYoneticisi veri;
    private final String aktifKullanici;
    private final String profilSahibi;
    private final JFrame sahip;

    private JLabel lblTakipci;
    private JButton btnTakip;

    public ProfilEkrani(JFrame sahip, VeriYoneticisi veri, String aktifKullanici) {
        this(sahip, veri, aktifKullanici, aktifKullanici);
    }

    public ProfilEkrani(JFrame sahip, VeriYoneticisi veri, String aktifKullanici, String profilSahibi) {
        super("Profil — " + profilSahibi);
        this.sahip = sahip;
        this.veri = veri;
        this.aktifKullanici = aktifKullanici;
        this.profilSahibi = profilSahibi;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(ARKAPLAN);

        araYuzKur();
        pack();
        setMinimumSize(new Dimension(400, 420));
        setLocationRelativeTo(sahip);
    }

    private void araYuzKur() {
        getContentPane().removeAll();
        setLayout(new BorderLayout(0, 0));

        Kullanici bulunan = kullaniciyiBul(profilSahibi);
        if (bulunan == null) {
            JOptionPane.showMessageDialog(sahip, "Kullanici bulunamadi.", "Profil", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        JPanel bilgiPanel = new JPanel(new GridLayout(6, 1, 0, 10));
bilgiPanel.setBackground(PANEL_RENK);
bilgiPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(203, 213, 225)),
        BorderFactory.createEmptyBorder(16, 16, 16, 16)));

bilgiPanel.add(yaziSatiri("Kullanici adi:", bulunan.getKullaniciAdi()));
bilgiPanel.add(yaziSatiri("E-posta:", bulunan.getEmail()));
bilgiPanel.add(yaziSatiri("Biyografi:", bosIseCizgi(bulunan.getBiyografi())));

int takipciSayisi = 0;
int takipEdilenSayisi = 0;
try {
    takipciSayisi = veri.takipciSayisi(profilSahibi);
    takipEdilenSayisi = veri.takipEdilenSayisi(profilSahibi);
} catch (IOException ignored) {}

lblTakipci = new JLabel("<html><b>Takipciler:</b>  " + takipciSayisi + "</html>");
bilgiPanel.add(lblTakipci);
bilgiPanel.add(yaziSatiri("Takip ettikleri:", String.valueOf(takipEdilenSayisi)));

JLabel uyar = new JLabel("(Sifre guvenlik icin gosterilmez.)");
uyar.setFont(uyar.getFont().deriveFont(uyar.getFont().getSize() - 2f));
uyar.setForeground(new Color(148, 163, 184));
bilgiPanel.add(uyar);
        JPanel butonPanel = new JPanel();
        butonPanel.setLayout(new BoxLayout(butonPanel, BoxLayout.Y_AXIS));
        butonPanel.setBackground(ARKAPLAN);
        butonPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        boolean kendiProfili = aktifKullanici.equals(profilSahibi);

        if (kendiProfili) {
            JButton btnSifreDegistir     = butonOlustur("Sifre Degistir", BUTON_MAVI);
            JButton btnBiyografiGuncelle = butonOlustur("Biyografi Guncelle", BUTON_MOR);
            JButton btnCikis             = butonOlustur("Cikis Yap", BUTON_KIRMIZI);
            JButton btnHesapSil = butonOlustur("Hesabi Sil", BUTON_KIRMIZI);
            btnHesapSil.addActionListener(e -> hesapSil());

            btnSifreDegistir.addActionListener(e -> sifreDegistir());
            btnBiyografiGuncelle.addActionListener(e -> biyografiGuncelle());
            btnCikis.addActionListener(e -> cikisYap());

            butonPanel.add(btnSifreDegistir);
            butonPanel.add(Box.createVerticalStrut(8));
            butonPanel.add(btnBiyografiGuncelle);
            butonPanel.add(Box.createVerticalStrut(8));
            butonPanel.add(btnCikis);
            butonPanel.add(Box.createVerticalStrut(8));
            butonPanel.add(btnHesapSil);
        } else {
            boolean zatenTakip = takipEdiyorMu();
            btnTakip = butonOlustur(
                    zatenTakip ? "Takibi Birak" : "Takip Et",
                    zatenTakip ? BUTON_TURUNCU : BUTON_YESIL);
            btnTakip.addActionListener(e -> takipToggle(bulunan));
            butonPanel.add(btnTakip);
        }

        add(bilgiPanel, BorderLayout.CENTER);
        add(butonPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private void takipToggle(Kullanici hedef) {
        try {
            if (takipEdiyorMu()) {
                veri.takibibirak(aktifKullanici, profilSahibi);
                btnTakip.setText("Takip Et");
                btnTakip.setBackground(BUTON_YESIL);
                JOptionPane.showMessageDialog(this, hedef.getKullaniciAdi() + " takibi birakildi.", "Takip", JOptionPane.INFORMATION_MESSAGE);
            } else {
                veri.takipEt(aktifKullanici, profilSahibi);
                btnTakip.setText("Takibi Birak");
                btnTakip.setBackground(BUTON_TURUNCU);
                JOptionPane.showMessageDialog(this, hedef.getKullaniciAdi() + " takip edildi!", "Takip", JOptionPane.INFORMATION_MESSAGE);
            }
            Kullanici guncel = kullaniciyiBul(profilSahibi);
            if (guncel != null)
                lblTakipci.setText("<html><b>Takipci sayisi:</b>  " + guncel.getTakipciSayisi() + "</html>");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean takipEdiyorMu() {
        try { return veri.takipEdiyorMu(aktifKullanici, profilSahibi); }
        catch (IOException ex) { return false; }
    }

    private void sifreDegistir() {
        JPasswordField eskiSifre       = new JPasswordField();
        JPasswordField yeniSifre       = new JPasswordField();
        JPasswordField yeniSifreTekrar = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        panel.add(new JLabel("Mevcut sifre:"));       panel.add(eskiSifre);
        panel.add(new JLabel("Yeni sifre:"));          panel.add(yeniSifre);
        panel.add(new JLabel("Yeni sifre (tekrar):")); panel.add(yeniSifreTekrar);

        int sonuc = JOptionPane.showConfirmDialog(this, panel, "Sifre Degistir", JOptionPane.OK_CANCEL_OPTION);
        if (sonuc != JOptionPane.OK_OPTION) return;

        String eski   = new String(eskiSifre.getPassword()).trim();
        String yeni   = new String(yeniSifre.getPassword()).trim();
        String tekrar = new String(yeniSifreTekrar.getPassword()).trim();

        Kullanici k = kullaniciyiBul(aktifKullanici);
        if (k == null) return;

        if (!k.getSifre().equals(eski)) {
            JOptionPane.showMessageDialog(this, "Mevcut sifre yanlis!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (yeni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Yeni sifre bos olamaz!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!yeni.equals(tekrar)) {
            JOptionPane.showMessageDialog(this, "Yeni sifreler eslesmedi!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        k.setSifre(yeni);
        try {
            veri.kullanicilariDosyayaYaz();
            JOptionPane.showMessageDialog(this, "Sifre basariyla degistirildi!", "Basarili", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Kayit hatasi: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void biyografiGuncelle() {
        Kullanici k = kullaniciyiBul(aktifKullanici);
        if (k == null) return;

        String yeniBiyo = JOptionPane.showInputDialog(this,
                "Yeni biyografinizi girin:",
                k.getBiyografi() != null ? k.getBiyografi() : "");
        if (yeniBiyo == null) return;

        k.setBiyografi(yeniBiyo.trim());
        try {
            veri.kullanicilariDosyayaYaz();
            JOptionPane.showMessageDialog(this, "Biyografi guncellendi!", "Basarili", JOptionPane.INFORMATION_MESSAGE);
            araYuzKur();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Kayit hatasi: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cikisYap() {
        int onay = JOptionPane.showConfirmDialog(this,
                "Cikis yapmak istiyor musunuz?",
                "Cikis Yap", JOptionPane.YES_NO_OPTION);
        if (onay != JOptionPane.YES_OPTION) return;
        dispose();
        sahip.dispose();
        new LoginEkrani().setVisible(true);
    }
    private void hesapSil() {
    int onay = JOptionPane.showConfirmDialog(this,
            "Hesabiniz kalici olarak silinecek! Emin misiniz?",
            "Hesabi Sil", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    if (onay != JOptionPane.YES_OPTION) return;

    try {
        veri.hesapSil(aktifKullanici);
        JOptionPane.showMessageDialog(this, "Hesabiniz silindi.", "Basarili", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        sahip.dispose();
        new LoginEkrani().setVisible(true);
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
    }
}

    private Kullanici kullaniciyiBul(String ad) {
        try { veri.kullanicilarDosyadanOku(); }
        catch (IOException ex) { return null; }
        for (Kullanici k : veri.getKullanicilar())
            if (k.getKullaniciAdi().equals(ad)) return k;
        return null;
    }

    private JButton butonOlustur(String metin, Color renk) {
        JButton btn = new JButton(metin);
        btn.setBackground(renk);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return btn;
    }

    private static JPanel yaziSatiri(String baslik, String deger) {
        JPanel satir = new JPanel(new BorderLayout(8, 0));
        satir.setBackground(new Color(255, 255, 255));
        satir.add(new JLabel("<html><b>" + baslik + "</b></html>"), BorderLayout.WEST);
        satir.add(new JLabel(deger), BorderLayout.CENTER);
        return satir;
    }

    private static String bosIseCizgi(String s) {
        return s == null || s.isBlank() ? "—" : s;
    }
}