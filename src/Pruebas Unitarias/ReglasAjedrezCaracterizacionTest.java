package CM_Tablero;

import Modelo.LogicaTablero;
import Modelo.Pieza;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReglasAjedrezCaracterizacionTest {

    private CMTablero controlador;
    private LogicaTablero logica;

    @BeforeEach
    void prepararControlador() throws ReflectiveOperationException {
        controlador = new CMTablero(null);
        logica = (LogicaTablero) obtenerCampo("logica").get(controlador);
    }

    @Test
    void generaMovimientosLegalesDelPeonInicial() throws ReflectiveOperationException {
        List<CMTablero.Movimiento> movimientos = movimientosLegales(6, 0);

        assertEquals(Set.of("5,0", "4,0"), destinos(movimientos));
    }

    @Test
    void generaMovimientosLegalesParaTodasLasPiezasDelColor() throws ReflectiveOperationException {
        Method metodo = CMTablero.class.getDeclaredMethod(
                "generarMovimientosLegalesParaColor", String.class);
        metodo.setAccessible(true);

        List<?> movimientos = (List<?>) metodo.invoke(controlador, "blanco");

        assertEquals(20, movimientos.size());
    }

    @Test
    void rechazaMovimientoQueExponeAlReyAlJaque() throws ReflectiveOperationException {
        limpiarTablero();
        colocar(new Rey("blanco", 7, 4), 7, 4);
        colocar(new Torre("blanco", 6, 4), 6, 4);
        colocar(new Torre("negro", 0, 4), 0, 4);
        colocar(new Rey("negro", 0, 0), 0, 0);

        List<CMTablero.Movimiento> movimientos = movimientosLegales(6, 4);

        assertFalse(destinos(movimientos).contains("6,5"));
    }

    @Test
    void simularMovimientosNoModificaElTableroOriginal() throws ReflectiveOperationException {
        Pieza peon = logica.getCasillas()[6][0];

        movimientosLegales(6, 0);

        assertSame(peon, logica.getCasillas()[6][0]);
        assertEquals(6, peon.getFila());
        assertEquals(0, peon.getColumna());
    }

    @Test
    void detectaJaqueAlReyBlanco() throws ReflectiveOperationException {
        limpiarTablero();
        colocar(new Rey("blanco", 7, 4), 7, 4);
        colocar(new Torre("negro", 0, 4), 0, 4);
        colocar(new Rey("negro", 0, 0), 0, 0);

        assertTrue(estaEnJaque("blanco"));
    }

    @SuppressWarnings("unchecked")
    private List<CMTablero.Movimiento> movimientosLegales(int fila, int columna)
            throws ReflectiveOperationException {
        Method metodo = CMTablero.class.getDeclaredMethod(
                "generarMovimientosLegalesParaPieza", int.class, int.class);
        metodo.setAccessible(true);
        return (List<CMTablero.Movimiento>) metodo.invoke(controlador, fila, columna);
    }

    private boolean estaEnJaque(String color) throws ReflectiveOperationException {
        Method metodo = CMTablero.class.getDeclaredMethod(
                "estaEnJaque", LogicaTablero.class, String.class);
        metodo.setAccessible(true);
        return (boolean) metodo.invoke(controlador, logica, color);
    }

    private Set<String> destinos(List<CMTablero.Movimiento> movimientos) {
        return movimientos.stream()
                .map(movimiento -> movimiento.fDestino + "," + movimiento.cDestino)
                .collect(Collectors.toSet());
    }

    private Field obtenerCampo(String nombre) throws ReflectiveOperationException {
        Field campo = CMTablero.class.getDeclaredField(nombre);
        campo.setAccessible(true);
        return campo;
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
