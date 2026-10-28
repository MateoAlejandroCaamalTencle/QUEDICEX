package CM_Tablero;

import Modelo.LogicaTablero;
import Modelo.Rey;
import Modelo.Torre;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

class CMTableroTest {

    private CMTablero tablero;
    private LogicaTablero logica;

    @BeforeEach
    void prepararTablero() throws ReflectiveOperationException {
        tablero = new CMTablero(null);
        Field campoLogica = CMTablero.class.getDeclaredField("logica");
        campoLogica.setAccessible(true);
        logica = (LogicaTablero) campoLogica.get(tablero);
    }

    @Test
    void peonBlancoInicialPuedeAvanzarUnaODosCasillas() throws ReflectiveOperationException {
        List<CMTablero.Movimiento> movimientos = movimientosLegales(6, 0);

        assertEquals(Set.of("5,0", "4,0"), destinos(movimientos));
    }

    @Test
    void movimientoQueExponeAlReyAlJaqueNoEsLegal() throws ReflectiveOperationException {
        limpiarTablero();
        colocar(new Rey("blanco", 7, 4), 7, 4);
        colocar(new Torre("blanco", 6, 4), 6, 4);
        colocar(new Torre("negro", 0, 4), 0, 4);
        colocar(new Rey("negro", 0, 0), 0, 0);

        List<CMTablero.Movimiento> movimientos = movimientosLegales(6, 4);

        assertFalse(destinos(movimientos).contains("6,5"),
                "La torre no debe dejar abierta la columna hacia el rey blanco.");
    }

    @Test
    void generarMovimientosLegalesNoModificaElTableroOriginal() throws ReflectiveOperationException {
        var peonInicial = logica.getCasillas()[6][0];

        movimientosLegales(6, 0);

        assertSame(peonInicial, logica.getCasillas()[6][0]);
        assertEquals(6, peonInicial.getFila());
        assertEquals(0, peonInicial.getColumna());
    }

    @SuppressWarnings("unchecked")
    private List<CMTablero.Movimiento> movimientosLegales(int fila, int columna)
            throws ReflectiveOperationException {
        Method metodo = CMTablero.class.getDeclaredMethod(
                "generarMovimientosLegalesParaPieza", int.class, int.class);
        metodo.setAccessible(true);
        return (List<CMTablero.Movimiento>) metodo.invoke(tablero, fila, columna);
    }

    private Set<String> destinos(List<CMTablero.Movimiento> movimientos) {
        return movimientos.stream()
                .map(movimiento -> movimiento.fDestino + "," + movimiento.cDestino)
                .collect(Collectors.toSet());
    }

    private void limpiarTablero() {
        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                logica.getCasillas()[fila][columna] = null;
            }
        }
    }

    private void colocar(Modelo.Pieza pieza, int fila, int columna) {
        logica.getCasillas()[fila][columna] = pieza;
    }
}
