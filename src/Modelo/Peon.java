package Modelo;

public class Peon extends Pieza {

    public Peon(String color, int fila, int columna) {
        super(color, fila, columna);
    }

    @Override
    public boolean esMovimientoPseudoLegal(int fOrigen, int cOrigen, int fDestino, int cDestino, Pieza[][] tablero) {
        int direccion = color.equals("blanco") ? -1 : 1; 
        int inicioFila = color.equals("blanco") ? 6 : 1;

        if (cOrigen == cDestino && tablero[fDestino][cDestino] == null) {
            if (fOrigen + direccion == fDestino)
                return true;
            if (fOrigen == inicioFila &&
                fOrigen + 2 * direccion == fDestino &&
                tablero[fOrigen + direccion][cDestino] == null) {
                return true;
            }
        }
        if (Math.abs(cOrigen - cDestino) == 1 &&
            fOrigen + direccion == fDestino) {
            Pieza objetivo = tablero[fDestino][cDestino];
            if (objetivo != null && !objetivo.getColor().equals(color))
                return true;
        }
        return false;
    }

    @Override
    public String getRutaIcono() {
        return color.equals("blanco") ? "/IMGS/Ficha Peon.png" : "/IMGS/Ficha PeonNegra.png";
    }

    @Override
    public Pieza clonar() {
        return new Peon(this.color, this.fila, this.columna);
    }
}
