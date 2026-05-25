package org.example.model;

// Koristi se za flagovanje specifičnih stanja ili naredbi u sistemu
public class StatusPoruka {
    private String poruka;

    public StatusPoruka(String poruka) { this.poruka = poruka; }
    public String getPoruka() { return poruka; }
    public void setPoruka(String poruka) { this.poruka = poruka; }
}