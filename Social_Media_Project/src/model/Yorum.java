package model;

public class Yorum {
    private int yorumId;
    private String kullaniciAdi;
    private String icerik;
    private String tarih;

    public Yorum(int yorumId, String kullaniciAdi, String icerik, String tarih) {
        this.yorumId = yorumId;
        this.kullaniciAdi = kullaniciAdi;
        this.icerik = icerik;
        this.tarih = tarih;
    }

    public int getYorumId() {
        return yorumId;
    }

    public void setYorumId(int yorumId) {
        this.yorumId = yorumId;
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
}
