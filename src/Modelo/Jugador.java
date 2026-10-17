/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.util.ArrayList;

/**
 *
 * @author compu
 */
public class Jugador {
    private String nombre;
    private String color;
    private ArrayList<Pieza> piezas;
    
    //constructor

    public Jugador(String nombre, String color, ArrayList<Pieza> piezas) {
        this.nombre = nombre;
        this.color = color;
        this.piezas = piezas;
    }
    
    //metodos get

    public String getNombre() {
        return nombre;
    }

    public String getColor() {
        return color;
    }

    public ArrayList<Pieza> getPiezas() {
        return piezas;
    }
    
}
