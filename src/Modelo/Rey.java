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
    if (!puedeIniciarEnroque(fOrigen, fDestino)) return false;

    //enroque corto
    if (cDestino == cOrigen + 2) {
        return puedeEnrocarCorto(fOrigen, tablero);
    }

    //enroque largo
    if (cDestino == cOrigen - 2) {
        return puedeEnrocarLargo(fOrigen, tablero);
    }

    return false;
}

    private boolean puedeIniciarEnroque(int filaOrigen, int filaDestino) {
        return !seMovio && filaOrigen == filaDestino;
    }

    private boolean puedeEnrocarCorto(int fila, Pieza[][] tablero) {
        return torrePuedeEnrocar(tablero[fila][7])
                && casillasVacias(tablero, fila, 5, 6);
    }

    private boolean puedeEnrocarLargo(int fila, Pieza[][] tablero) {
        return torrePuedeEnrocar(tablero[fila][0])
                && casillasVacias(tablero, fila, 1, 2, 3);
    }

    private boolean torrePuedeEnrocar(Pieza pieza) {
        return pieza instanceof Torre torre && !torre.isSeMovio();
    }

    private boolean casillasVacias(Pieza[][] tablero, int fila, int... columnas) {
        for (int columna : columnas) {
            if (tablero[fila][columna] != null) {
                return false;
            }
        }
        return true;
    }
}
