/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author compu
 */
public class LogicaTablero {
    private Pieza[][] casillas = new Pieza[8][8];
    
    public LogicaTablero() {
        inicializar();
    }
    
    //métodos get
    public Pieza[][] getCasillas() {
        return casillas;
    }
    
    private void inicializar() {
        // Peones
        for (int i = 0; i < 8; i++) {
            casillas[1][i] = new Peon("negro", 1, i);
            casillas[6][i] = new Peon("blanco", 6, i);
        }
        // Torres
        casillas[0][0] = new Torre("negro", 0, 0);
        casillas[0][7] = new Torre("negro", 0, 7);
        casillas[7][0] = new Torre("blanco", 7, 0);
        casillas[7][7] = new Torre("blanco", 7, 7);
        // Caballos
        casillas[0][1] = new Caballo("negro", 0, 1);
        casillas[0][6] = new Caballo("negro", 0, 6);
        casillas[7][1] = new Caballo("blanco", 7, 1);
        casillas[7][6] = new Caballo("blanco", 7, 6);
        
        // Alfiles
        casillas[0][2] = new Alfil("negro", 0, 2);
        casillas[0][5] = new Alfil("negro", 0, 5);
        casillas[7][2] = new Alfil("blanco", 7, 2);
        casillas[7][5] = new Alfil("blanco", 7, 5);
        
        // Reinas
        casillas[0][3] = new Reina("negro", 0, 3);
        casillas[7][3] = new Reina("blanco", 7, 3);
        
        // Reyes
        casillas[0][4] = new Rey("negro", 0, 4);
        casillas[7][4] = new Rey("blanco", 7, 4);
    }
    
    //método para el movimiento de las piezas
public boolean mover(int fOrigen, int cOrigen, int fDestino, int cDestino) {
    Pieza pieza = casillas[fOrigen][cOrigen];

    // LLAMAR A LA FIRMA QUE USA ORIGEN + DESTINO
    if (pieza != null && pieza.movimientoValido(fOrigen, cOrigen, fDestino, cDestino, casillas)) {

        //detectar enroque (si la pieza es rey, mover también la torre)
        if (pieza instanceof Rey) {
            Rey rey = (Rey) pieza;

            //enroque corto
            if (cDestino == cOrigen + 2) {
                Pieza torre = casillas[fOrigen][7];
                if (torre != null) {
                    casillas[fOrigen][5] = torre;
                    casillas[fOrigen][7] = null;
                    torre.setPosicion(fOrigen, 5);
                    if (torre instanceof Torre) ((Torre) torre).setSeMovio(true);
                }
            }

            //enroque largo
            if (cDestino == cOrigen - 2) {
                Pieza torre = casillas[fOrigen][0];
                if (torre != null) {
                    casillas[fOrigen][3] = torre;
                    casillas[fOrigen][0] = null;
                    torre.setPosicion(fOrigen, 3);
                    if (torre instanceof Torre) ((Torre) torre).setSeMovio(true);
                }
            }

            rey.setSeMovio(true);
        }

        //movimiento normal de las piezas
        casillas[fDestino][cDestino] = pieza;
        casillas[fOrigen][cOrigen] = null;
        pieza.setPosicion(fDestino, cDestino);

        //si es torre, marcar el seMovio
        if (pieza instanceof Torre) {
            ((Torre) pieza).setSeMovio(true);
        }

        return true;
    }
    return false;
}
}
