package CM_Tablero;

import Modelo.*;
import Vista.FrmJaque;
import Vista.Tablero;
import java.awt.Color;
import javax.swing.*;
import java.util.*;

public class CMTablero {
    private final Tablero frm;
    private final LogicaTablero logica;

    private boolean seleccionando = false; 
    private int fOrigen, cOrigen; 
    private int fDestino, cDestino; 

    private JButton[][] casillas;

    public CMTablero(Tablero frm) {
        this.frm = frm;
        this.logica = new LogicaTablero();
        
        if (frm != null) {
            crearMatrizBotones();
            agregarEventos();
            actualizarGUI();
        }
    }
    
    private void crearMatrizBotones() {
        casillas = new JButton[][]{
            {frm.btnCasilla00, frm.btnCasilla01, frm.btnCasilla02, frm.btnCasilla03, frm.btnCasilla04, frm.btnCasilla05, frm.btnCasilla06, frm.btnCasilla07},
            {frm.btnCasilla10, frm.btnCasilla11, frm.btnCasilla12, frm.btnCasilla13, frm.btnCasilla14, frm.btnCasilla15, frm.btnCasilla16, frm.btnCasilla17},
            {frm.btnCasilla20, frm.btnCasilla21, frm.btnCasilla22, frm.btnCasilla23, frm.btnCasilla24, frm.btnCasilla25, frm.btnCasilla26, frm.btnCasilla27},
            {frm.btnCasilla30, frm.btnCasilla31, frm.btnCasilla32, frm.btnCasilla33, frm.btnCasilla34, frm.btnCasilla35, frm.btnCasilla36, frm.btnCasilla37},
            {frm.btnCasilla40, frm.btnCasilla41, frm.btnCasilla42, frm.btnCasilla43, frm.btnCasilla44, frm.btnCasilla45, frm.btnCasilla46, frm.btnCasilla47},
            {frm.btnCasilla50, frm.btnCasilla51, frm.btnCasilla52, frm.btnCasilla53, frm.btnCasilla54, frm.btnCasilla55, frm.btnCasilla56, frm.btnCasilla57},
            {frm.btnCasilla60, frm.btnCasilla61, frm.btnCasilla62, frm.btnCasilla63, frm.btnCasilla64, frm.btnCasilla65, frm.btnCasilla66, frm.btnCasilla67},
            {frm.btnCasilla70, frm.btnCasilla71, frm.btnCasilla72, frm.btnCasilla73, frm.btnCasilla74, frm.btnCasilla75, frm.btnCasilla76, frm.btnCasilla77}
        };
    }
    
    private void agregarEventos() {
        for (int f = 0; f < 8; f++) {
            for (int c = 0; c < 8; c++) {
                int ff = f;
                int cc = c;
                casillas[f][c].addActionListener(e -> clicEnCasilla(ff, cc));
            }
        }
    }
    
    public void clicEnCasilla(int fila, int columna) {
        if (!seleccionando) {
            seleccionarOrigen(fila, columna);
        } else {
            seleccionarDestino(fila, columna);
        }
    }
    
    private void seleccionarOrigen(int f, int c) {
        Pieza p = logica.getCasillas()[f][c];
        if (p == null) {
            JOptionPane.showMessageDialog(frm, "No hay pieza en esa casilla.");
            return;
        }
        if (!p.getColor().equals(logica.getTurno())) {
            JOptionPane.showMessageDialog(frm, "No es tu turno.");
            return;
        }
        fOrigen = f;
        cOrigen = c;
        seleccionando = true;
        resaltarCasilla(f, c);

        List<Movimiento> legales = generarMovimientosLegalesParaPieza(f, c);
        marcarMovimientosLegales(legales);
    }
    
    private void seleccionarDestino(int f, int c) {
        fDestino = f;
        cDestino = c;
        if (fDestino == fOrigen && cDestino == cOrigen) {
            limpiarResaltados();
            seleccionando = false;
            fOrigen = cOrigen = -1;
            fDestino = cDestino = -1;
            return;
        }

        List<Movimiento> legales = generarMovimientosLegalesParaPieza(fOrigen, cOrigen);
        boolean esLegal = false;

        for (Movimiento m : legales) {
            if (m.fDestino == fDestino && m.cDestino == cDestino) {
                esLegal = true;
                break;
            }
        }   
        if (!esLegal) {
            JOptionPane.showMessageDialog(frm, "Movimiento inválido.");
            limpiarResaltados();
            seleccionando = false;
            fOrigen = cOrigen = -1;
            fDestino = cDestino = -1;
            return;
        }

        ejecutarMovimientoConEspeciales(fOrigen, cOrigen, fDestino, cDestino);

        limpiarResaltados();
        seleccionando = false;
        fOrigen = cOrigen = -1;
        fDestino = cDestino = -1;

        String enemigo = logica.getTurno().equals("blanco") ? "negro" : "blanco";
        if (estaEnJaque(logica, enemigo)) {
            List<Movimiento> movsEnemigo = generarMovimientosLegalesParaColor(enemigo);
            if (movsEnemigo.isEmpty()) {
                new FrmJaque(frm).setVisible(true);
            } else {
                new FrmJaque(frm).setVisible(true);
            }
        }
    }

    private void ejecutarMovimientoConEspeciales(int fo, int co, int fd, int cd) {
        Pieza origen = logica.getCasillas()[fo][co];
        Pieza destino = logica.getCasillas()[fd][cd];

        if (origen instanceof Rey && Math.abs(cd - co) == 2 && fo == fd) {
            boolean realizado = intentarEnroque(origen.getColor(), fo, co, fd, cd);
            if (realizado) {
                actualizarGUI(); 
                logica.alternarTurno();
                return;
            }
        }

        boolean esEnPassant = false;
        if (origen instanceof Peon && Math.abs(cd - co) == 1 && destino == null) {
            if (logica.getUltimaDoblePeon() != null) {
                int filaPeonDoble = logica.getUltimaDoblePeon()[0];
                int colPeonDoble = logica.getUltimaDoblePeon()[1];
                int filaCaptura = origen.getColor().equals("blanco") ? fd + 1 : fd - 1;
                if (filaPeonDoble == filaCaptura && colPeonDoble == cd) {
                    logica.getCasillas()[filaPeonDoble][colPeonDoble] = null;
                    esEnPassant = true;
                }
            }
        }

        boolean moved = logica.mover(fo, co, fd, cd);
        if (!moved) {
            JOptionPane.showMessageDialog(frm, "Error al mover.");
            return;
        }

        if (origen instanceof Rey) {
            logica.getMovedMap().put("rey_" + origen.getColor(), true);
        } else if (origen instanceof Torre) {
            String key = "torre_" + origen.getColor() + "_" + fo + "_" + co;
            logica.getMovedMap().put(key, true);
        }

        if (origen instanceof Peon && Math.abs(fd - fo) == 2) {
            logica.setUltimaDoblePeon(new int[]{fd, cd});
        } else {
            logica.setUltimaDoblePeon(null);
        }

        Pieza movedPiece = logica.getCasillas()[fd][cd];
        if (debeCoronar(movedPiece, fd)) {
            realizarPromocion(fd, cd, movedPiece.getColor());
            actualizarGUI(); 
            logica.alternarTurno();
            return;
        }

        actualizarGUI();
        logica.alternarTurno();
    }

    private void actualizarIconosDespuesEnroque(int fr, int cr, int fd, int cd) {
        actualizarGUI();
    }

    private boolean intentarEnroque(String color, int fr, int cr, int fd, int cd) {
        boolean corto = cd > cr;
        int fila = fr;
        int torreCol = corto ? 7 : 0;

        Pieza torreOriginal = logica.getCasillas()[fila][torreCol];
        Pieza reyOriginal = logica.getCasillas()[fr][cr];
        if (!(torreOriginal instanceof Torre) || !(reyOriginal instanceof Rey)) return false;

        String keyRey = "rey_" + color;
        String keyTorre = "torre_" + color + "_" + fila + "_" + torreCol;
        if (Boolean.TRUE.equals(logica.getMovedMap().get(keyRey)) || Boolean.TRUE.equals(logica.getMovedMap().get(keyTorre))) {
            return false;
        }

        int paso = corto ? 1 : -1;
        int c = cr + paso;
        while (c != torreCol) {
            if (logica.getCasillas()[fila][c] != null) return false;
            c += paso;
        }

        LogicaTablero copia = copiarModelo(logica);
        Pieza reyCopia = copia.getCasillas()[fr][cr]; 
        if (reyCopia == null || !(reyCopia instanceof Rey)) return false;

        int col1 = cr;
        int col2 = cr + paso;
        int col3 = cr + 2 * paso;

        copia.getCasillas()[fila][col1] = null;
        copia.getCasillas()[fila][col2] = reyCopia;
        reyCopia.setPosicion(fila, col2);
        if (estaEnJaque(copia, color)) {
            return false;
        }

        copia.getCasillas()[fila][col2] = null;
        copia.getCasillas()[fila][col3] = reyCopia;
        reyCopia.setPosicion(fila, col3);
        if (estaEnJaque(copia, color)) {
            return false;
        }

        logica.getCasillas()[fr][cr] = null;
        logica.getCasillas()[fd][cd] = reyOriginal;
        reyOriginal.setPosicion(fd, cd);

        int nuevaColTorre = corto ? cd - 1 : cd + 1;
        logica.getCasillas()[fila][torreCol] = null;
        logica.getCasillas()[fila][nuevaColTorre] = torreOriginal;
        torreOriginal.setPosicion(fila, nuevaColTorre);

        logica.getMovedMap().put(keyRey, true);
        logica.getMovedMap().put(keyTorre, true);
        logica.setUltimaDoblePeon(null);
        return true;
    }

    private void marcarMovimientosLegales(List<Movimiento> lista) {
        for (Movimiento m : lista) {
            casillas[m.fDestino][m.cDestino].setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
        }
    }

    private void limpiarResaltados() {
        for (int f = 0; f < 8; f++) for (int c = 0; c < 8; c++) {
            casillas[f][c].setBorder(BorderFactory.createEtchedBorder());
        }
    }

    private void resaltarCasilla(int f, int c) {
        casillas[f][c].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
    }

    private List<Movimiento> generarMovimientosLegalesParaPieza(int f, int c) {
        List<Movimiento> res = new ArrayList<>();
        Pieza p = logica.getCasillas()[f][c];
        if (p == null) return res;
        for (int rf = 0; rf < 8; rf++) for (int rc = 0; rc < 8; rc++) {
            if (p.esMovimientoPseudoLegal(f, c, rf, rc, logica.getCasillas())
                    && esMovimientoLegalEnCopia(p, f, c, rf, rc)) {
                res.add(new Movimiento(f, c, rf, rc));
            }
        }
        return res;
    }

    private boolean esMovimientoLegalEnCopia(Pieza pieza, int fOrigen, int cOrigen, int fDestino, int cDestino) {
        LogicaTablero copia = copiarModelo(logica);
        if (pieza instanceof Peon && Math.abs(cDestino - cOrigen) == 1
                && copia.getCasillas()[fDestino][cDestino] == null
                && logica.getUltimaDoblePeon() != null) {
            int filaPeonDoble = logica.getUltimaDoblePeon()[0];
            int colPeonDoble = logica.getUltimaDoblePeon()[1];
            int filaCaptura = pieza.getColor().equals("blanco") ? fDestino + 1 : fDestino - 1;
            if (filaPeonDoble == filaCaptura && colPeonDoble == cDestino) {
                copia.getCasillas()[filaPeonDoble][colPeonDoble] = null;
            }
        }
        if (!copia.mover(fOrigen, cOrigen, fDestino, cDestino)) return false;
        return !estaEnJaque(copia, pieza.getColor());
    }

    private List<Movimiento> generarMovimientosLegalesParaColor(String color) {
        List<Movimiento> lista = new ArrayList<>();
        for (int f = 0; f < 8; f++) for (int c = 0; c < 8; c++) {
            Pieza p = logica.getCasillas()[f][c];
            if (p == null || !p.getColor().equals(color)) continue;
            lista.addAll(generarMovimientosLegalesParaPieza(f, c));
        }
        return lista;
    }
    
    private boolean estaEnJaque(LogicaTablero tab, String color) {
        int[] posRey = encontrarRey(tab, color);
        if (posRey == null) return true; 
        int rf = posRey[0], rc = posRey[1];
        String enemigo = color.equals("blanco") ? "negro" : "blanco";
        for (int f = 0; f < 8; f++) for (int c = 0; c < 8; c++) {
            Pieza p = tab.getCasillas()[f][c];
            if (p == null || !p.getColor().equals(enemigo)) continue;
            if (p.esMovimientoPseudoLegal(f, c, rf, rc, tab.getCasillas())) return true;
        }
        return false;
    }

    private int[] encontrarRey(LogicaTablero tab, String color) {
        Pieza[][] cas = tab.getCasillas();
        for (int f = 0; f < 8; f++) for (int c = 0; c < 8; c++) {
            Pieza p = cas[f][c];
            if (p instanceof Rey && p.getColor().equals(color)) return new int[]{f, c};
        }
        return null;
    }
    
    // Replace Conditional with Polymorphism aplicado (Adiós al Switch)
    private LogicaTablero copiarModelo(LogicaTablero original) {
        LogicaTablero copia = new LogicaTablero();
        Pieza[][] dest = copia.getCasillas();
        Pieza[][] src = original.getCasillas();
        
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (src[r][c] != null) {
                    dest[r][c] = src[r][c].clonar(); 
                } else {
                    dest[r][c] = null;
                }
            }
        }
        return copia;
    }
    
    private boolean debeCoronar(Pieza pieza, int fila) {
        return pieza instanceof Peon
                && ((pieza.getColor().equals("blanco") && fila == 0)
                || (pieza.getColor().equals("negro") && fila == 7));
    }

    private void realizarPromocion(int f, int c, String color) {
        String[] opciones = {"Reina", "Torre", "Alfil", "Caballo"};
        String seleccion = (String) JOptionPane.showInputDialog(
                frm, "Elige pieza para coronacion:", "Coronacion",
                JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (seleccion == null) seleccion = "Reina";

        Pieza nueva;
        switch (seleccion) {
            case "Torre" -> nueva = new Torre(color, f, c);
            case "Alfil" -> nueva = new Alfil(color, f, c);
            case "Caballo" -> nueva = new Caballo(color, f, c);
            default -> nueva = new Reina(color, f, c);
        }
        logica.getCasillas()[f][c] = nueva;
        actualizarGUI();
    }

    private Icon getIconoPromocion(String seleccion, String color) {
        String ruta = null;
        boolean blanco = color.equals("blanco");
        switch (seleccion) {
            case "Reina" -> ruta = blanco ? "/IMGS/Reina.png" : "/IMGS/ReinaNegra.png";
            case "Torre" -> ruta = blanco ? "/IMGS/Torre.png" : "/IMGS/TorreNegra.png";
            case "Alfil" -> ruta = blanco ? "/IMGS/Alfil.png" : "/IMGS/AlfilNegro.png";
            case "Caballo" -> ruta = blanco ? "/IMGS/Caballo.png" : "/IMGS/CaballoNegro.png";
        }
        if (ruta == null) return null;
        java.net.URL url = getClass().getResource(ruta);
        return url != null ? new ImageIcon(url) : null;
    }

    // Replace Conditional with Polymorphism aplicado (Adiós instanceof para íconos)
    private Icon getIconoParaPieza(Pieza p) {
        if (p == null) return null;
        String ruta = p.getRutaIcono(); 
        if (ruta == null) return null;
        java.net.URL url = getClass().getResource(ruta);
        return url != null ? new ImageIcon(url) : null;
    }

    public static class Movimiento {
        public final int fOrigen, cOrigen, fDestino, cDestino;
        public Movimiento(int fo, int co, int fd, int cd) {
            this.fOrigen = fo; this.cOrigen = co; this.fDestino = fd; this.cDestino = cd;
        }
    }

    public void actualizarGUI() {
        Pieza[][] cas = logica.getCasillas();
        for (int f = 0; f < 8; f++) {
            for (int c = 0; c < 8; c++) {
                casillas[f][c].setIcon(null);
            }
        }
        for (int f = 0; f < 8; f++) {
            for (int c = 0; c < 8; c++) {
                Pieza p = cas[f][c];
                if (p != null) {
                    casillas[f][c].setIcon(getIconoParaPieza(p));
                }
            }
        }
    }
}
