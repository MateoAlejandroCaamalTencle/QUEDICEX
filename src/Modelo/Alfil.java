/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author compu
 */
public class Alfil extends Pieza {
    
    //constructor

    public Alfil(String color, int fila, int columna) {
        super(color, fila, columna);
    }
    
    //método polimorfico

    @Override
    public boolean movimientoValido(int fOrigen, int cOrigen, int fDestino, int cDestino, Pieza[][] tablero) {

        //validar los limites que tiene el tablero
        if (fDestino < 0 || fDestino >= tablero.length ||
            cDestino < 0 || cDestino >= tablero[0].length) {
            return false;
        }

        //no se pueden capturar piezas propias
        if (tablero[fDestino][cDestino] != null &&
            tablero[fDestino][cDestino].getColor().equals(this.color)) {
            return false;
        }

        //movimientos diagonales
        if (Math.abs(fDestino - fOrigen) != Math.abs(cDestino - cOrigen)) {
            return false;
        }

        //calcular las dirrecciones
        int pasoFila = (fDestino - fOrigen) > 0 ? 1 : -1;
        int pasoColumna = (cDestino - cOrigen) > 0 ? 1 : -1;

        int i = fOrigen + pasoFila;
        int j = cOrigen + pasoColumna;

        //revisar las casillas que se interponen
        while (i != fDestino && j != cDestino) {
            if (tablero[i][j] != null) {
                return false;
            }
            i += pasoFila;
            j += pasoColumna;
        }

        //movimiento válido si es captura enemiga o casilla vacía
        return tablero[fDestino][cDestino] == null ||
               !tablero[fDestino][cDestino].getColor().equals(this.color);
    }
}
