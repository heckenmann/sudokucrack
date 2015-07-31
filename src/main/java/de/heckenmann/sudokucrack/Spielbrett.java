package de.heckenmann.sudokucrack;

import java.io.Serializable;

/**
 * Diese Klasse löst ein Sudoku-Rätsel. 
 */
public class Spielbrett implements Serializable, Runnable {

    private static final long serialVersionUID = 1L;

    private byte[][] werte; // werte[zeile][spalte]; Die Werte, die die Positionen momentan haben; Nicht belegte Positionen haben den Wert -1; Datentyp byte, um möglichst wenig Aufwand zu betreiben und die größt mögliche Performance zu erreichen
    private boolean[][] moeglichkeitenZeile; // moeglichkeitenZeile[zeile][wert] besagt, dass in der Zeile der Wert noch möglich ist
    private boolean[][] moeglichkeitenSpalte; // Siehe Spalte
    private boolean[][][] moeglichkeitenQuadrat; // [3][3][9] ==> [zeile][spalte][wert]
    private boolean suchend; // Gibt an, ob die Suche noch läuft.
    private boolean erfolgreich; // Gibt an, ob eine Lösung gefunden wurde

    /**
     * Konstruktur
     */
    public Spielbrett() {
        init();
    }

    /*
     * METHODEN
     */
    /**
     * Mit init() kann das Spielbrett auch zurückgesetzt werden.
     */
    public void init() {
        this.suchend = false; // Anfangs wird nicht nach einer Lösung gesucht
        this.erfolgreich = false; // Es wurde noch keine Lösung gefunden
        this.werte = new byte[9][9]; // Größe des Spielfelds festlegen
        this.moeglichkeitenZeile = new boolean[9][9];
        this.moeglichkeitenSpalte = new boolean[9][9];
        this.moeglichkeitenQuadrat = new boolean[3][3][9];
        // Möglichkeiten setzen
        for (int zeile = 0; zeile < 9; zeile++) {
            for (int spalte = 0; spalte < 9; spalte++) {
                for (int wert = 1; wert <= 9; wert++) {
                    this.moeglichkeitenZeile[zeile][wert - 1] = true; // Unsauber programmiert, da in jedem Schleifendurchlauf der Wert überschrieben wird. Ist jedoch lesbarer.
                    this.moeglichkeitenSpalte[spalte][wert - 1] = true; // " "
                    this.werte[zeile][spalte] = -1; // Auf jede Position -1 schreiben, weil noch kein Wert eingetragen wurde
                }
            }
        }
        // Möglichkeiten für 3x3 Quadrate setzen
        for (byte zeile = 0; zeile < 3; zeile++) {
            for (byte spalte = 0; spalte < 3; spalte++) {
                for (byte wert = 1; wert <= 9; wert++) {
                    this.moeglichkeitenQuadrat[zeile][spalte][wert - 1] = true;
                }
            }
        }
    }

    /**
     * Beginnt die Suche nach einer Lösung.
     */
    public void starteSuche() {
        setSuchend(true); // Ab jetzt befindet sich das Model im Status "suchend"
        this.erfolgreich = sucheBelegung((byte) 0, (byte) 0); // Die Suche beginnt in Zeile 0 und Spalte 0
        setSuchend(false); // Die Suche ist abgeschlossen
    }

    /**
     * Setzt den Wert für eine Position.
     */
    public boolean setWerteAt(final byte zeile, final byte spalte, final byte wert) {
        // Prüfen, ob die Position korrekt ist
        if (zeile < 0 || zeile > 9 || spalte < 0 || spalte > 9) {
            return false;
        }
        // Wert überprüfen
        final byte alterWert = this.werte[zeile][spalte]; // Alten Werte merken, um die Möglichkeiten später richtig setzen zu können
        if (wert == (byte) -1 || (wert >= 1 && wert <= 9 && istWertFuerPositionMoeglich(zeile, spalte, wert))) {
            this.werte[zeile][spalte] = wert;
            setzeMoeglichkeiten(zeile, spalte, wert, false); // Möglichkeit für den neuen Wert entfernen
            setzeMoeglichkeiten(zeile, spalte, alterWert, true); // Der alte Wert ist wieder möglich
            return true;
        }
        return false; // Der Wert wurde nicht geändert
    }

    /**
     * Entfernt die Möglichkeiten für eine Position, der Spalte, der Zeile und des Quadrats.
     */
    private void setzeMoeglichkeiten(final byte zeile, final byte spalte, final byte wert, final boolean moeglich) {
        if (wert < 1) {
            return;
        }
        // 3x3 Quadrat berechnen
        final byte zeileStart = (byte) (zeile / 3);
        final byte spalteStart = (byte) (spalte / 3);
        // Möglichkeiten ändern
        this.moeglichkeitenZeile[zeile][wert - 1] = moeglich;
        this.moeglichkeitenSpalte[spalte][wert - 1] = moeglich;
        this.moeglichkeitenQuadrat[zeileStart][spalteStart][wert - 1] = moeglich;
    }

    /**
     * Prüft, ob ein Wert für eine Position möglich ist.
     */
    private boolean istWertFuerPositionMoeglich(final byte zeile, final byte spalte, final byte wert) {
        if (wert < 1) {
            return true;
        }
        // 3x3 Quadrat berechnen
        final byte zeileStart = (byte) (zeile / 3);
        final byte spalteStart = (byte) (spalte / 3);

        // Prüfen
        return this.moeglichkeitenZeile[zeile][wert - 1] && this.moeglichkeitenSpalte[spalte][wert - 1]
                && this.moeglichkeitenQuadrat[zeileStart][spalteStart][wert - 1];
    }

    /**
     * Sucht eine Belegung für das Spielbrett; Die Parameter sind die Position, an der die Rekursion beginnt.
     */
    private boolean sucheBelegung(final byte zeile, final byte spalte) {
        final byte neueZeile = (byte) (zeile == 8 ? 0 : zeile + 1);
        final byte neueSpalte = (byte) (neueZeile == 0 ? spalte + 1 : spalte);
        // Falls die letzte Position erreicht wurde, wird abgebrochen
        if (spalte == 9) {
            return pruefeSpielbrett(); // Könnte man sich eigentlich sparen
        }
        // Falls die Position schon beschrieben ist, wird sie übersprungen
        if (this.werte[zeile][spalte] != (byte) -1) {
            return sucheBelegung(neueZeile, neueSpalte); // Direkt zum nächsten Methodenaufruf springen
        }
        // Alle Möglichkeiten durchprobieren
        for (byte moeglichkeit = 1; moeglichkeit <= 9; moeglichkeit++) {
            if (istWertFuerPositionMoeglich(zeile, spalte, moeglichkeit)) {
                setWerteAt(zeile, spalte, moeglichkeit); // Die nächste Position wird durchprobiert
                if (sucheBelegung(neueZeile, neueSpalte)) {
                    return true;
                }
                setWerteAt(zeile, spalte, (byte) -1); // Wert wieder entfernen
            }
        }
        return false; // Gefundene Belegung war nicht korrekt
    }

    /**
     * Zum Prüfen, ob das Spielbrett korrekt ausgefüllt wurde, muss nur noch geprüft werden, ob sich keine leeren Positionen mehr auf dem Spielbrett befinden.
     */
    private boolean pruefeSpielbrett() {
        for (byte zeile = 0; zeile < 9; zeile++) {
            for (byte spalte = 0; spalte < 9; spalte++) {
                if (this.werte[zeile][spalte] == -1) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Da die Berechnung länger andauern kann, ist es sinnvoll sie in einen weiteren Thread auszulagern.
     * Für JavaEE-Umgebung eher ungeeignet!
     */
    @Override
    public void run() {
        starteSuche();
    }

    /*
     * GETTER & SETTER
     */
    public byte[][] getWerte() {
        return this.werte;
    }

    public void setWerte(final byte[][] werte) {
        this.werte = werte;
    }

    public boolean isSuchend() {
        return this.suchend;
    }

    private void setSuchend(final boolean suchend) {
        this.suchend = suchend;
    }

    public boolean isErfolgreich() {
        return this.erfolgreich;
    }
}
