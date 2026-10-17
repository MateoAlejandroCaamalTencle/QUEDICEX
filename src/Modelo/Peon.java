/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author compu
 */
public class Peon extends Pieza {

    //constructor
    
    public Peon(String color, int fila, int columna) {
        super(color, fila, columna);
    }

    //método polimorfico
    
    @Override
    public boolean movimientoValido(int fOrigen, int cOrigen, int fDestino, int cDestino, Pieza[][] tablero) {

        int direccion = color.equals("blanco") ? -1 : 1; 
        int inicioFila = color.equals("blanco") ? 6 : 1;

        //movimiento normal
        if (cOrigen == cDestino && tablero[fDestino][cDestino] == null) {
            if (fOrigen + direccion == fDestino)
                return true;

            //movimiento doble
            if (fOrigen == inicioFila &&
                fOrigen + 2 * direccion == fDestino &&
                tablero[fOrigen + direccion][cDestino] == null) {

                return true;
            }
        }

        //captura de piezas
        if (Math.abs(cOrigen - cDestino) == 1 &&
            fOrigen + direccion == fDestino) {

            Pieza objetivo = tablero[fDestino][cDestino];

            if (objetivo != null && !objetivo.getColor().equals(color))
                return true;
        }

        return false;
    }
}
