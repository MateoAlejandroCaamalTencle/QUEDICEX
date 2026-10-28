/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author compu
 */
public class Rey extends Pieza {
    
    private boolean seMovio = false;
    
    //constructor

    public Rey(String color, int fila, int columna) {
        super(color, fila, columna);
    }
    
    public boolean isSeMovio() {
        return seMovio;
    }
    
    public void setSeMovio(boolean seMovio) {
        this.seMovio = seMovio;
    }
    
    //método polimorfico

    @Override
    public boolean movimientoValido(int fOrigen, int cOrigen, int fDestino, int cDestino, Pieza[][] tablero) {

    int df = Math.abs(fDestino - fOrigen);
    int dc = Math.abs(cDestino - cOrigen);

    //movimiento normal del rey
    if (df <= 1 && dc <= 1) {
        return tablero[fDestino][cDestino] == null ||
               !tablero[fDestino][cDestino].getColor().equals(this.color);
    }

    //movimiento especial, enroque
    if (seMovio || fDestino != fOrigen) return false;

    //enroque corto
    if (cDestino == cOrigen + 2) {
        Pieza torre = tablero[fOrigen][7];

        if (torre instanceof Torre && !((Torre) torre).isSeMovio()) {
            if (tablero[fOrigen][5] == null && tablero[fOrigen][6] == null) {
                return true;
            }
        }
    }

    //enroque largo
    if (cDestino == cOrigen - 2) {
        Pieza torre = tablero[fOrigen][0];

        if (torre instanceof Torre && !((Torre) torre).isSeMovio()) {
            if (tablero[fOrigen][1] == null &&
                tablero[fOrigen][2] == null &&
                tablero[fOrigen][3] == null) {
                return true;
            }
        }
    }

    return false;
}
}
