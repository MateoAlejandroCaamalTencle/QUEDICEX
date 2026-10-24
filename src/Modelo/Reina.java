package Modelo;

public class Reina extends Pieza {

    public Reina(String color, int fila, int columna) {
        super(color, fila, columna);
    }
    
    @Override
    public boolean esMovimientoPseudoLegal(int fOrigen, int cOrigen, int fDestino, int cDestino, Pieza[][] tablero) {
        if (fOrigen == fDestino || cOrigen == cDestino) {
            return new Torre(color, fOrigen, cOrigen).esMovimientoPseudoLegal(fOrigen, cOrigen, fDestino, cDestino, tablero);
        }
        if (Math.abs(fDestino - fOrigen) == Math.abs(cDestino - cOrigen)) {
            return new Alfil(color, fOrigen, cOrigen).esMovimientoPseudoLegal(fOrigen, cOrigen, fDestino, cDestino, tablero);
        }
        return false;
    }

    @Override
    public String getRutaIcono() {
        return color.equals("blanco") ? "/IMGS/Reina.png" : "/IMGS/ReinaNegra.png";
    }

    @Override
    public Pieza clonar() {
        return new Reina(this.color, this.fila, this.columna);
    }
}
