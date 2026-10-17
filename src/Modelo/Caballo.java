/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author compu
 */
public class Caballo extends Pieza {
    
    //constructor

    public Caballo(String color, int fila, int columna) {
        super(color, fila, columna);
    }
    
    //método polimorfico

    @Override
    public boolean movimientoValido(int fOrigen, int cOrigen, int fDestino, int cDestino, Pieza[][] tablero) {

        int df = Math.abs(fDestino - fOrigen);
        int dc = Math.abs(cDestino - cOrigen);

        //movimiento en L
        if ((df == 2 && dc == 1) || (df == 1 && dc == 2)) {
            return tablero[fDestino][cDestino] == null ||
                   !tablero[fDestino][cDestino].getColor().equals(this.color);
        }

        return false;
    }
}
