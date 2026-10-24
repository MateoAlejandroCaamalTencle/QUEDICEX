package Modelo;

public class Rey extends Pieza {
    
    private boolean seMovio = false;

    public Rey(String color, int fila, int columna) {
        super(color, fila, columna);
    }
    
    public boolean isSeMovio() { return seMovio; }
    public void setSeMovio(boolean seMovio) { this.seMovio = seMovio; }
    
    @Override
    public boolean esMovimientoPseudoLegal(int fOrigen, int cOrigen, int fDestino, int cDestino, Pieza[][] tablero) {
        int df = Math.abs(fDestino - fOrigen);
        int dc = Math.abs(cDestino - cOrigen);

        if (df <= 1 && dc <= 1) {
            return tablero[fDestino][cDestino] == null ||
                   !tablero[fDestino][cDestino].getColor().equals(this.color);
        }

        if (seMovio || fDestino != fOrigen) return false;

        if (cDestino == cOrigen + 2) {
            Pieza torre = tablero[fOrigen][7];
            if (torre instanceof Torre && !((Torre) torre).isSeMovio()) {
                if (tablero[fOrigen][5] == null && tablero[fOrigen][6] == null) return true;
            }
        }
        if (cDestino == cOrigen - 2) {
            Pieza torre = tablero[fOrigen][0];
            if (torre instanceof Torre && !((Torre) torre).isSeMovio()) {
                if (tablero[fOrigen][1] == null && tablero[fOrigen][2] == null && tablero[fOrigen][3] == null) return true;
            }
        }
        return false;
    }

    @Override
    public String getRutaIcono() {
        return color.equals("blanco") ? "/IMGS/Rey.png" : "/IMGS/ReyNegro.png";
    }

    @Override
    public Pieza clonar() {
        Rey r = new Rey(this.color, this.fila, this.columna);
        r.setSeMovio(this.seMovio);
        return r;
    }
}
