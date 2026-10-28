package CM_Tablero;

import Modelo.LogicaTablero;
import Modelo.Rey;
import Modelo.Torre;
import Modelo.Pieza;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnroqueCaracterizacionTest {

    private CMTablero controlador;
    private LogicaTablero logica;

    @BeforeEach
    void prepararTablero() throws ReflectiveOperationException {
        controlador = new CMTablero(null);
        logica = (LogicaTablero) obtenerCampo("logica").get(controlador);
        limpiarTablero();
        colocar(new Rey("blanco", 7, 4), 7, 4);
        colocar(new Torre("blanco", 7, 7), 7, 7);
        colocar(new Torre("blanco", 7, 0), 7, 0);
        colocar(new Rey("negro", 0, 4), 0, 4);
        @SuppressWarnings("unchecked")
        Map<String, Boolean> movedMap = (Map<String, Boolean>) obtenerCampo("movedMap").get(controlador);
        movedMap.replaceAll((key, value) -> false);
    }

    @Test
    void reyPermiteEnroqueCortoYLargoCuandoTorreYCaminoEstanLibres() {
        Pieza[][] tablero = logica.getCasillas();
        Rey rey = (Rey) tablero[7][4];

        assertTrue(rey.movimientoValido(7, 4, 7, 6, tablero));
        assertTrue(rey.movimientoValido(7, 4, 7, 2, tablero));
    }

    @Test
    void reyRechazaEnroqueSiElReySeMovio() {
        Rey rey = (Rey) logica.getCasillas()[7][4];
        rey.setSeMovio(true);

        assertFalse(rey.movimientoValido(7, 4, 7, 6, logica.getCasillas()));
    }

    @Test
    void reyRechazaEnroqueSiLaTorreSeMovio() {
        Torre torre = (Torre) logica.getCasillas()[7][7];
        torre.setSeMovio(true);

        assertFalse(((Rey) logica.getCasillas()[7][4])
                .movimientoValido(7, 4, 7, 6, logica.getCasillas()));
    }

    @Test
    void reyRechazaEnroqueSiUnaCasillaDelCaminoEstaOcupada() {
        logica.getCasillas()[7][5] = new Torre("blanco", 7, 5);

        assertFalse(((Rey) logica.getCasillas()[7][4])
                .movimientoValido(7, 4, 7, 6, logica.getCasillas()));

        logica.getCasillas()[7][5] = null;
        logica.getCasillas()[7][1] = new Torre("blanco", 7, 1);
        assertFalse(((Rey) logica.getCasillas()[7][4])
                .movimientoValido(7, 4, 7, 2, logica.getCasillas()));
    }

    @Test
    void controladorRealizaEnroqueCortoYDesplazaLaTorre() throws ReflectiveOperationException {
        Pieza rey = logica.getCasillas()[7][4];
        boolean realizado = intentarEnroque(4, 6);

        assertTrue(realizado);
        assertNull(logica.getCasillas()[7][4]);
        assertSame(rey, logica.getCasillas()[7][6]);
        assertTrue(logica.getCasillas()[7][5] instanceof Torre);
        assertNull(logica.getCasillas()[7][7]);
    }

    @Test
    void controladorRealizaEnroqueLargoYDesplazaLaTorre() throws ReflectiveOperationException {
        Pieza rey = logica.getCasillas()[7][4];
        boolean realizado = intentarEnroque(4, 2);

        assertTrue(realizado);
        assertNull(logica.getCasillas()[7][4]);
        assertSame(rey, logica.getCasillas()[7][2]);
        assertTrue(logica.getCasillas()[7][3] instanceof Torre);
        assertNull(logica.getCasillas()[7][0]);
    }

    @Test
    void controladorRechazaEnroqueSiUnCaminoEstaBloqueado() throws ReflectiveOperationException {
        Pieza bloqueadora = new Torre("blanco", 7, 5);
        logica.getCasillas()[7][5] = bloqueadora;

        assertFalse(intentarEnroque(4, 6));
        assertSame(bloqueadora, logica.getCasillas()[7][5]);
        assertTrue(logica.getCasillas()[7][4] instanceof Rey);
        assertTrue(logica.getCasillas()[7][7] instanceof Torre);
    }

    @Test
    void controladorRechazaEnroqueSiElReyOTorreYaSeMovieron()
            throws ReflectiveOperationException {
        @SuppressWarnings("unchecked")
        Map<String, Boolean> movedMap = (Map<String, Boolean>) obtenerCampo("movedMap").get(controlador);

        movedMap.put("rey_blanco", true);
        assertFalse(intentarEnroque(4, 6));

        movedMap.put("rey_blanco", false);
        movedMap.put("torre_blanco_7_7", true);
        assertFalse(intentarEnroque(4, 6));
    }

    @Test
    void controladorRechazaEnroqueSiLaCasillaIntermediaEstaAtacada()
            throws ReflectiveOperationException {
        logica.getCasillas()[0][5] = new Torre("negro", 0, 5);

        assertFalse(intentarEnroque(4, 6));
        assertTrue(logica.getCasillas()[7][4] instanceof Rey);
        assertTrue(logica.getCasillas()[7][7] instanceof Torre);
    }

    private boolean intentarEnroque(int columnaOrigen, int columnaDestino)
            throws ReflectiveOperationException {
        Method metodo = CMTablero.class.getDeclaredMethod(
                "intentarEnroque", String.class, int.class, int.class, int.class, int.class);
        metodo.setAccessible(true);
        return (boolean) metodo.invoke(controlador, "blanco", 7, columnaOrigen, 7, columnaDestino);
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
