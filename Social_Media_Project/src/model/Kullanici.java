package model;

public class Kullanici {
    private String kullaniciAdi;
    private String sifre;
    private String email;
    private String biyografi;
    private int takipciSayisi;

    public Kullanici(String kullaniciAdi, String sifre, String email, String biyografi, int takipciSayisi) {
        this.kullaniciAdi = kullaniciAdi;
        this.sifre = sifre;
        this.email = email;
        this.biyografi = biyografi;
        this.takipciSayisi = takipciSayisi;
    }

    public String getKullaniciAdi() {
        return kullaniciAdi;
    }

    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
    }

    public String getSifre() {
        return sifre;
    }

    public void setSifre(String sifre) {
        this.sifre = sifre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBiyografi() {
        return biyografi;
    }

    public void setBiyografi(String biyografi) {
        this.biyografi = biyografi;
    }

    public int getTakipciSayisi() {
        return takipciSayisi;
    }

    public void setTakipciSayisi(int takipciSayisi) {
        this.takipciSayisi = takipciSayisi;
    }

    
    @Override
    public String toString() {
        return kullaniciAdi + "|" + sifre + "|" + email + "|" + biyografi + "|" + takipciSayisi;
    }
}
