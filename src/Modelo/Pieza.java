package Modelo;

public abstract class Pieza {
    protected String color;
    protected int fila;
    protected int columna;
    
    public Pieza(String color, int fila, int columna) {
        this.color = color;
        this.fila = fila;
        this.columna = columna;
    }
    
    public String getColor() {
        return color;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }
    
    public void setPosicion(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }
    
    // Rename Method aplicado
    public abstract boolean esMovimientoPseudoLegal(int fOrigen, int cOrigen, int fDestino, int cDestino, Pieza[][] tablero);
    
    // Replace Conditional with Polymorphism aplicado
    public abstract String getRutaIcono();
    public abstract Pieza clonar();
}
