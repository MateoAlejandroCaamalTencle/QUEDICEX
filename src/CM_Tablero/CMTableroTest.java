package CM_Tablero;

import Modelo.LogicaTablero;
import Modelo.Peon;
import Modelo.Pieza;
import Modelo.Reina;
import Modelo.Rey;
import Modelo.Torre;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import javax.swing.JButton;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CMTableroTest {

     @Test //Inline Temp
    void reinaValidaMovimientosRectosDelegandoEnTorre() {
        Pieza[][] tablero = new Pieza[8][8];
        Reina reina = new Reina("blanco", 4, 4);

        assertTrue(reina.movimientoValido(4, 4, 4, 7, tablero),
                "La reina debe poder moverse horizontalmente si el camino está libre");
        assertTrue(reina.movimientoValido(4, 4, 1, 4, tablero),
                "La reina debe poder moverse verticalmente si el camino está libre");

        tablero[4][6] = new Torre("negro", 4, 6);
        assertFalse(reina.movimientoValido(4, 4, 4, 7, tablero),
                "La reina no debe poder saltar sobre una pieza en su camino");
    }

    @Test //REPLACE WITH QUERY
    void debeDetectarQueElReyBlancoEstaEnJaque() throws Exception {
        LogicaTablero tablero = new LogicaTablero();

        tablero.getCasillas()[7][4] = null;
        tablero.getCasillas()[7][4] = new Rey("blanco", 7, 4);

        tablero.getCasillas()[7][3] = null;
        tablero.getCasillas()[7][2] = null;
        
        tablero.getCasillas()[7][1] = null;
        tablero.getCasillas()[7][0] = new Torre("negro", 7, 0);

        CMTablero juego = new CMTablero(null);

        Method metodo = CMTablero.class.getDeclaredMethod("estaEnJaque", LogicaTablero.class, String.class);
        metodo.setAccessible(true);

        boolean enJaque = (boolean) metodo.invoke(juego, tablero, "blanco");

        assertTrue(enJaque, "El rey blanco debe estar en jaque si una torre negra lo amenaza");
    }

    @Test //Extrac Method
    void generaMovimientosLegalesParaPeonBlanco() throws Exception {
        CMTablero juego = new CMTablero(null);
        Method metodo = CMTablero.class.getDeclaredMethod(
                "generarMovimientosLegalesParaPieza", int.class, int.class);
        metodo.setAccessible(true);

        List<?> movimientos = (List<?>) metodo.invoke(juego, 6, 0);

        assertEquals(2, movimientos.size(),
                "El peon blanco inicial debe tener dos avances legales");
        assertTrue(movimientos.stream()
                .map(CMTablero.Movimiento.class::cast)
                .anyMatch(movimiento -> movimiento.fDestino == 5 && movimiento.cDestino == 0));
        assertTrue(movimientos.stream()
                .map(CMTablero.Movimiento.class::cast)
                .anyMatch(movimiento -> movimiento.fDestino == 4 && movimiento.cDestino == 0));
    }

        @Test //Inline Method
        void actualizaBordesDeCasillasYLosLimpia() throws Exception {
                CMTablero juego = new CMTablero(null);
                JButton[][] casillas = prepararCasillas(juego);

                Method marcar = CMTablero.class.getDeclaredMethod("marcarMovimientosLegales", List.class);
                marcar.setAccessible(true);
                marcar.invoke(juego, List.of(new CMTablero.Movimiento(6, 0, 5, 0)));

                LineBorder bordeVerde = (LineBorder) casillas[5][0].getBorder();
                assertEquals(Color.GREEN, bordeVerde.getLineColor());
                assertEquals(3, bordeVerde.getThickness());

                Method resaltar = CMTablero.class.getDeclaredMethod("resaltarCasilla", int.class, int.class);
                resaltar.setAccessible(true);
                resaltar.invoke(juego, 4, 4);

                LineBorder bordeAmarillo = (LineBorder) casillas[4][4].getBorder();
                assertEquals(Color.YELLOW, bordeAmarillo.getLineColor());
                assertEquals(3, bordeAmarillo.getThickness());

                Method limpiar = CMTablero.class.getDeclaredMethod("limpiarResaltados");
                limpiar.setAccessible(true);
                limpiar.invoke(juego);

                assertTrue(casillas[5][0].getBorder() instanceof EtchedBorder);
                assertTrue(casillas[4][4].getBorder() instanceof EtchedBorder);
        }

        private JButton[][] prepararCasillas(CMTablero juego) throws Exception {
                JButton[][] casillas = new JButton[8][8];
                for (int fila = 0; fila < 8; fila++) {
                        for (int columna = 0; columna < 8; columna++) {
                                casillas[fila][columna] = new JButton();
                        }
                }

                Field campoCasillas = CMTablero.class.getDeclaredField("casillas");
                campoCasillas.setAccessible(true);
                campoCasillas.set(juego, casillas);
                return casillas;
        }

        @Test
        void cambiaTurnoDespuesDeCadaMovimiento() throws Exception {
                CMTablero juego = new CMTablero(null);
                prepararCasillas(juego);
                Field turno = CMTablero.class.getDeclaredField("turno");
                turno.setAccessible(true);
                Method ejecutarMovimiento = CMTablero.class.getDeclaredMethod(
                        "ejecutarMovimientoConEspeciales",
                        int.class, int.class, int.class, int.class);
                ejecutarMovimiento.setAccessible(true);

                assertEquals("blanco", turno.get(juego));
                ejecutarMovimiento.invoke(juego, 6, 0, 5, 0);
                assertEquals("negro", turno.get(juego));
                ejecutarMovimiento.invoke(juego, 1, 0, 2, 0);
                assertEquals("blanco", turno.get(juego));
        }

        @Test //Explaining Variable
        void determinaSiElPeonDebeCoronarSegunColorYFila() throws Exception {
                CMTablero juego = new CMTablero(null);
                Method debeCoronar = CMTablero.class.getDeclaredMethod(
                        "debeCoronar", Pieza.class, int.class);
                debeCoronar.setAccessible(true);

                assertTrue((boolean) debeCoronar.invoke(juego, new Peon("blanco", 1, 0), 0));
                assertTrue((boolean) debeCoronar.invoke(juego, new Peon("negro", 6, 0), 7));
                assertFalse((boolean) debeCoronar.invoke(juego, new Peon("blanco", 1, 0), 7));
                assertFalse((boolean) debeCoronar.invoke(juego, new Torre("blanco", 1, 0), 0));
        }

}

