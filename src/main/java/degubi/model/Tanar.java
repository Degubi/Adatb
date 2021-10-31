package degubi.model;

public final class Tanar {
    public final String szemelyiSzam;
    public final String nev;
    public final String kepzettseg;

    public Tanar(String szemelyiSzam, String nev, String kepzettseg) {
        this.szemelyiSzam = szemelyiSzam;
        this.nev = nev;
        this.kepzettseg = kepzettseg;
    }

    //FX-nek getterek
    public String getSzemelyiSzam() { return szemelyiSzam; }
    public String getNev() { return nev; }
    public String getKepzettseg() { return kepzettseg; }
}