/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author compu
 */
public class Torre extends Pieza {
    
    private boolean seMovio = false;
    
    public Torre(String color, int fila, int columna) {
        super(color, fila, columna);
    }
    
    public boolean isSeMovio() {
        return seMovio;
    }
    
    public void setSeMovio(boolean seMovio) {
        this.seMovio = seMovio;
    }
    
    @Override
    public boolean movimientoValido(int fOrigen, int cOrigen, int fDestino, int cDestino, Pieza[][] tablero) {

        //validar límites
        if (fDestino < 0 || fDestino > 7 || cDestino < 0 || cDestino > 7)
            return false;

        //movimiento debe ser recto
        if (fOrigen != fDestino && cOrigen != cDestino)
            return false;

        //movimiento vertical
        if (cOrigen == cDestino) {
            int paso = (fDestino > fOrigen) ? 1 : -1;

            for (int f = fOrigen + paso; f != fDestino; f += paso) {
                if (f < 0 || f > 7) return false; // evita índices -1 o 8
                if (tablero[f][cOrigen] != null) return false;
            }
        }

        //movimiento horizontal
        else {
            int paso = (cDestino > cOrigen) ? 1 : -1;

            for (int c = cOrigen + paso; c != cDestino; c += paso) {
                if (c < 0 || c > 7) return false; // evita índices -1 o 8
                if (tablero[fOrigen][c] != null) return false;
            }
        }

        //no capturar tu propia pieza
        Pieza destino = tablero[fDestino][cDestino];
        if (destino != null && destino.getColor().equals(this.color))
            return false;

        return true;
    }
}
