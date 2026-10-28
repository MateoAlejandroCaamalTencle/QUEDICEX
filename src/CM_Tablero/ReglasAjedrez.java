package CM_Tablero;

import Modelo.*;
import java.util.ArrayList;
import java.util.List;

final class ReglasAjedrez {

    private final LogicaTablero logica;

    ReglasAjedrez(LogicaTablero logica) {
        this.logica = logica;
    }

    List<Movimiento> generarMovimientosLegalesParaPieza(int fila, int columna, int[] ultimaDoblePeon) {
        List<Movimiento> movimientos = new ArrayList<>();
        Pieza pieza = logica.getCasillas()[fila][columna];
        if (pieza == null) return movimientos;

        for (int filaDestino = 0; filaDestino < 8; filaDestino++) {
            for (int columnaDestino = 0; columnaDestino < 8; columnaDestino++) {
                if (pieza.movimientoValido(
                        fila, columna, filaDestino, columnaDestino, logica.getCasillas())
                        && esMovimientoLegalEnCopia(
                                pieza, fila, columna, filaDestino, columnaDestino, ultimaDoblePeon)) {
                    movimientos.add(new Movimiento(fila, columna, filaDestino, columnaDestino));
                }
            }
        }
        return movimientos;
    }

    List<Movimiento> generarMovimientosLegalesParaColor(String color, int[] ultimaDoblePeon) {
        List<Movimiento> movimientos = new ArrayList<>();
        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                Pieza pieza = logica.getCasillas()[fila][columna];
                if (pieza != null && pieza.getColor().equals(color)) {
                    movimientos.addAll(
                            generarMovimientosLegalesParaPieza(fila, columna, ultimaDoblePeon));
                }
            }
        }
        return movimientos;
    }

    boolean estaEnJaque(LogicaTablero tablero, String color) {
        int[] posicionRey = encontrarRey(tablero, color);
        if (posicionRey == null) return true;

        int filaRey = posicionRey[0];
        int columnaRey = posicionRey[1];
        String colorEnemigo = color.equals("blanco") ? "negro" : "blanco";
        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                Pieza pieza = tablero.getCasillas()[fila][columna];
                if (pieza != null && pieza.getColor().equals(colorEnemigo)
                        && pieza.movimientoValido(
                                fila, columna, filaRey, columnaRey, tablero.getCasillas())) {
                    return true;
                }
            }
        }
        return false;
    }

    LogicaTablero copiarModelo(LogicaTablero original) {
        LogicaTablero copia = new LogicaTablero();
        Pieza[][] destino = copia.getCasillas();
        Pieza[][] origen = original.getCasillas();
        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                destino[fila][columna] = null;
            }
        }

        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                Pieza pieza = origen[fila][columna];
                if (pieza == null) continue;

                String color = pieza.getColor();
                Pieza copiaPieza = switch (pieza.getClass().getSimpleName()) {
                    case "Peon" -> new Peon(color, fila, columna);
                    case "Torre" -> new Torre(color, fila, columna);
                    case "Caballo" -> new Caballo(color, fila, columna);
                    case "Alfil" -> new Alfil(color, fila, columna);
                    case "Reina" -> new Reina(color, fila, columna);
                    case "Rey" -> new Rey(color, fila, columna);
                    default -> null;
                };
                destino[fila][columna] = copiaPieza;
            }
        }
        return copia;
    }

    private boolean esMovimientoLegalEnCopia(
            Pieza pieza, int filaOrigen, int columnaOrigen,
            int filaDestino, int columnaDestino, int[] ultimaDoblePeon) {
        LogicaTablero copia = copiarModelo(logica);

        if (pieza instanceof Peon && Math.abs(columnaDestino - columnaOrigen) == 1
                && copia.getCasillas()[filaDestino][columnaDestino] == null
                && ultimaDoblePeon != null) {
            int filaPeonDoble = ultimaDoblePeon[0];
            int columnaPeonDoble = ultimaDoblePeon[1];
            int filaCaptura = pieza.getColor().equals("blanco")
                    ? filaDestino + 1 : filaDestino - 1;
            if (filaPeonDoble == filaCaptura && columnaPeonDoble == columnaDestino) {
                copia.getCasillas()[filaPeonDoble][columnaPeonDoble] = null;
            }
        }

        if (!copia.mover(filaOrigen, columnaOrigen, filaDestino, columnaDestino)) return false;
        return !estaEnJaque(copia, pieza.getColor());
    }

    private int[] encontrarRey(LogicaTablero tablero, String color) {
        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                Pieza pieza = tablero.getCasillas()[fila][columna];
                if (pieza instanceof Rey && pieza.getColor().equals(color)) {
                    return new int[]{fila, columna};
                }
            }
        }
        return null;
    }

    static class Movimiento {
        final int fOrigen;
        final int cOrigen;
        final int fDestino;
        final int cDestino;

        Movimiento(int fOrigen, int cOrigen, int fDestino, int cDestino) {
            this.fOrigen = fOrigen;
            this.cOrigen = cOrigen;
            this.fDestino = fDestino;
            this.cDestino = cDestino;
        }
    }
}
