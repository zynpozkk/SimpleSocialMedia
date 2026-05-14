package model;

import java.util.ArrayList;

public class Gonderi {
    private int gonderiId;
    private String kullaniciAdi;
    private String icerik;
    private String tarih;
    private int beğeniSayisi;
    private ArrayList<Yorum> yorumlar;

    public Gonderi(int gonderiId, String kullaniciAdi, String icerik, String tarih, int beğeniSayisi) {
        this.gonderiId = gonderiId;
        this.kullaniciAdi = kullaniciAdi;
        this.icerik = icerik;
        this.tarih = tarih;
        this.beğeniSayisi = beğeniSayisi;
        this.yorumlar = new ArrayList<>();
    }

    public int getGonderiId() {
        return gonderiId;
    }

    public void setGonderiId(int gonderiId) {
        this.gonderiId = gonderiId;
    }

    public String getKullaniciAdi() {
        return kullaniciAdi;
    }

    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
    }

    public String getIcerik() {
        return icerik;
    }

    public void setIcerik(String icerik) {
        this.icerik = icerik;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public int getBeğeniSayisi() {
        return beğeniSayisi;
    }

    public void setBeğeniSayisi(int beğeniSayisi) {
        this.beğeniSayisi = beğeniSayisi;
    }

    public ArrayList<Yorum> getYorumlar() {
        return yorumlar;
    }

    public void setYorumlar(ArrayList<Yorum> yorumlar) {
        this.yorumlar = yorumlar;
    }

    /**
     * Dosyaya yazım için: gönderi alanları | ile; her yorum yorumId^yazar^metin^tarih olarak,
     * yorumlar arasında ~ ile ayrılır.
     */
    @Override
    public String toString() {
        StringBuilder yorumParcalari = new StringBuilder();
        for (int i = 0; i < yorumlar.size(); i++) {
            if (i > 0) {
                yorumParcalari.append("~");
            }
            Yorum y = yorumlar.get(i);
            yorumParcalari.append(y.getYorumId()).append("^");
            yorumParcalari.append(y.getKullaniciAdi()).append("^");
            yorumParcalari.append(y.getIcerik()).append("^");
            yorumParcalari.append(y.getTarih());
        }
        return gonderiId + "|" + kullaniciAdi + "|" + icerik + "|" + tarih + "|" + beğeniSayisi + "|" + yorumParcalari;
    }
}
