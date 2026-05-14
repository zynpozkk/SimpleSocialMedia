package service;

import model.Gonderi;
import model.Kullanici;
import model.Yorum;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class VeriYoneticisi {
    private final Path kullaniciDosyaYolu;
    private final Path gonderiDosyaYolu;
    private final Path takipDosyaYolu;
    private final Path begeniDosyaYolu;

    private ArrayList<Kullanici> kullanicilar;
    private ArrayList<Gonderi> gonderiler;
    private ArrayList<String> takipler;
    private ArrayList<String> begeniler;

    public VeriYoneticisi() {
        this(Paths.get("data"));
    }

    public VeriYoneticisi(Path veriKlasoru) {
        this.kullaniciDosyaYolu = veriKlasoru.resolve("kullanicilar.txt");
        this.gonderiDosyaYolu   = veriKlasoru.resolve("gonderiler.txt");
        this.takipDosyaYolu     = veriKlasoru.resolve("takipler.txt");
        this.begeniDosyaYolu    = veriKlasoru.resolve("begeniler.txt");
        this.kullanicilar = new ArrayList<>();
        this.gonderiler   = new ArrayList<>();
        this.takipler     = new ArrayList<>();
        this.begeniler    = new ArrayList<>();
    }

    public ArrayList<Kullanici> getKullanicilar() { return kullanicilar; }
    public ArrayList<Gonderi>   getGonderiler()   { return gonderiler; }
    public ArrayList<String>    getTakipler()     { return takipler; }
    public ArrayList<String>    getBegeniler()    { return begeniler; }

    //Kullanıcı kısmı

    public ArrayList<Kullanici> kullanicilarDosyadanOku() throws IOException {
        kullanicilar.clear();
        if (!Files.exists(kullaniciDosyaYolu)) return kullanicilar;
        for (String line : Files.readAllLines(kullaniciDosyaYolu, StandardCharsets.UTF_8)) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] p = line.split("\\|", 5);
            if (p.length != 5) continue;
            try {
                kullanicilar.add(new Kullanici(p[0], p[1], p[2], p[3], Integer.parseInt(p[4])));
            } catch (NumberFormatException ignored) {}
        }
        return kullanicilar;
    }

    public void kullanicilariDosyayaYaz() throws IOException {
        dizinOlustur(kullaniciDosyaYolu);
        try (BufferedWriter w = Files.newBufferedWriter(kullaniciDosyaYolu, StandardCharsets.UTF_8)) {
            for (Kullanici k : kullanicilar) { w.write(k.toString()); w.newLine(); }
        }
    }

    public void kullaniciKaydet(Kullanici kullanici) throws IOException {
        kullanicilarDosyadanOku();
        kullanicilar.add(kullanici);
        kullanicilariDosyayaYaz();
    }

    public boolean kullaniciGirisDogruMu(String kullaniciAdi, String sifre) {
        for (Kullanici k : kullanicilar)
            if (k.getKullaniciAdi().equals(kullaniciAdi) && k.getSifre().equals(sifre)) return true;
        return false;
    }

    // Gönderi kısmı

    public ArrayList<Gonderi> gonderilerDosyadanOku() throws IOException {
        gonderiler.clear();
        if (!Files.exists(gonderiDosyaYolu)) return gonderiler;
        for (String line : Files.readAllLines(gonderiDosyaYolu, StandardCharsets.UTF_8)) {
            line = line.trim();
            if (line.isEmpty()) continue;
            Gonderi g = gonderiSatirindanAyristir(line);
            if (g != null) gonderiler.add(g);
        }
        return gonderiler;
    }

    public void gonderileriDosyayaYaz() throws IOException {
        dizinOlustur(gonderiDosyaYolu);
        try (BufferedWriter w = Files.newBufferedWriter(gonderiDosyaYolu, StandardCharsets.UTF_8)) {
            for (Gonderi g : gonderiler) { w.write(g.toString()); w.newLine(); }
        }
    }

    public void gonderiKaydet(Gonderi gonderi) throws IOException {
        gonderilerDosyadanOku();
        gonderiler.add(gonderi);
        gonderileriDosyayaYaz();
    }

    // Beğeni kısmı

    public void begeniDosyadanOku() throws IOException {
        begeniler.clear();
        if (!Files.exists(begeniDosyaYolu)) return;
        for (String line : Files.readAllLines(begeniDosyaYolu, StandardCharsets.UTF_8)) {
            line = line.trim();
            if (!line.isEmpty()) begeniler.add(line);
        }
    }

    public void begeniDosyayaYaz() throws IOException {
        dizinOlustur(begeniDosyaYolu);
        try (BufferedWriter w = Files.newBufferedWriter(begeniDosyaYolu, StandardCharsets.UTF_8)) {
            for (String b : begeniler) { w.write(b); w.newLine(); }
        }
    }

    public boolean zatenBegenmis(String kullaniciAdi, int gonderiId) throws IOException {
        begeniDosyadanOku();
        return begeniler.contains(kullaniciAdi + "|" + gonderiId);
    }

    public void begeniEkle(String kullaniciAdi, int gonderiId) throws IOException {
        begeniDosyadanOku();
        String kayit = kullaniciAdi + "|" + gonderiId;
        if (begeniler.contains(kayit)) return;
        begeniler.add(kayit);
        begeniDosyayaYaz();
    }

    // Takip kısmı
    public void takiplerDosyadanOku() throws IOException {
        takipler.clear();
        if (!Files.exists(takipDosyaYolu)) return;
        for (String line : Files.readAllLines(takipDosyaYolu, StandardCharsets.UTF_8)) {
            line = line.trim();
            if (!line.isEmpty()) takipler.add(line);
        }
    }

    public void takiplerDosyayaYaz() throws IOException {
        dizinOlustur(takipDosyaYolu);
        try (BufferedWriter w = Files.newBufferedWriter(takipDosyaYolu, StandardCharsets.UTF_8)) {
            for (String t : takipler) { w.write(t); w.newLine(); }
        }
    }

    public void takipEt(String takipEden, String takipEdilen) throws IOException {
        takiplerDosyadanOku();
        String kayit = takipEden + "|" + takipEdilen;
        if (takipler.contains(kayit)) return;
        takipler.add(kayit);
        takiplerDosyayaYaz();
        kullanicilarDosyadanOku();
        for (Kullanici k : kullanicilar) {
            if (k.getKullaniciAdi().equals(takipEdilen)) {
                k.setTakipciSayisi(k.getTakipciSayisi() + 1);
                break;
            }
        }
        kullanicilariDosyayaYaz();
    }

    public void takibibirak(String takipEden, String takipEdilen) throws IOException {
        takiplerDosyadanOku();
        String kayit = takipEden + "|" + takipEdilen;
        if (!takipler.contains(kayit)) return;
        takipler.remove(kayit);
        takiplerDosyayaYaz();
        kullanicilarDosyadanOku();
        for (Kullanici k : kullanicilar) {
            if (k.getKullaniciAdi().equals(takipEdilen)) {
                k.setTakipciSayisi(Math.max(0, k.getTakipciSayisi() - 1));
                break;
            }
        }
        kullanicilariDosyayaYaz();
    }

    public boolean takipEdiyorMu(String takipEden, String takipEdilen) throws IOException {
        takiplerDosyadanOku();
        return takipler.contains(takipEden + "|" + takipEdilen);
    }

    /** Bir kullanicinin kac kisiyi takip ettigini dondurur */
    public int takipEdilenSayisi(String kullaniciAdi) throws IOException {
        takiplerDosyadanOku();
        int sayi = 0;
        for (String t : takipler) {
            String[] p = t.split("\\|", 2);
            if (p.length == 2 && p[0].equals(kullaniciAdi)) sayi++;
        }
        return sayi;
    }

    /** Bir kullanicinin kac takipcisi oldugunu dondurur */
    public int takipciSayisi(String kullaniciAdi) throws IOException {
        takiplerDosyadanOku();
        int sayi = 0;
        for (String t : takipler) {
            String[] p = t.split("\\|", 2);
            if (p.length == 2 && p[1].equals(kullaniciAdi)) sayi++;
        }
        return sayi;
    }

    // Yardimci metodlar
    public void hesapSil(String kullaniciAdi) throws IOException {
    kullanicilarDosyadanOku();
    kullanicilar.removeIf(k -> k.getKullaniciAdi().equals(kullaniciAdi));
    kullanicilariDosyayaYaz();

    // Kullanicinin gonderilerini sil
    gonderilerDosyadanOku();
    gonderiler.removeIf(g -> g.getKullaniciAdi().equals(kullaniciAdi));
    gonderileriDosyayaYaz();

    // Takip kayitlarini sil
    takiplerDosyadanOku();
    takipler.removeIf(t -> t.startsWith(kullaniciAdi + "|") || t.endsWith("|" + kullaniciAdi));
    takiplerDosyayaYaz();

    // Begeni kayitlarini sil
    begeniDosyadanOku();
    begeniler.removeIf(b -> b.startsWith(kullaniciAdi + "|"));
    begeniDosyayaYaz();
}

    private void dizinOlustur(Path dosya) throws IOException {
        Path parent = dosya.getParent();
        if (parent != null && !Files.exists(parent)) Files.createDirectories(parent);
    }

    private Gonderi gonderiSatirindanAyristir(String line) {
        String[] p = line.split("\\|", 6);
        try {
            if (p.length == 6) {
                Gonderi g = new Gonderi(Integer.parseInt(p[0]), p[1], p[2], p[3], Integer.parseInt(p[4]));
                yorumBolgesiniListeYap(g, p[5]);
                return g;
            }
            if (p.length == 5)
                return new Gonderi(Integer.parseInt(p[0]), p[1], p[2], p[3], Integer.parseInt(p[4]));
        } catch (NumberFormatException ignored) {}
        return null;
    }

    private void yorumBolgesiniListeYap(Gonderi hedef, String yorumlarMetni) {
        if (yorumlarMetni == null || yorumlarMetni.isEmpty()) return;
        ArrayList<Yorum> liste = new ArrayList<>();
        for (String blok : yorumlarMetni.split("~")) {
            if (blok.isEmpty()) continue;
            String[] yp = blok.split("\\^", 4);
            if (yp.length != 4) continue;
            try { liste.add(new Yorum(Integer.parseInt(yp[0]), yp[1], yp[2], yp[3])); }
            catch (NumberFormatException ignored) {}
        }
        hedef.setYorumlar(liste);
    }
}