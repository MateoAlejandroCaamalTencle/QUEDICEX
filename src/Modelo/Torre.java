package Modelo;

public class Torre extends Pieza {
    
    private boolean seMovio = false;
    
    public Torre(String color, int fila, int columna) {
        super(color, fila, columna);
    }
    
    public boolean isSeMovio() { return seMovio; }
    public void setSeMovio(boolean seMovio) { this.seMovio = seMovio; }
    
    @Override
    public boolean esMovimientoPseudoLegal(int fOrigen, int cOrigen, int fDestino, int cDestino, Pieza[][] tablero) {
        if (fDestino < 0 || fDestino > 7 || cDestino < 0 || cDestino > 7) return false;
        if (fOrigen != fDestino && cOrigen != cDestino) return false;

        if (cOrigen == cDestino) {
            int paso = (fDestino > fOrigen) ? 1 : -1;
            for (int f = fOrigen + paso; f != fDestino; f += paso) {
                if (f < 0 || f > 7) return false;
                if (tablero[f][cOrigen] != null) return false;
            }
        } else {
            int paso = (cDestino > cOrigen) ? 1 : -1;
            for (int c = cOrigen + paso; c != cDestino; c += paso) {
                if (c < 0 || c > 7) return false;
                if (tablero[fOrigen][c] != null) return false;
            }
        }

        Pieza destino = tablero[fDestino][cDestino];
        if (destino != null && destino.getColor().equals(this.color)) return false;

        return true;
    }

    @Override
    public String getRutaIcono() {
        return color.equals("blanco") ? "/IMGS/Torre.png" : "/IMGS/TorreNegra.png";
    }

    @Override
    public Pieza clonar() {
        Torre t = new Torre(this.color, this.fila, this.columna);
        t.setSeMovio(this.seMovio);
        return t;
    }
}
