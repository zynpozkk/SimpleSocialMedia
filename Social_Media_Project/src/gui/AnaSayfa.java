package gui;

import model.Gonderi;
import model.Yorum;
import service.VeriYoneticisi;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.*;
import java.time.format.*;
import java.util.ArrayList;
import java.util.Locale;

public class AnaSayfa extends JFrame {

    private static final DateTimeFormatter TARIH_SATIR_FORMATI = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Color ARKAPLAN = new Color(245, 247, 250);
    private static final Color PANEL_RENK = new Color(255, 255, 255);
    private static final Color BUTON_MAVI = new Color(59, 130, 246);
    private static final Color BUTON_YESIL = new Color(34, 197, 94);
    private static final Color BUTON_TURUNCU = new Color(249, 115, 22);
    private static final Color BUTON_MOR = new Color(139, 92, 246);
    private static final Color BUTON_KIRMIZI = new Color(239, 68, 68);

    private final VeriYoneticisi veriYoneticisi;
    private final String kullaniciAdi;

    private final JTextField txtFiltre;
    private final JTextField txtYeniGonderi;
    private final JButton btnProfil;
    private final JButton btnIstatistik;
    private final JButton btnPaylas;
    private final JButton btnYorumEkle;
    private final JButton btnBegeni;
    private final JButton btnYorumlariGor;
    private final JTable tablo;
    private final GonderiTabloModel tabloModel;
    private final JButton btnSil;

    public AnaSayfa(VeriYoneticisi veriYoneticisi, String kullaniciAdi) {
        super("Basit Sosyal Medya — Ana Sayfa");
        this.veriYoneticisi = veriYoneticisi;
        this.kullaniciAdi = kullaniciAdi;

        getContentPane().setBackground(ARKAPLAN);

        tabloModel = new GonderiTabloModel();
        tablo = new JTable(tabloModel);
        tablo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablo.setFillsViewportHeight(true);
        tablo.setRowHeight(28);
        tablo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablo.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablo.getTableHeader().setBackground(new Color(59, 130, 246));
        tablo.getTableHeader().setForeground(Color.WHITE);
        tablo.setSelectionBackground(new Color(219, 234, 254));
        tablo.setGridColor(new Color(226, 232, 240));

        JScrollPane kaydirma = new JScrollPane(tablo);
        kaydirma.setPreferredSize(new Dimension(680, 300));
        kaydirma.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));

        txtFiltre = new JTextField(24);
        txtFiltre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtFiltre.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(148, 163, 184)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));

        txtYeniGonderi = new JTextField(42);
        txtYeniGonderi.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtYeniGonderi.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(148, 163, 184)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        txtYeniGonderi.setToolTipText("Ne dusunuyorsunuz?");

        btnProfil = butonOlustur("Profil", BUTON_MAVI);
        btnIstatistik = butonOlustur("Istatistik", BUTON_MOR);
        btnPaylas = butonOlustur("Paylas", BUTON_YESIL);
        btnYorumEkle = butonOlustur("Yorum Ekle", BUTON_MAVI);
        btnBegeni = butonOlustur(":) Begen", BUTON_TURUNCU);
        btnYorumlariGor = butonOlustur("Yorumlari Gor", BUTON_MOR);
        btnSil = butonOlustur("Gonderi Sil", BUTON_KIRMIZI);

        dinleyiciEkle();

        JLabel lblKim = new JLabel("<html><b style='font-size:14px'>" + guvenli(kullaniciAdi) + "</b></html>");
        JLabel lblAciklama = new JLabel("Oturum acildi");
        lblAciklama.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAciklama.setForeground(new Color(100, 116, 139));

        JPanel solIc = new JPanel();
        solIc.setLayout(new BoxLayout(solIc, BoxLayout.Y_AXIS));
        solIc.setBackground(PANEL_RENK);
        lblKim.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblAciklama.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnProfil.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnIstatistik.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnProfil.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnIstatistik.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        solIc.add(lblKim);
        solIc.add(Box.createVerticalStrut(4));
        solIc.add(lblAciklama);
        solIc.add(Box.createVerticalStrut(16));
        solIc.add(btnProfil);
        solIc.add(Box.createVerticalStrut(8));
        solIc.add(btnIstatistik);

        JPanel solPanel = new JPanel(new BorderLayout());
        solPanel.setBackground(PANEL_RENK);
        solPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(16, 12, 16, 12)));
        solPanel.add(solIc, BorderLayout.NORTH);
        solPanel.setPreferredSize(new Dimension(160, 0));

        JPanel filtrePanel = new JPanel(new BorderLayout(8, 0));
        filtrePanel.setBackground(PANEL_RENK);
        JLabel lblFiltre = new JLabel("Filtre (yazar):");
        lblFiltre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filtrePanel.add(lblFiltre, BorderLayout.WEST);
        filtrePanel.add(txtFiltre, BorderLayout.CENTER);

        JPanel aksiyonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        aksiyonPanel.setBackground(PANEL_RENK);
        aksiyonPanel.add(btnBegeni);
        aksiyonPanel.add(btnYorumEkle);
        aksiyonPanel.add(btnYorumlariGor);
        aksiyonPanel.add(btnSil);

        JPanel ortaUst = new JPanel(new BorderLayout(8, 8));
        ortaUst.setBackground(PANEL_RENK);
        ortaUst.add(filtrePanel, BorderLayout.NORTH);
        ortaUst.add(aksiyonPanel, BorderLayout.SOUTH);

        JPanel orta = new JPanel(new BorderLayout(8, 8));
        orta.setBackground(PANEL_RENK);
        orta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JLabel lblAkis = new JLabel("<html><b style='font-size:14px'>Gonderi Akisi</b></html>");
        lblAkis.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        orta.add(lblAkis, BorderLayout.NORTH);
        orta.add(ortaUst, BorderLayout.CENTER);
        orta.add(kaydirma, BorderLayout.SOUTH);

        JPanel altSatir = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        altSatir.setBackground(ARKAPLAN);
        altSatir.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(203, 213, 225)));
        altSatir.add(new JLabel("Yeni gonderi:"));
        altSatir.add(txtYeniGonderi);
        altSatir.add(btnPaylas);

        add(solPanel, BorderLayout.WEST);
        add(orta, BorderLayout.CENTER);
        add(altSatir, BorderLayout.SOUTH);

        txtFiltre.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { tabloyuYenile(); }
            public void removeUpdate(DocumentEvent e) { tabloyuYenile(); }
            public void changedUpdate(DocumentEvent e) { tabloyuYenile(); }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(860, 500));
        pack();
        setLocationRelativeTo(null);
        tabloyuYenile();
    }

    private JButton butonOlustur(String metin, Color renk) {
        JButton btn = new JButton(metin);
        btn.setBackground(renk);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void dinleyiciEkle() {
        btnProfil.addActionListener(e -> new ProfilEkrani(this, veriYoneticisi, kullaniciAdi).setVisible(true));
        btnIstatistik.addActionListener(e -> new IstatistikEkrani(this, veriYoneticisi).setVisible(true));
        btnPaylas.addActionListener(e -> yeniGonderiPaylas());
        btnYorumEkle.addActionListener(e -> seciliGonderiyeYorumEkle());
        btnBegeni.addActionListener(e -> seciliGonderiyiBegeni());
        btnYorumlariGor.addActionListener(e -> seciliGonderiYorumlariGoster());
        btnSil.addActionListener(e -> seciliGonderiSil());
        tablo.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(java.awt.event.MouseEvent e) {
        if (e.getClickCount() == 2) {
            int r = tablo.getSelectedRow();
            if (r < 0) return;
            Gonderi g = tabloModel.getSatir(r);
            if (g == null) return;
            new ProfilEkrani(AnaSayfa.this, veriYoneticisi, kullaniciAdi, g.getKullaniciAdi()).setVisible(true);
        }
    }
        });
    }

    private void seciliGonderiyiBegeni() {
    int r = tablo.getSelectedRow();
    if (r < 0) {
        JOptionPane.showMessageDialog(this, "Once bir gonderi secin.", "Begeni", JOptionPane.WARNING_MESSAGE);
        return;
    }
    Gonderi g = tabloModel.getSatir(r);
    if (g == null) return;

    try {
        if (veriYoneticisi.zatenBegenmis(kullaniciAdi, g.getGonderiId())) {
            JOptionPane.showMessageDialog(this, "Bu gonderiyi zaten begendiniz!", "Begeni", JOptionPane.WARNING_MESSAGE);
            return;
        }
        veriYoneticisi.gonderilerDosyadanOku();
        Gonderi hedef = gonderiyiIdyleBul(g.getGonderiId());
        if (hedef == null) return;
        hedef.setBeğeniSayisi(hedef.getBeğeniSayisi() + 1);
        veriYoneticisi.gonderileriDosyayaYaz();
        veriYoneticisi.begeniEkle(kullaniciAdi, g.getGonderiId());
        tabloyuYenile();
        JOptionPane.showMessageDialog(this, "Begenildi!", "Begeni", JOptionPane.INFORMATION_MESSAGE);
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
    }
}

    private void seciliGonderiYorumlariGoster() {
        int r = tablo.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Once bir gonderi secin.", "Yorumlar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Gonderi g = tabloModel.getSatir(r);
        if (g == null) return;

        try {
            veriYoneticisi.gonderilerDosyadanOku();
            Gonderi hedef = gonderiyiIdyleBul(g.getGonderiId());
            if (hedef == null) return;

            ArrayList<Yorum> yorumlar = hedef.getYorumlar();
            if (yorumlar.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bu gonderiye henuz yorum yapilmamis.", "Yorumlar", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (Yorum y : yorumlar) {
                sb.append(y.getKullaniciAdi())
                  .append("  ").append(y.getTarih()).append("\n")
                  .append("  ").append(y.getIcerik()).append("\n\n");
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            textArea.setBackground(new Color(248, 250, 252));
            JScrollPane sp = new JScrollPane(textArea);
            sp.setPreferredSize(new Dimension(400, 250));

            JOptionPane.showMessageDialog(this, sp,
                    "Yorumlar - " + hedef.getKullaniciAdi() + " gonderisi",
                    JOptionPane.PLAIN_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void yeniGonderiPaylas() {
        String icerik = txtYeniGonderi.getText().trim();
        if (icerik.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Gonderi metni bos olamaz.", "Paylas", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int sonrakiId = birSonrakiGonderiId();
            String tarih = LocalDateTime.now().format(TARIH_SATIR_FORMATI);
            Gonderi yeni = new Gonderi(sonrakiId, kullaniciAdi, icerik, tarih, 0);
            veriYoneticisi.gonderiKaydet(yeni);
            txtYeniGonderi.setText("");
            tabloyuYenile();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seciliGonderiyeYorumEkle() {
        int r = tablo.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Once bir gonderi secin.", "Yorum", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Gonderi gorunenden = tabloModel.getSatir(r);
        if (gorunenden == null) return;

        String metin = JOptionPane.showInputDialog(this,
                gorunenden.getKullaniciAdi() + " gonderisi icin yorum:",
                "Yorum ekle", JOptionPane.PLAIN_MESSAGE);
        if (metin == null) return;
        metin = metin.trim();
        if (metin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bos yorum eklenmez.", "Yorum", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            veriYoneticisi.gonderilerDosyadanOku();
            Gonderi hedef = gonderiyiIdyleBul(gorunenden.getGonderiId());
            if (hedef == null) return;
            int yorumId = birSonrakiYorumId(hedef);
            String tarih = LocalDateTime.now().format(TARIH_SATIR_FORMATI);
            hedef.getYorumlar().add(new Yorum(yorumId, kullaniciAdi, metin, tarih));
            veriYoneticisi.gonderileriDosyayaYaz();
            tabloyuYenile();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void seciliGonderiSil() {
    int r = tablo.getSelectedRow();
    if (r < 0) {
        JOptionPane.showMessageDialog(this, "Once bir gonderi secin.", "Sil", JOptionPane.WARNING_MESSAGE);
        return;
    }
    Gonderi g = tabloModel.getSatir(r);
    if (g == null) return;

    if (!g.getKullaniciAdi().equals(kullaniciAdi)) {
        JOptionPane.showMessageDialog(this, "Sadece kendi gonderinizi silebilirsiniz!", "Sil", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int onay = JOptionPane.showConfirmDialog(this,
            "Bu gonderiyi silmek istiyor musunuz?",
            "Gonderi Sil", JOptionPane.YES_NO_OPTION);
    if (onay != JOptionPane.YES_OPTION) return;

    try {
        veriYoneticisi.gonderilerDosyadanOku();
        veriYoneticisi.getGonderiler().removeIf(gon -> gon.getGonderiId() == g.getGonderiId());
        veriYoneticisi.gonderileriDosyayaYaz();
        tabloyuYenile();
        JOptionPane.showMessageDialog(this, "Gonderi silindi!", "Sil", JOptionPane.INFORMATION_MESSAGE);
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
    }
}

    private Gonderi gonderiyiIdyleBul(int id) {
        for (Gonderi g : veriYoneticisi.getGonderiler()) {
            if (g.getGonderiId() == id) return g;
        }
        return null;
    }

    private int birSonrakiGonderiId() throws IOException {
        veriYoneticisi.gonderilerDosyadanOku();
        int mx = 0;
        for (Gonderi g : veriYoneticisi.getGonderiler()) mx = Math.max(mx, g.getGonderiId());
        return mx + 1;
    }

    private static int birSonrakiYorumId(Gonderi g) {
        int mx = 0;
        for (Yorum y : g.getYorumlar()) mx = Math.max(mx, y.getYorumId());
        return mx + 1;
    }

    private void tabloyuYenile() {
        try {
            ArrayList<Gonderi> list = goruntuListesiOlustur();
            tabloModel.setSatirlar(list);
            tablo.clearSelection();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private ArrayList<Gonderi> goruntuListesiOlustur() throws IOException {
        veriYoneticisi.gonderilerDosyadanOku();
        ArrayList<Gonderi> liste = new ArrayList<>(veriYoneticisi.getGonderiler());
        tariheGoreBubbleSortAzalan(liste);
        String filt = txtFiltre.getText().trim().toLowerCase(Locale.ROOT);
        if (filt.isEmpty()) return liste;
        ArrayList<Gonderi> sonuc = new ArrayList<>();
        for (Gonderi g : liste) {
            String yazar = g.getKullaniciAdi() != null ? g.getKullaniciAdi().toLowerCase(Locale.ROOT) : "";
            if (yazar.contains(filt)) sonuc.add(g);
        }
        return sonuc;
    }

    private void tariheGoreBubbleSortAzalan(ArrayList<Gonderi> liste) {
        int n = liste.size();
        for (int i = 0; i < n - 1; i++)
            for (int j = 0; j < n - i - 1; j++) {
                if (tarihPuani(liste.get(j).getTarih()) < tarihPuani(liste.get(j + 1).getTarih())) {
                    Gonderi tmp = liste.get(j);
                    liste.set(j, liste.get(j + 1));
                    liste.set(j + 1, tmp);
                }
            }
    }

    private static long tarihPuani(String tarih) {
        if (tarih == null || tarih.trim().isEmpty()) return Long.MIN_VALUE;
        try { return LocalDateTime.parse(tarih.trim()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(); } catch (DateTimeParseException ignored) {}
        try { return LocalDateTime.parse(tarih.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(); } catch (DateTimeParseException ignored) {}
        try { return LocalDate.parse(tarih.trim()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(); } catch (DateTimeParseException ignored) {}
        return tarih.hashCode();
    }

    private static String guvenli(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public VeriYoneticisi getVeriYoneticisi() { return veriYoneticisi; }
    public String getKullaniciAdi() { return kullaniciAdi; }

    private static final class GonderiTabloModel extends AbstractTableModel {
        private static final String[] KOLONLAR = {"ID", "Yazar", "Icerik", "Tarih", "Begeni", "Yorum"};
        private ArrayList<Gonderi> satirlar = new ArrayList<>();

        void setSatirlar(ArrayList<Gonderi> yeni) {
            satirlar = yeni != null ? yeni : new ArrayList<>();
            fireTableDataChanged();
        }

        Gonderi getSatir(int row) {
            if (row < 0 || row >= satirlar.size()) return null;
            return satirlar.get(row);
        }

        public int getRowCount() { return satirlar.size(); }
        public int getColumnCount() { return KOLONLAR.length; }
        public String getColumnName(int c) { return KOLONLAR[c]; }

        public Object getValueAt(int r, int c) {
            Gonderi g = satirlar.get(r);
            return switch (c) {
                case 0 -> g.getGonderiId();
                case 1 -> g.getKullaniciAdi();
                case 2 -> g.getIcerik();
                case 3 -> g.getTarih();
                case 4 -> g.getBeğeniSayisi();
                case 5 -> g.getYorumlar().size();
                default -> "";
            };
        }

        public Class<?> getColumnClass(int c) {
            return switch (c) {
                case 0, 4, 5 -> Integer.class;
                default -> String.class;
            };
        }

        public boolean isCellEditable(int r, int c) { return false; }
    }
}