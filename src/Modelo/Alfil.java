package Modelo;

public class Alfil extends Pieza {

    public Alfil(String color, int fila, int columna) {
        super(color, fila, columna);
    }
    
    @Override
    public boolean esMovimientoPseudoLegal(int fOrigen, int cOrigen, int fDestino, int cDestino, Pieza[][] tablero) {
        if (fDestino < 0 || fDestino >= tablero.length || cDestino < 0 || cDestino >= tablero[0].length) return false;
        if (tablero[fDestino][cDestino] != null && tablero[fDestino][cDestino].getColor().equals(this.color)) return false;
        if (Math.abs(fDestino - fOrigen) != Math.abs(cDestino - cOrigen)) return false;

        int pasoFila = (fDestino - fOrigen) > 0 ? 1 : -1;
        int pasoColumna = (cDestino - cOrigen) > 0 ? 1 : -1;
        int i = fOrigen + pasoFila;
        int j = cOrigen + pasoColumna;

        while (i != fDestino && j != cDestino) {
            if (tablero[i][j] != null) return false;
            i += pasoFila;
            j += pasoColumna;
        }

        return tablero[fDestino][cDestino] == null || !tablero[fDestino][cDestino].getColor().equals(this.color);
    }

    @Override
    public String getRutaIcono() {
        return color.equals("blanco") ? "/IMGS/Alfil.png" : "/IMGS/AlfilNegro.png";
    }

    @Override
    public Pieza clonar() {
        return new Alfil(this.color, this.fila, this.columna);
    }
}
