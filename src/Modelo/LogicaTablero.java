package Modelo;

import java.util.HashMap;
import java.util.Map;

public class LogicaTablero {
    private Pieza[][] casillas = new Pieza[8][8];
    
    // Move Field aplicado (Estado traído desde CMTablero)
    private String turno = "blanco";
    private final Map<String, Boolean> movedMap = new HashMap<>();
    private int[] ultimaDoblePeon = null;
    
    public LogicaTablero() {
        inicializar();
        initMovedMap();
    }
    
    public Pieza[][] getCasillas() {
        return casillas;
    }
    
    // Getters y Setters para el estado
    public String getTurno() { return turno; }
    public void alternarTurno() { turno = turno.equals("blanco") ? "negro" : "blanco"; }
    
    public Map<String, Boolean> getMovedMap() { return movedMap; }
    
    public int[] getUltimaDoblePeon() { return ultimaDoblePeon; }
    public void setUltimaDoblePeon(int[] pos) { this.ultimaDoblePeon = pos; }
    
    private void initMovedMap() {
        movedMap.put("rey_blanco", false);
        movedMap.put("rey_negro", false);
        movedMap.put("torre_blanco_7_0", false);
        movedMap.put("torre_blanco_7_7", false);
        movedMap.put("torre_negro_0_0", false);
        movedMap.put("torre_negro_0_7", false);
    }
    
    private void inicializar() {
        for (int i = 0; i < 8; i++) {
            casillas[1][i] = new Peon("negro", 1, i);
            casillas[6][i] = new Peon("blanco", 6, i);
        }
        casillas[0][0] = new Torre("negro", 0, 0); casillas[0][7] = new Torre("negro", 0, 7);
        casillas[7][0] = new Torre("blanco", 7, 0); casillas[7][7] = new Torre("blanco", 7, 7);
        casillas[0][1] = new Caballo("negro", 0, 1); casillas[0][6] = new Caballo("negro", 0, 6);
        casillas[7][1] = new Caballo("blanco", 7, 1); casillas[7][6] = new Caballo("blanco", 7, 6);
        casillas[0][2] = new Alfil("negro", 0, 2); casillas[0][5] = new Alfil("negro", 0, 5);
        casillas[7][2] = new Alfil("blanco", 7, 2); casillas[7][5] = new Alfil("blanco", 7, 5);
        casillas[0][3] = new Reina("negro", 0, 3); casillas[7][3] = new Reina("blanco", 7, 3);
        casillas[0][4] = new Rey("negro", 0, 4); casillas[7][4] = new Rey("blanco", 7, 4);
    }
    
    public boolean mover(int fOrigen, int cOrigen, int fDestino, int cDestino) {
        Pieza pieza = casillas[fOrigen][cOrigen];

        if (pieza != null && pieza.esMovimientoPseudoLegal(fOrigen, cOrigen, fDestino, cDestino, casillas)) {
            if (pieza instanceof Rey) {
                Rey rey = (Rey) pieza;
                if (cDestino == cOrigen + 2) {
                    Pieza torre = casillas[fOrigen][7];
                    if (torre != null) {
                        casillas[fOrigen][5] = torre;
                        casillas[fOrigen][7] = null;
                        torre.setPosicion(fOrigen, 5);
                        if (torre instanceof Torre) ((Torre) torre).setSeMovio(true);
                    }
                }
                if (cDestino == cOrigen - 2) {
                    Pieza torre = casillas[fOrigen][0];
                    if (torre != null) {
                        casillas[fOrigen][3] = torre;
                        casillas[fOrigen][0] = null;
                        torre.setPosicion(fOrigen, 3);
                        if (torre instanceof Torre) ((Torre) torre).setSeMovio(true);
                    }
                }
                rey.setSeMovio(true);
            }
            casillas[fDestino][cDestino] = pieza;
            casillas[fOrigen][cOrigen] = null;
            pieza.setPosicion(fDestino, cDestino);

            if (pieza instanceof Torre) {
                ((Torre) pieza).setSeMovio(true);
            }
            return true;
        }
        return false;
    }
}
