package org.example.model;

public class SistemStatus {
    private int brojPacijenataNaPrijemu;
    private int brojSestaraPacijenti;
    private int brojSestaraPapirologija;
    private boolean radSaPacijentima; // TRUE = pacijenti, FALSE = papirologija

    // Getteri i Setteri
    public int getBrojPacijenataNaPrijemu() { return brojPacijenataNaPrijemu; }
    public void setBrojPacijenataNaPrijemu(int br) { this.brojPacijenataNaPrijemu = br; }
    public int getBrojSestaraPacijenti() { return brojSestaraPacijenti; }
    public void setBrojSestaraPacijenti(int br) { this.brojSestaraPacijenti = br; }
    public int getBrojSestaraPapirologija() { return brojSestaraPapirologija; }
    public void setBrojSestaraPapirologija(int br) { this.brojSestaraPapirologija = br; }
    public boolean isRadSaPacijentima() { return radSaPacijentima; }
    public void setRadSaPacijentima(boolean rad) { this.radSaPacijentima = rad; }
}