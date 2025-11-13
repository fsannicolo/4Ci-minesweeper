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

                campo[r][c].addMouseListener(new MouseAdapter() {
                    
                    // usiamo una classe anonima perché dobbiamo implementare diversi metodi astratti
                    // NON è possibile usare una lambda function!!

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        
                        Cella cliccata = (Cella)e.getSource();

                        // button1: pulsante sx
                        // button2: tasto centrale/rotellina
                        // button3: pulsante dx
                        switch (e.getButton()) {
                            case MouseEvent.BUTTON1:
                                // scopre la singola cella (o l'area vuota)
                                scopriCella(cliccata.getR(), cliccata.getC());
                                break;

                            case MouseEvent.BUTTON3:
                                if (cliccata.isScoperta()) break;    

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

        // imposta gli indizi
        contaIndizi();
    }

    /**
     * Posizionare le mine casualmente nelle celle
     */
    private void generaMine() {

        Random rand = new Random();
    }

    /**
     * Analizzare le celle adiacenti contando le mine, e imposta gli indizi numerici
     */
    private void contaIndizi() {

    }

    /**
     * Analizza e scopre le celle adiacenti se vuote
     * @param r riga della cella partenza
     * @param c colonna della cella di partenza
     */
    public void scopriCella(int r, int c) {

        // va fatta ricorsiva!!!
    }
}
