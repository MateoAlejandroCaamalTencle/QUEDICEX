/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author compu
 */
public abstract class Pieza {
    protected String color;
    protected int fila;
    protected int columna;
    
    //constructor

    public Pieza(String color, int fila, int columna) {
        this.color = color;
        this.fila = fila;
        this.columna = columna;
    }
    
    //métodos get

    public String getColor() {
        return color;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }
    
    //método set

    public void setPosicion(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }
    
    public abstract boolean esMovimientoPseudoLegal(int fOrigen, int cOrigen, int fDestino, int cDestino, Pieza[][] tablero);
}
