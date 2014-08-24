/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package de.heckenmann.sudokucrack.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import de.heckenmann.sudokucrack.Spielbrett;

@ManagedBean
@SessionScoped
public class InputBean implements Serializable {

    private static final long serialVersionUID = 8991337861640011705L;

    private Spielbrett sb; // Das Spielbrett
    // private ArrayList<Spielbrett> oldSearches = new ArrayList<>(); // Alte Lösungen
    // private Spielbrett oldSelected; // Auswahl einer alten Lösung
    private String[][] input;
    private boolean ready;

    @PostConstruct
    public void init() {
        this.sb = new Spielbrett();
        this.input = new String[9][9];
        this.ready = true;
    }

    /*
     * Listener
     */
    // Beginnt mit der Suche
    public synchronized void startSearching(final ActionEvent ae) {
        this.ready = false;
        // Eingaben eintragen
        for (byte zeile = 0; zeile < 9; zeile++) {
            for (byte spalte = 0; spalte < 9; spalte++) {
                if (this.input[zeile][spalte] != null && !this.input[zeile][spalte].equals("")) {
                    this.sb.setWerteAt(zeile, spalte, Byte.parseByte(this.input[zeile][spalte]));
                }
            }
        }
        //Thread t = new Thread(sb);
        //t.start();
        this.sb.starteSuche();
    }

    // Erzeugt ein neues Spielfeld
    public void reset(final ActionEvent ae) {
        // oldSearches.add(sb);
        init();
    }

    //
    /*public void setSelected() {
        oldSearches.remove(oldSelected);
        oldSearches.add(this.sb);
        this.sb = oldSelected;
    }*/

    /*
     * GETTER & SETTER
     */
    public Spielbrett getSb() {
        return this.sb;
    }

    public void setSb(final Spielbrett sb) {
        this.sb = sb;
    }

    public synchronized String[][] getInput() {
        if (this.sb != null && !this.sb.isSuchend()) {
            for (byte zeile = 0; zeile < 9; zeile++) {
                for (byte spalte = 0; spalte < 9; spalte++) {
                    if (this.sb.getWerte()[zeile][spalte] != (byte) -1) {
                        this.input[zeile][spalte] = String.valueOf(this.sb.getWerte()[zeile][spalte]);
                    } else {
                        // input[zeile][spalte] = "";
                    }
                }
            }
            this.ready = true;
        }
        return this.input;
    }

    public synchronized void setInput(final String[][] input) {
        this.input = input;
    }

    public synchronized boolean isReady() {
        return this.ready;
    }
}
