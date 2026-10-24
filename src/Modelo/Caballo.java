package Modelo;

public class Caballo extends Pieza {

    public Caballo(String color, int fila, int columna) {
        super(color, fila, columna);
    }
    
    @Override
    public boolean esMovimientoPseudoLegal(int fOrigen, int cOrigen, int fDestino, int cDestino, Pieza[][] tablero) {
        int df = Math.abs(fDestino - fOrigen);
        int dc = Math.abs(cDestino - cOrigen);
        if ((df == 2 && dc == 1) || (df == 1 && dc == 2)) {
            return tablero[fDestino][cDestino] == null ||
                   !tablero[fDestino][cDestino].getColor().equals(this.color);
        }
        return false;
    }

    @Override
    public String getRutaIcono() {
        return color.equals("blanco") ? "/IMGS/Caballo.png" : "/IMGS/CaballoNegro.png";
    }

    @Override
    public Pieza clonar() {
        return new Caballo(this.color, this.fila, this.columna);
    }
}
