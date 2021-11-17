package degubi;

import static degubi.mapping.ObjectMapper.*;
import static org.junit.jupiter.api.Assertions.*;

import degubi.model.*;
import java.util.*;
import org.junit.jupiter.api.*;

public class CreateInstanceTests {

    @Test
    public void testTantargyInstanceCreation() {
        var obj = createInstance(Map.of("azonosito", 13, "nev", "Irodalom"), Tantargy.class);

        assertEquals(13, obj.azonosito);
        assertEquals("Irodalom", obj.nev);
    }

    @Test
    public void testKepzettsegInstanceCreation() {
        var obj = createInstance(Map.of("azonosito", 12, "megnevezes", "Lakatos"), Kepzettseg.class);

        assertEquals(12, obj.azonosito);
        assertEquals("Lakatos", obj.megnevezes);
    }

    @Test
    public void testTeremInstanceCreation() {
        var obj = createInstance(Map.of("azonosito", 3, "teremSzam", 4, "epuletSzam", 5, "ferohelyekSzama", 42, "vanEProjektor", true), Terem.class);

        assertEquals(3, obj.azonosito);
        assertEquals(4, obj.teremSzam);
        assertEquals(5, obj.epuletSzam);
        assertEquals(42, obj.ferohelyekSzama);
        assertTrue(obj.vanEProjektor);
    }

    @Test
    public void testOsztalyInstanceCreation() {
        var obj = createInstance(Map.of("azonosito", 15, "megnevezes", "12. C"), Osztaly.class);

        assertEquals(15, obj.azonosito);
        assertEquals("12. C", obj.megnevezes);
    }


    @Test
    public void testTanarFullInstanceCreation() {
        var obj = createInstance(Map.of("szemelyiSzam", "ABC123", "nev", "Margit",
                                        "kepzettseg.azonosito", 15, "kepzettseg.megnevezes", "Nem Lakatos"), Tanar.class);

        assertEquals("ABC123", obj.szemelyiSzam);
        assertEquals("Margit", obj.nev);
        assertEquals(15, obj.kepzettseg.azonosito);
        assertEquals("Nem Lakatos", obj.kepzettseg.megnevezes);
    }

    @Test
    public void testTanarPartialInstanceCreation() {
        var obj = createInstance(Map.of("szemelyiSzam", "ABC123", "nev", "Margit"), Tanar.class);

        assertEquals("ABC123", obj.szemelyiSzam);
        assertEquals("Margit", obj.nev);
        assertNull(obj.kepzettseg);
    }

    @Test
    public void testDiakFullInstanceCreation() {
        var obj = createInstance(Map.of("neptunKod", "HAHA123", "nev", "Béla",
                                        "osztaly.azonosito", 13, "osztaly.megnevezes", "11. E"), Diak.class);

        assertEquals("HAHA123", obj.neptunKod);
        assertEquals("Béla", obj.nev);
        assertEquals(13, obj.osztaly.azonosito);
        assertEquals("11. E", obj.osztaly.megnevezes);
    }

    @Test
    public void testDiakPartialInstanceCreation() {
        var obj = createInstance(Map.of("neptunKod", "HAHA123", "nev", "Béla"), Diak.class);

        assertEquals("HAHA123", obj.neptunKod);
        assertEquals("Béla", obj.nev);
        assertNull(obj.osztaly);
    }
}