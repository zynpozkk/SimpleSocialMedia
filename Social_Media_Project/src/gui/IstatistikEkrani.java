package gui;

import model.Gonderi;
import model.Kullanici;
import service.VeriYoneticisi;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IstatistikEkrani extends JFrame {

    private static final int GRAFIK_YUKSEKLIK = 200;
    private static final int GRAFIK_CUBUK_SAYISI = 8;

    public IstatistikEkrani(JFrame sahip, VeriYoneticisi veri) {
        super("İstatistikler");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        try {
            veri.kullanicilarDosyadanOku();
            veri.gonderilerDosyadanOku();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(sahip, "Dosya okunurken hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        List<Kullanici> kullanicilar = veri.getKullanicilar();
        List<Gonderi> gonderiler = veri.getGonderiler();

        int yorumToplam = 0;
        for (Gonderi g : gonderiler) {
            yorumToplam += g.getYorumlar().size();
        }

        JPanel ozet = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        ozet.setBorder(new TitledBorder("Özet"));
        ozet.add(ozetEtiket("Toplam kullanıcı:", String.valueOf(kullanicilar.size())));
        ozet.add(ozetEtiket("Toplam gönderi:", String.valueOf(gonderiler.size())));
        ozet.add(ozetEtiket("Toplam yorum:", String.valueOf(yorumToplam)));

        List<KullaniciGonderiSatiri> enCokGonderi = enCokGonderiYapanlar(gonderiler);
        List<BegeniSatiri> enCokBegeni = enCokBegeniAlanGonderiler(gonderiler);

        JTable tabloKullanici = new JTable(new KullaniciGonderiModel(enCokGonderi));
        tabloKullanici.setFillsViewportHeight(true);

        JTable tabloBegeni = new JTable(new BegeniGonderiModel(enCokBegeni));
        tabloBegeni.setFillsViewportHeight(true);

        JScrollPane scKullanici = new JScrollPane(tabloKullanici);
        scKullanici.setPreferredSize(new Dimension(320, 220));
        scKullanici.setBorder(new TitledBorder("En çok gönderi yapan kullanıcılar"));

        JScrollPane scBegeni = new JScrollPane(tabloBegeni);
        scBegeni.setPreferredSize(new Dimension(360, 220));
        scBegeni.setBorder(new TitledBorder("En çok beğeni alan gönderiler"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scKullanici, scBegeni);
        split.setResizeWeight(0.48);
        split.setContinuousLayout(true);

        List<String> grafikEtiketler = new ArrayList<>();
        List<Integer> grafikDegerler = new ArrayList<>();
        int n = Math.min(GRAFIK_CUBUK_SAYISI, enCokGonderi.size());
        for (int i = 0; i < n; i++) {
            KullaniciGonderiSatiri s = enCokGonderi.get(i);
            grafikEtiketler.add(s.kullaniciAdi());
            grafikDegerler.add(s.gonderiSayisi());
        }

        GrafikPanel grafik =
                new GrafikPanel(grafikEtiketler, grafikDegerler, "Gönderi sayısı (en çok paylaşım yapanlar)");
        grafik.setPreferredSize(new Dimension(640, GRAFIK_YUKSEKLIK));
        grafik.setBorder(BorderFactory.createCompoundBorder(new TitledBorder("Çubuk grafik"),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        JPanel govde = new JPanel(new BorderLayout(8, 8));
        govde.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
        govde.add(ozet, BorderLayout.NORTH);
        govde.add(split, BorderLayout.CENTER);
        govde.add(grafik, BorderLayout.SOUTH);

        add(govde, BorderLayout.CENTER);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(sahip);
    }

    private static JPanel ozetEtiket(String baslik, String deger) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.add(new JLabel("<html>" + baslik + "</html>"), BorderLayout.NORTH);
        JLabel vl = new JLabel(deger);
        vl.setFont(vl.getFont().deriveFont(Font.BOLD, vl.getFont().getSize() + 5f));
        p.add(vl, BorderLayout.CENTER);
        return p;
    }

    private static List<KullaniciGonderiSatiri> enCokGonderiYapanlar(List<Gonderi> gonderiler) {
        Map<String, Integer> say = new HashMap<>();
        for (Gonderi g : gonderiler) {
            String ad = g.getKullaniciAdi();
            ad = ad == null || ad.isBlank() ? "(bilinmeyen)" : ad.trim();
            say.merge(ad, 1, Integer::sum);
        }
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(say.entrySet());
        entries.sort(Map.Entry.<String, Integer>comparingByValue().reversed()
                .thenComparing(Map.Entry.comparingByKey()));
        ArrayList<KullaniciGonderiSatiri> sonuc = new ArrayList<>();
        int sirano = 1;
        for (Map.Entry<String, Integer> e : entries) {
            sonuc.add(new KullaniciGonderiSatiri(sirano++, e.getKey(), e.getValue()));
        }
        return sonuc;
    }

    private static List<BegeniSatiri> enCokBegeniAlanGonderiler(List<Gonderi> gonderiler) {
        ArrayList<Gonderi> kopya = new ArrayList<>(gonderiler);
        kopya.sort(Comparator
                .comparingInt(Gonderi::getBeğeniSayisi).reversed()
                .thenComparingInt(Gonderi::getGonderiId));
        List<BegeniSatiri> sonuc = new ArrayList<>();
        int sirano = 1;
        for (Gonderi g : kopya) {
            sonuc.add(new BegeniSatiri(
                    sirano++,
                    g.getGonderiId(),
                    bosIsemBilinmeyen(g.getKullaniciAdi()),
                    g.getBeğeniSayisi(),
                    ksalt(g.getIcerik())));
        }
        return sonuc;
    }

    private static String bosIsemBilinmeyen(String s) {
        if (s == null || s.isBlank()) {
            return "(bilinmeyen)";
        }
        return s.trim();
    }

    private static String ksalt(String icerik) {
        if (icerik == null) {
            return "";
        }
        String s = icerik.replace('\n', ' ').trim();
        return s.length() <= 52 ? s : s.substring(0, 49) + "…";
    }

    /** Saf Graphics2D çubuk grafiği. */
    private static final class GrafikPanel extends JPanel {

        private static final Color ARKA_PLAN = new Color(250, 250, 250);
        private static final Color ESAS_CIZGI = new Color(90, 90, 110);
        private static final Color[] CUBUK_RENKLER = {
                new Color(70, 130, 200),
                new Color(220, 120, 60),
                new Color(80, 180, 140),
                new Color(200, 85, 150),
                new Color(150, 100, 200),
                new Color(200, 180, 70),
                new Color(110, 150, 90),
                new Color(100, 180, 200)
        };

        private final List<String> etiketler;
        private final List<Integer> degerler;
        private final String baslik;

        GrafikPanel(List<String> etiketler, List<Integer> degerler, String baslik) {
            setOpaque(true);
            this.etiketler = List.copyOf(etiketler);
            this.degerler = List.copyOf(degerler);
            this.baslik = baslik;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            g2.setColor(ARKA_PLAN);
            g2.fillRect(0, 0, w, h);

            if (baslik != null && !baslik.isEmpty()) {
                g2.setColor(ESAS_CIZGI);
                Font f = getFont().deriveFont(Font.BOLD, Math.max(11f, getFont().getSize()));
                g2.setFont(f);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(baslik, 12, Math.min(22, fm.getAscent() + 10));
            }

            int n = Math.min(this.etiketler.size(), this.degerler.size());
            if (n == 0) {
                g2.setColor(Color.GRAY);
                g2.drawString("Çizilecek veri yok.", 14, h / 2);
                g2.dispose();
                return;
            }

            int marginSol = 14;
            int marginSag = 14;
            int marginAlt = 38;
            int marginUst = 36;

            int plotSol = marginSol;
            int plotAlt = h - marginAlt;
            int plotSağ = w - marginSag;
            int plotUst = marginUst;
            int plotW = Math.max(1, plotSağ - plotSol);
            int plotH = Math.max(1, plotAlt - plotUst);

            int maxDeg = Math.max(this.degerler.stream().max(Integer::compareTo).orElse(1), 1);

            g2.setColor(ESAS_CIZGI);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawLine(plotSol, plotAlt, plotSağ, plotAlt);

            double boslukOrani = 0.28;
            double cubukYuvar = 6;
            int cubukAlan = plotW / n;

            Font etikFont = getFont().deriveFont(Math.max(10f, getFont().getSize() - 1f));
            g2.setFont(etikFont);

            for (int i = 0; i < n; i++) {
                int deg = Math.max(this.degerler.get(i), 0);
                int cubukGen = (int) Math.round(cubukAlan * (1.0 - boslukOrani));
                cubukGen = Math.max(4, cubukGen);

                int x = plotSol + i * cubukAlan + (cubukAlan - cubukGen) / 2;
                int cubukYuk = (int) Math.round((double) deg / maxDeg * (plotH - 8));
                int cubukBas = plotAlt - cubukYuk;

                Color rk = CUBUK_RENKLER[i % CUBUK_RENKLER.length];
                g2.setColor(rk.brighter());
                g2.fillRoundRect(x, cubukBas, cubukGen, cubukYuk,
                        (int) cubukYuvar, (int) cubukYuvar);
                g2.setColor(rk.darker());
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(x, cubukBas, cubukGen, cubukYuk,
                        (int) cubukYuvar, (int) cubukYuvar);

                g2.setColor(ESAS_CIZGI);
                String ust = Integer.toString(deg);
                FontMetrics uf = g2.getFontMetrics(g2.getFont());
                int tx = x + (cubukGen - uf.stringWidth(ust)) / 2;
                int ty = Math.max(plotUst + 12, cubukBas - 4);
                g2.drawString(ust, tx, ty);

                String etiket = kisaltEtik(this.etiketler.get(i), cubukGen + 14);
                FontMetrics lf = g2.getFontMetrics(etikFont);
                int lx = plotSol + i * cubukAlan + Math.max(0, (cubukAlan - lf.stringWidth(etiket)) / 2);
                int ly = Math.min(plotAlt + lf.getAscent() + 6, h - 6);
                g2.setFont(etikFont);
                g2.drawString(etiket, lx, ly);
                g2.setFont(getFont());
            }

            g2.dispose();
        }

        private static String kisaltEtik(String metin, int maxGen) {
            if (metin == null || metin.isBlank()) {
                return "—";
            }
            String s = metin.trim();
            int maxLen = Math.max(4, Math.min(14, maxGen / 8));
            if (s.length() <= maxLen) {
                return s;
            }
            return s.substring(0, maxLen - 1) + "…";
        }
    }

    private record KullaniciGonderiSatiri(int sira, String kullaniciAdi, int gonderiSayisi) {}

    private record BegeniSatiri(int sira, int gonderiId, String kullaniciAdi, int begeni, String ozetIcerik) {}

    private static final class KullaniciGonderiModel extends AbstractTableModel {

        private final List<KullaniciGonderiSatiri> veri;

        KullaniciGonderiModel(List<KullaniciGonderiSatiri> veri) {
            this.veri = veri;
        }

        @Override
        public int getRowCount() {
            return veri.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int column) {
            return switch (column) {
                case 0 -> "Sıra";
                case 1 -> "Kullanıcı adı";
                case 2 -> "Gönderi sayısı";
                default -> "";
            };
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            KullaniciGonderiSatiri s = veri.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> s.sira();
                case 1 -> s.kullaniciAdi();
                case 2 -> s.gonderiSayisi();
                default -> "";
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 0, 2 -> Integer.class;
                default -> String.class;
            };
        }
    }

    private static final class BegeniGonderiModel extends AbstractTableModel {

        private final List<BegeniSatiri> veri;

        BegeniGonderiModel(List<BegeniSatiri> veri) {
            this.veri = veri;
        }

        @Override
        public int getRowCount() {
            return veri.size();
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public String getColumnName(int column) {
            return switch (column) {
                case 0 -> "Sıra";
                case 1 -> "Gönderi ID";
                case 2 -> "Yazar";
                case 3 -> "Beğeni";
                case 4 -> "İçerik özeti";
                default -> "";
            };
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            BegeniSatiri r = veri.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> r.sira();
                case 1 -> r.gonderiId();
                case 2 -> r.kullaniciAdi();
                case 3 -> r.begeni();
                case 4 -> r.ozetIcerik();
                default -> "";
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 0, 1, 3 -> Integer.class;
                default -> String.class;
            };
        }
    }
}
