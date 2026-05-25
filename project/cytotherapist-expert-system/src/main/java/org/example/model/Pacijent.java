package org.example.model;

import java.util.List;

public class Pacijent {
    private int starost;
    private double temperatura;
    private String pritisak; // Format "gornji/donji" npr "120/80"
    private boolean zaliSeNaBolove;
    private boolean zaliSeNaTegobe;

    // Kategorizacije koje pravila postavljaju (inzertuju/menjaju)
    private String kategorijaTemperature = "";
    private String kategorijaPritiska = "";

    // Getteri i Setteri
    public int getStarost() { return starost; }
    public void setStarost(int starost) { this.starost = starost; }
    public double getTemperatura() { return temperatura; }
    public void setTemperatura(double temperatura) { this.temperatura = temperatura; }
    public String getPritisak() { return pritisak; }
    public void setPritisak(String pritisak) { this.pritisak = pritisak; }
    public boolean isZaliSeNaBolove() { return zaliSeNaBolove; }
    public void setZaliSeNaBolove(boolean zaliSeNaBolove) { this.zaliSeNaBolove = zaliSeNaBolove; }
    public boolean isZaliSeNaTegobe() { return zaliSeNaTegobe; }
    public void setZaliSeNaTegobe(boolean zaliSeNaTegobe) { this.zaliSeNaTegobe = zaliSeNaTegobe; }
    public String getKategorijaTemperature() { return kategorijaTemperature; }
    public void setKategorijaTemperature(String kategorijaTemperature) { this.kategorijaTemperature = kategorijaTemperature; }
    public String getKategorijaPritiska() { return kategorijaPritiska; }
    public void setKategorijaPritiska(String kategorijaPritiska) { this.kategorijaPritiska = kategorijaPritiska; }

    // Pomoćne metode za parsiranje pritiska radi lakšeg pisanja pravila
    public int getGornjiPritisak() {
        return Integer.parseInt(pritisak.split("/")[0]);
    }
    public int getDonjiPritisak() {
        return Integer.parseInt(pritisak.split("/")[1]);
    }
}