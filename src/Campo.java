import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Campo extends JPanel {

    private Cella[][] campo;

    private final int mine;
    private int nBandiere = 0;
    private final int MINA = -1;

    private boolean primoClick = true;

    public Campo(int righe, int colonne, int maxMine, Container content) {

        setPreferredSize(new Dimension(450,300));
        campo = new Cella[righe][colonne];
        this.mine = maxMine;

        // gestire input delle celle
        for (int r=0; r < campo.length; r++) {
            for (int c=0; c < campo[0].length; c++) {

                // istanzare la cella e posizionarla a schermo
                campo[r][c] = new Cella(30, r, c);
                content.add(campo[r][c]);
                campo[r][c].setLocation(campo[r][c].getWidth() * c, campo[r][c].getHeight() * r); 

                // deriviamo una classe anonima
                campo[r][c].addMouseListener(new MouseAdapter() {
                    
                    // MouseListener interfaccia -> MouseAdapter classe astratta
                    // usiamo una classe astratta perché dobbiamo implementare troppi metodi astratti
                    // NON è possibile usare una lambda function! non saprebbe quale metodo stiamo richiamando

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        
                        // recupero le informazioni della cella cliccata
                        Cella cliccata = (Cella)e.getSource();

                        // button1: pulsante sx
                        // button2: tasto centrale/rotellina
                        // button3: pulsante dx
                        switch (e.getButton()) {
                            case MouseEvent.BUTTON1:

                                // verifico che il primo click sia sempre valido, e rigenero le mine
                                while (primoClick && campo[cliccata.getR()][cliccata.getC()].getContenuto() == MINA) {  
                                    generaMine();
                                }

                                // ricalcolo gli indizi
                                if (primoClick) {
                                    contaIndizi();

                                    primoClick = false;
                                }

                                // scopre la singola cella (o l'area vuota)
                                scopriCella(cliccata.getR(), cliccata.getC());

                                // controllo se ho vinto
                                if (checkVittoria()) {
                                    JOptionPane.showMessageDialog(null, "HAI VINTO!");
                                    return;
                                }
                                break;

                            case MouseEvent.BUTTON3:
                                if (cliccata.isScoperta()) break;    

                                // se ho esaurito le bandierine
                                if (!cliccata.isBandiera() && nBandiere == mine) {
                                    JOptionPane.showMessageDialog(null, "Hai usato tutte le bandiere!");
                                    break;
                                }
                                
                                cliccata.toggleBandiera();

                                // se sono bandiera, incremento - se coperta, decremento
                                nBandiere += cliccata.isBandiera() ? +1 : -1;
                                break;
                        }

                        //super.mouseReleased(e);
                    }
                });
            }
        }

        // generare la posizione delle mine
        generaMine();

        // impostare gli indizi
        contaIndizi();
    }

    /**
     * Posiziona le mine casualmente nelle celle
     */
    private void generaMine() {

        // svuoto le vecchie mine
        for (int r=0; r < campo.length; r++) {
            for (int c=0; c < campo[0].length; c++) {
                campo[r][c].setContenuto(0);
            }
        }

        int mineInserite = 0;

        Random rand = new Random();

        while (mineInserite < mine) {

            // randomizzo coordinate della cella
            int r = rand.nextInt(campo.length);
            int c = rand.nextInt(campo[0].length); 

            if (campo[r][c].getContenuto() != MINA) {
                campo[r][c].setContenuto(MINA);
                mineInserite++;
            }
        }
    }

    /**
     * Analizza le celle adiacenti contando le mine, e imposta gli indizi numerici
     */
    private void contaIndizi() {

        int mineContate;

        for (int r=0; r < campo.length; r++)
            for (int c = 0; c < campo[0].length; c++) {
                
                mineContate = 0;

                // se è una mina, la salto
                if (campo[r][c].getContenuto() == MINA) continue;

                // scorro le adiacenze 3x3
                for (int riga = r-1; riga <= r+1; riga++) {
                    for (int col = c-1; col <= c+1; col++) {
                        
                        // controllare la validità delle celle adiacenti
                        try {
                            if (campo[riga][col].getContenuto() == MINA)
                                mineContate++;
                        }
                        catch (ArrayIndexOutOfBoundsException e) {/* salto la cella */}
                    } 
                }

                campo[r][c].setContenuto(mineContate);
            }
    }

    /**
     * Analizza e scopre le celle adiacenti se vuote
     * @param r riga della cella partenza
     * @param c colonna della cella di partenza
     */
    public void scopriCella(int r, int c) {

        if (campo[r][c].isBandiera()) return;

        // caso base
        campo[r][c].setVisibile(true);

        // controllo se ho perso
        if (campo[r][c].getContenuto() == MINA) {
            JOptionPane.showMessageDialog(null, "Game over!");
            System.exit(0); 
        }

        // clausola di chiusura
        if (campo[r][c].getContenuto() > 0)
            return;

        // chiamata ricorsiva
        for (int riga = r-1; riga <= r+1; riga++) {
            for (int col = c-1; col <= c+1; col++) {

                // salto la cella attuale
                if (riga == r && col == c) continue;

                // controllo se le celle adiacenti sono coperte
                try {
                    if (!campo[riga][col].isScoperta())
                        scopriCella(riga, col);      
                }
                catch (ArrayIndexOutOfBoundsException e) {/* salto la cella */}
            }
        }
    }

    private boolean checkVittoria() {

        boolean win = true;

        // label, identifica il ciclo più esterno
        ciclo:
        for (int r = 0; r < campo.length; r++) 
            for (int c = 0; c < campo[0].length; c++) {
                
                if (campo[r][c].getContenuto() != MINA && !campo[r][c].isScoperta()) {

                    win = false;
                    break ciclo;
                }
            }
            
        return win;
    }
}
