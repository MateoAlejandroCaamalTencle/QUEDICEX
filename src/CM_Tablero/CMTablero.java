/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CM_Tablero;

import Modelo.*;
import Vista.FrmJaque;
import Vista.Tablero;
import java.awt.Color;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author compu
 */
public class CMTablero {
    //atributos
    private final Tablero frm;
    private final LogicaTablero logica;
    private final ReglasAjedrez reglas;

    private boolean seleccionando = false; //indica si el jugador ya hizo click en una casilla de origen
    private int fOrigen, cOrigen; 
    private int fDestino, cDestino; //almacena la fila y columna de la casilla donde se quiere mover una pieza
    private String turno = "blanco"; // alterna entre "blanco" y "negro"

    //matriz de botones
    private JButton[][] casillas;
    //enroque
    private final Map<String, Boolean> movedMap = new HashMap<>();
    //captura al paso
    private int[] ultimaDoblePeon = null;
    
    //constructor

    public CMTablero(Tablero frm) {
        this.frm = frm;
        this.logica = new LogicaTablero();
        this.reglas = new ReglasAjedrez(logica);
        initMovedMap();
        if (frm != null) {
            crearMatrizBotones();
            agregarEventos();
            actualizarGUI();
        }
    }
    
    private void initMovedMap() {
        movedMap.put("rey_blanco", false);
        movedMap.put("rey_negro", false);
        movedMap.put("torre_blanco_7_0", false);
        movedMap.put("torre_blanco_7_7", false);
        movedMap.put("torre_negro_0_0", false);
        movedMap.put("torre_negro_0_7", false);
    }
    
    //método para crear la matriz
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
    
    //método para asignar eventos
    private void agregarEventos() {

        for (int f = 0; f < 8; f++) {
            for (int c = 0; c < 8; c++) {
                int ff = f;
                int cc = c;
                casillas[f][c].addActionListener(e -> clicEnCasilla(ff, cc));
            }
        }
    }
    
    //método del click
    private void clicEnCasilla(int fila, int columna) {
        if (!seleccionando) {
            seleccionarOrigen(fila, columna);
        } else {
            seleccionarDestino(fila, columna);
        }
    }
    
    //método para selecionar origen
    private void seleccionarOrigen(int f, int c) {
        Pieza p = logica.getCasillas()[f][c];
        if (p == null) {
            JOptionPane.showMessageDialog(frm, "No hay pieza en esa casilla.");
            return;
        }
        if (!p.getColor().equals(turno)) {
            JOptionPane.showMessageDialog(frm, "No es tu turno.");
            return;
        }
        //resaltar el origen de la pieza y los movimientos validos que tiene
        fOrigen = f;
        cOrigen = c;
        seleccionando = true;
        resaltarCasilla(f, c);

        List<Movimiento> legales = generarMovimientosLegalesParaPieza(f, c);
        marcarMovimientosLegales(legales);
    }
    
    //método para seleccionar destino
    private void seleccionarDestino(int f, int c) {
        fDestino = f;
        cDestino = c;
        //si vuelve a pulsar la casilla origen -> cancelar
        if (fDestino == fOrigen && cDestino == cOrigen) {
            limpiarResaltados();
            seleccionando = false;
            fOrigen = cOrigen = -1;
            fDestino = cDestino = -1;
            return;
        }
        //verificar movimientos legales
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

        //ejecutar movimiento (lógica + visual)
        ejecutarMovimientoConEspeciales(fOrigen, cOrigen, fDestino, cDestino);

        //quitar los resaltados y reset selección
        limpiarResaltados();
        seleccionando = false;
        fOrigen = cOrigen = -1;
        fDestino = cDestino = -1;

        //después del movimiento, comprobar jaque mate al oponente
        String enemigo = turno.equals("blanco") ? "negro" : "blanco";
        if (estaEnJaque(logica, enemigo)) {
            //si el oponente no tiene movimientos legales, es mate
            List<Movimiento> movsEnemigo = generarMovimientosLegalesParaColor(enemigo);
            if (movsEnemigo.isEmpty()) {
                new FrmJaque(frm).setVisible(true);
            } else {
                new FrmJaque(frm).setVisible(true);
            }
        }
    }

    //ejecuta el movimiento lógico y las acciones visuales y especiales
    private void ejecutarMovimientoConEspeciales(int fo, int co, int fd, int cd) {
        Pieza origen = logica.getCasillas()[fo][co];
        Pieza destino = logica.getCasillas()[fd][cd];

        //enroque (si el rey se desplaza dos columnas)
        if (origen instanceof Rey && Math.abs(cd - co) == 2 && fo == fd) {
            boolean realizado = intentarEnroque(origen.getColor(), fo, co, fd, cd);
            if (realizado) {
                actualizarGUI(); //repinta todo desde el modelo
                // Cambio inline: alterna el turno directamente en el flujo del movimiento.
                turno = turno.equals("blanco") ? "negro" : "blanco";
                return;
            }
        }

        //en captura al paso
        boolean esEnPassant = false;
        if (origen instanceof Peon && Math.abs(cd - co) == 1 && destino == null) {
            if (ultimaDoblePeon != null) {
                int filaPeonDoble = ultimaDoblePeon[0];
                int colPeonDoble = ultimaDoblePeon[1];
                int filaCaptura = origen.getColor().equals("blanco") ? fd + 1 : fd - 1;
                if (filaPeonDoble == filaCaptura && colPeonDoble == cd) {
                    logica.getCasillas()[filaPeonDoble][colPeonDoble] = null;
                    esEnPassant = true;
                }
            }
        }

        //movimiento normal o captura en la lógica
        boolean moved = logica.mover(fo, co, fd, cd);
        if (!moved) {
            JOptionPane.showMessageDialog(frm, "Error al mover.");
            return;
        }

        //actualizar movedMap (si mueve rey o torre)
        if (origen instanceof Rey) {
            movedMap.put("rey_" + origen.getColor(), true);
        } else if (origen instanceof Torre) {
            String key = "torre_" + origen.getColor() + "_" + fo + "_" + co;
            movedMap.put(key, true);
        }

        //si fue doble paso de peón, registrar para captura en paso
        if (origen instanceof Peon && Math.abs(fd - fo) == 2) {
            ultimaDoblePeon = new int[]{fd, cd};
        } else {
            ultimaDoblePeon = null;
        }

        //coronación cuando el peon llegue a la ultima fila
        Pieza movedPiece = logica.getCasillas()[fd][cd];
        if (debeCoronar(movedPiece, fd)) {
            realizarPromocion(fd, cd, movedPiece.getColor());
            actualizarGUI(); //repintar estado después de la promoción
            turno = turno.equals("blanco") ? "negro" : "blanco";
            return;
        }
        
        //si fue captura en paso, el modelo ya eliminó el peón; solo repintamos
        if (esEnPassant) {
            // el modelo ya reflejó la captura, repintamos
        }

        //repintar todo
        actualizarGUI();

        //cambiar turno
        turno = turno.equals("blanco") ? "negro" : "blanco";
    }

    //actualizar iconos cuando se hizo enroque
    private void actualizarIconosDespuesEnroque(int fr, int cr, int fd, int cd) {
        // repintamos siempre desde el modelo para evitar inconsistencias
        actualizarGUI();
    }

    //intentar enroque (comprueba usando una copia del tablero)
    private boolean intentarEnroque(String color, int fr, int cr, int fd, int cd) {
        boolean corto = cd > cr;
        int fila = fr;
        int torreCol = corto ? 7 : 0;

        Pieza torreOriginal = logica.getCasillas()[fila][torreCol];
        Pieza reyOriginal = logica.getCasillas()[fr][cr];
        if (!piezasPreparadasParaEnroque(torreOriginal, reyOriginal)) return false;

        String keyRey = "rey_" + color;
        String keyTorre = "torre_" + color + "_" + fila + "_" + torreCol;
        if (algunaPiezaDelEnroqueYaSeMovio(keyRey, keyTorre)) return false;

        int paso = corto ? 1 : -1;
        if (!caminoDelEnroqueLibre(fila, cr, torreCol, paso)) return false;

        LogicaTablero copia = copiarModelo(logica);
        Pieza reyCopia = copia.getCasillas()[fr][cr];
        if (!trayectoriaDelReySegura(copia, reyCopia, color, fila, cr, paso)) return false;

        //si pasa las pruebas, realizamos el enroque en el tablero real
        logica.getCasillas()[fr][cr] = null;
        logica.getCasillas()[fd][cd] = reyOriginal;
        reyOriginal.setPosicion(fd, cd);

        int nuevaColTorre = corto ? cd - 1 : cd + 1;
        logica.getCasillas()[fila][torreCol] = null;
        logica.getCasillas()[fila][nuevaColTorre] = torreOriginal;
        torreOriginal.setPosicion(fila, nuevaColTorre);

        movedMap.put(keyRey, true);
        movedMap.put(keyTorre, true);
        ultimaDoblePeon = null;
        return true;
    }

    private boolean piezasPreparadasParaEnroque(Pieza torre, Pieza rey) {
        return torre instanceof Torre && rey instanceof Rey;
    }

    private boolean algunaPiezaDelEnroqueYaSeMovio(String keyRey, String keyTorre) {
        return Boolean.TRUE.equals(movedMap.get(keyRey))
                || Boolean.TRUE.equals(movedMap.get(keyTorre));
    }

    private boolean caminoDelEnroqueLibre(int fila, int columnaRey, int columnaTorre, int paso) {
        for (int columna = columnaRey + paso; columna != columnaTorre; columna += paso) {
            if (logica.getCasillas()[fila][columna] != null) {
                return false;
            }
        }
        return true;
    }

    private boolean trayectoriaDelReySegura(
            LogicaTablero copia, Pieza rey, String color, int fila, int columnaOrigen, int paso) {
        if (!(rey instanceof Rey)) return false;

        int columnaIntermedia = columnaOrigen + paso;
        int columnaDestino = columnaOrigen + 2 * paso;
        return moverReyEnCopiaSiNoHayJaque(
                    copia, rey, color, fila, columnaOrigen, columnaIntermedia)
                && moverReyEnCopiaSiNoHayJaque(
                    copia, rey, color, fila, columnaIntermedia, columnaDestino);
    }

    private boolean moverReyEnCopiaSiNoHayJaque(
            LogicaTablero copia, Pieza rey, String color, int fila, int columnaOrigen, int columnaDestino) {
        copia.getCasillas()[fila][columnaOrigen] = null;
        copia.getCasillas()[fila][columnaDestino] = rey;
        rey.setPosicion(fila, columnaDestino);
        return !estaEnJaque(copia, color);
    }

    //marcar con borde verde movimientos legales para la pieza seleccionada
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

    //generar movimientos legales
    private List<Movimiento> generarMovimientosLegalesParaPieza(int f, int c) {
        return reglas.generarMovimientosLegalesParaPieza(f, c, ultimaDoblePeon).stream()
                .map(movimiento -> new Movimiento(
                        movimiento.fOrigen, movimiento.cOrigen,
                        movimiento.fDestino, movimiento.cDestino))
                .toList();
    }

    //generar todos los movimientos legales para un color (usado para jaque mate detection)
    private List<Movimiento> generarMovimientosLegalesParaColor(String color) {
        return reglas.generarMovimientosLegalesParaColor(color, ultimaDoblePeon).stream()
                .map(movimiento -> new Movimiento(
                        movimiento.fOrigen, movimiento.cOrigen,
                        movimiento.fDestino, movimiento.cDestino))
                .toList();
    }
    
    private boolean estaEnJaque(LogicaTablero tab, String color) {
        return reglas.estaEnJaque(tab, color);
    }

    private LogicaTablero copiarModelo(LogicaTablero original) {
        return reglas.copiarModelo(original);
    }
    
    //coronación
    private boolean debeCoronar(Pieza pieza, int fila) {
        return pieza instanceof Peon
                && ((pieza.getColor().equals("blanco") && fila == 0)
                || (pieza.getColor().equals("negro") && fila == 7));
    }

    private void realizarPromocion(int f, int c, String color) {
        String[] opciones = {"Reina", "Torre", "Alfil", "Caballo"};
        String seleccion = (String) JOptionPane.showInputDialog(
                frm,
                "Elige pieza para coronacion:",
                "Coronacion",
                JOptionPane.PLAIN_MESSAGE,
                null,
                opciones,
                opciones[0]);
        if (seleccion == null) seleccion = "Reina";

        Pieza nueva;
        switch (seleccion) {
            case "Torre" -> nueva = new Torre(color, f, c);
            case "Alfil" -> nueva = new Alfil(color, f, c);
            case "Caballo" -> nueva = new Caballo(color, f, c);
            default -> nueva = new Reina(color, f, c);
        }
        logica.getCasillas()[f][c] = nueva;

        //actualizamos la vista repintando todo
        actualizarGUI();
    }

    //obtiene un icono para promoción (se mantiene igual)
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
        if (url == null) return null;
        return new ImageIcon(url);
    }

    //obtiene icono por pieza
    private Icon getIconoParaPieza(Pieza p) {
        if (p == null) return null;
        String color = p.getColor(); // "blanco" o "negro"
        boolean blanco = "blanco".equals(color);

        String ruta = null;
        if (p instanceof Peon) {
            ruta = blanco ? "/IMGS/Ficha Peon.png" : "/IMGS/Ficha PeonNegra.png";
        } else if (p instanceof Torre) {
            ruta = blanco ? "/IMGS/Torre.png" : "/IMGS/TorreNegra.png";
        } else if (p instanceof Caballo) {
            ruta = blanco ? "/IMGS/Caballo.png" : "/IMGS/CaballoNegro.png";
        } else if (p instanceof Alfil) {
            ruta = blanco ? "/IMGS/Alfil.png" : "/IMGS/AlfilNegro.png";
        } else if (p instanceof Reina) {
            ruta = blanco ? "/IMGS/Reina.png" : "/IMGS/ReinaNegra.png";
        } else if (p instanceof Rey) {
            ruta = blanco ? "/IMGS/Rey.png" : "/IMGS/ReyNegro.png";
        }

        if (ruta == null) return null;
        java.net.URL url = getClass().getResource(ruta);
        if (url == null) return null;
        return new ImageIcon(url);
    }

    //clase interna de movimiento
    public static class Movimiento {
        public final int fOrigen, cOrigen, fDestino, cDestino;
        public Movimiento(int fo, int co, int fd, int cd) {
            this.fOrigen = fo; this.cOrigen = co; this.fDestino = fd; this.cDestino = cd;
        }
    }

    private void actualizarGUI() {
        Pieza[][] cas = logica.getCasillas();
        //limpia todos los iconos primero
        for (int f = 0; f < 8; f++) {
            for (int c = 0; c < 8; c++) {
                casillas[f][c].setIcon(null);
            }
        }
        //luego se pinta según el modelo
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
