package CM_Tablero;

import Modelo.LogicaTablero;
import Modelo.Pieza;
import Modelo.Rey;
import Modelo.Torre;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReglasAjedrezRefactorizadasTest {

    private LogicaTablero logica;
    private ReglasAjedrez reglas;

    @BeforeEach
    void prepararReglas() {
        logica = new LogicaTablero();
        reglas = new ReglasAjedrez(logica);
    }

    @Test
    void generaMovimientosLegalesDelPeonInicial() {
        List<ReglasAjedrez.Movimiento> movimientos =
                reglas.generarMovimientosLegalesParaPieza(6, 0, null);

        assertEquals(Set.of("5,0", "4,0"), destinos(movimientos));
    }

    @Test
    void generaMovimientosLegalesParaTodasLasPiezasDelColor() {
        assertEquals(20, reglas.generarMovimientosLegalesParaColor("blanco", null).size());
    }

    @Test
    void rechazaMovimientoQueExponeAlReyAlJaque() {
        limpiarTablero();
        colocar(new Rey("blanco", 7, 4), 7, 4);
        colocar(new Torre("blanco", 6, 4), 6, 4);
        colocar(new Torre("negro", 0, 4), 0, 4);
        colocar(new Rey("negro", 0, 0), 0, 0);

        List<ReglasAjedrez.Movimiento> movimientos =
                reglas.generarMovimientosLegalesParaPieza(6, 4, null);

        assertFalse(destinos(movimientos).contains("6,5"));
    }

    @Test
    void simularMovimientosNoModificaElTableroOriginal() {
        Pieza peon = logica.getCasillas()[6][0];

        reglas.generarMovimientosLegalesParaPieza(6, 0, null);

        assertSame(peon, logica.getCasillas()[6][0]);
        assertEquals(6, peon.getFila());
        assertEquals(0, peon.getColumna());
    }

    @Test
    void detectaJaqueAlReyBlanco() {
        limpiarTablero();
        colocar(new Rey("blanco", 7, 4), 7, 4);
        colocar(new Torre("negro", 0, 4), 0, 4);
        colocar(new Rey("negro", 0, 0), 0, 0);

        assertTrue(reglas.estaEnJaque(logica, "blanco"));
    }

    @Test
    void creaUnaCopiaIndependienteDelTablero() {
        Pieza reyOriginal = logica.getCasillas()[7][4];

        LogicaTablero copia = reglas.copiarModelo(logica);

        assertNotSame(logica.getCasillas(), copia.getCasillas());
        assertNotSame(reyOriginal, copia.getCasillas()[7][4]);
        assertEquals(reyOriginal.getColor(), copia.getCasillas()[7][4].getColor());
        assertEquals(reyOriginal.getFila(), copia.getCasillas()[7][4].getFila());
        assertSame(reyOriginal, logica.getCasillas()[7][4]);
    }

    private Set<String> destinos(List<ReglasAjedrez.Movimiento> movimientos) {
        return movimientos.stream()
                .map(movimiento -> movimiento.fDestino + "," + movimiento.cDestino)
                .collect(Collectors.toSet());
    }

    private void limpiarTablero() {
        for (Pieza[] fila : logica.getCasillas()) {
            java.util.Arrays.fill(fila, null);
        }
    }

    private void colocar(Pieza pieza, int fila, int columna) {
        logica.getCasillas()[fila][columna] = pieza;
    }
}
