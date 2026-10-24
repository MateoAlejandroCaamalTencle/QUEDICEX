/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author compu
 */
public class Reina extends Pieza {
    
    //constructor

    public Reina(String color, int fila, int columna) {
        super(color, fila, columna);
    }
    
    //método polimorfico

    @Override
    public boolean movimientoValido(int fOrigen, int cOrigen, int fDestino, int cDestino, Pieza[][] tablero) {

        //movimiento en lineas rectas
        if (fOrigen == fDestino || cOrigen == cDestino) {
            return new Torre(color, fOrigen, cOrigen)
                    .movimientoValido(fOrigen, cOrigen, fDestino, cDestino, tablero);
        }

        //movimiento en diagonales
        if (Math.abs(fDestino - fOrigen) == Math.abs(cDestino - cOrigen)) {
            return new Alfil(color, fOrigen, cOrigen)
                    .movimientoValido(fOrigen, cOrigen, fDestino, cDestino, tablero);
        }

        return false;
    }
}
