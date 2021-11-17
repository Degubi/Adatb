package degubi;

import static degubi.mapping.ObjectMapper.*;
import static org.junit.jupiter.api.Assertions.*;

import degubi.model.*;
import org.junit.jupiter.api.*;

public class SQLTests {

    @Test
    public void testTantargyQueries() {
        var obj = new Tantargy(10, "Magyar Irodalom");

        assertEquals("SELECT * FROM tantargy", createMapper(Tantargy.class).listAllQuery);
        assertEquals("INSERT INTO tantargy (azonosito, nev) VALUES (NULL, 'Magyar Irodalom')", generateInsertQuery(obj));
        assertEquals("DELETE FROM tantargy WHERE azonosito = 10", generateDeleteQuery(obj));
        assertEquals("UPDATE tantargy SET nev = 'Angol Irodalom' WHERE azonosito = 10", generateUpdateQuery(obj, new Tantargy(obj.azonosito, "Angol Irodalom")));
    }

    @Test
    public void testKepzettsegQueries() {
        var obj = new Kepzettseg(42, "Lakatos");

        assertEquals("SELECT * FROM kepzettseg", createMapper(Kepzettseg.class).listAllQuery);
        assertEquals("INSERT INTO kepzettseg (azonosito, megnevezes) VALUES (NULL, 'Lakatos')", generateInsertQuery(obj));
        assertEquals("DELETE FROM kepzettseg WHERE azonosito = 42", generateDeleteQuery(obj));
        assertEquals("UPDATE kepzettseg SET megnevezes = 'Magyartanár' WHERE azonosito = 42", generateUpdateQuery(obj, new Kepzettseg(obj.azonosito, "Magyartanár")));
    }

    @Test
    public void testTeremQueries() {
        var obj = new Terem(13, 12, 11, 110, true);

        assertEquals("SELECT * FROM terem", createMapper(Terem.class).listAllQuery);
        assertEquals("INSERT INTO terem (azonosito, teremSzam, epuletSzam, ferohelyekSzama, vanEProjektor) VALUES (NULL, 12, 11, 110, 1)", generateInsertQuery(obj));
        assertEquals("DELETE FROM terem WHERE azonosito = 13", generateDeleteQuery(obj));
        assertEquals("UPDATE terem SET teremSzam = 16, epuletSzam = 22, ferohelyekSzama = 102, vanEProjektor = 0 WHERE azonosito = 13", generateUpdateQuery(obj, new Terem(obj.azonosito, 16, 22, 102, false)));
    }

    @Test
    public void testOsztalyQueries() {
        var obj = new Osztaly(42, "12. C");

        assertEquals("SELECT * FROM osztaly", createMapper(Osztaly.class).listAllQuery);
        assertEquals("INSERT INTO osztaly (azonosito, megnevezes) VALUES (NULL, '12. C')", generateInsertQuery(obj));
        assertEquals("DELETE FROM osztaly WHERE azonosito = 42", generateDeleteQuery(obj));
        assertEquals("UPDATE osztaly SET megnevezes = '11. C' WHERE azonosito = 42", generateUpdateQuery(obj, new Osztaly(obj.azonosito, "11. C")));
    }


    @Test
    public void testTanarQueries() {
        var obj = new Tanar("ABC123", "Mézga Géza", new Kepzettseg(13, "Lakatos"));

        assertEquals("SELECT tanar.*, kepzettseg.* FROM tanar INNER JOIN kepzettseg ON tanar.kepzettsegAzonosito = kepzettseg.azonosito", createMapper(Tanar.class).listAllQuery);
        assertEquals("INSERT INTO tanar (szemelyiSzam, nev, kepzettsegAzonosito) VALUES ('ABC123', 'Mézga Géza', 13)", generateInsertQuery(obj));
        assertEquals("DELETE FROM tanar WHERE szemelyiSzam = 'ABC123'", generateDeleteQuery(obj));
        assertEquals("UPDATE tanar SET nev = 'Fehér Béla', kepzettsegAzonosito = 69 WHERE szemelyiSzam = 'ABC123'", generateUpdateQuery(obj, new Tanar(obj.szemelyiSzam, "Fehér Béla", new Kepzettseg(69, "Takarító"))));
    }

    @Test
    public void testDiakQueries() {
        var obj = new Diak("MAMA123", new Osztaly(13, "9. C"), "Albert De Nem Einstein");

        assertEquals("SELECT diak.*, osztaly.* FROM diak INNER JOIN osztaly ON diak.osztalyAzonosito = osztaly.azonosito", createMapper(Diak.class).listAllQuery);
        assertEquals("INSERT INTO diak (neptunKod, osztalyAzonosito, nev) VALUES ('MAMA123', 13, 'Albert De Nem Einstein')", generateInsertQuery(obj));
        assertEquals("DELETE FROM diak WHERE neptunKod = 'MAMA123'", generateDeleteQuery(obj));
        assertEquals("UPDATE diak SET osztalyAzonosito = 14, nev = 'Idc' WHERE neptunKod = 'MAMA123'", generateUpdateQuery(obj, new Diak("MAMA123", new Osztaly(14, "10. B"), "Idc")));
    }


    @Test
    public void testOraQueries() {
        var tantargy = new Tantargy(10, "Magyar Irodalom");
        var tanar = new Tanar("ABC123", "Mézga Géza", new Kepzettseg(13, "Lakatos"));
        var osztaly = new Osztaly(42, "12. C");
        var terem = new Terem(13, 12, 11, 110, true);
        var obj = new Ora(3, 2, "15:20", tantargy, tanar, osztaly, terem);

        assertEquals("SELECT ora.*, tantargy.*, tanar.*, osztaly.*, terem.* FROM ora INNER JOIN tantargy ON ora.tantargyAzonosito = tantargy.azonosito INNER JOIN tanar ON ora.tanarSzemelyiSzam = tanar.szemelyiSzam INNER JOIN osztaly ON ora.osztalyAzonosito = osztaly.azonosito INNER JOIN terem ON ora.teremAzonosito = terem.azonosito", createMapper(Ora.class).listAllQuery);
        assertEquals("INSERT INTO ora (azonosito, napIndex, idopont, tantargyAzonosito, tanarSzemelyiSzam, osztalyAzonosito, teremAzonosito) VALUES (NULL, 2, '15:20', 10, 'ABC123', 42, 13)", generateInsertQuery(obj));
        assertEquals("DELETE FROM ora WHERE azonosito = 3", generateDeleteQuery(obj));
        assertEquals("UPDATE ora SET napIndex = 2, idopont = '15:20', tantargyAzonosito = 10, tanarSzemelyiSzam = 'ABC123', osztalyAzonosito = 42, teremAzonosito = 13 WHERE azonosito = 3", generateUpdateQuery(obj, obj));
    }
}