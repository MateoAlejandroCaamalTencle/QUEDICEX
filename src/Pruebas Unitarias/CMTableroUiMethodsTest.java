package CM_Tablero;

import Modelo.LogicaTablero;
import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CMTableroUiMethodsTest {

    private CMTablero controlador;
    private LogicaTablero logica;
    private JButton[][] casillas;

    @BeforeEach
    void prepararControlador() throws ReflectiveOperationException {
        controlador = new CMTablero(null);
        logica = (LogicaTablero) obtenerCampo("logica").get(controlador);
        casillas = new JButton[8][8];
        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                casillas[fila][columna] = new JButton();
            }
        }
        obtenerCampo("casillas").set(controlador, casillas);
    }

    @Test
    void clicEnCasillaSeleccionaLaPiezaYResaltaMovimientosLegales()
            throws ReflectiveOperationException {
        invocar("clicEnCasilla", new Class<?>[]{int.class, int.class}, 6, 0);

        assertEquals(Color.YELLOW, ((LineBorder) casillas[6][0].getBorder()).getLineColor(),
                "La casilla de origen debe resaltarse en amarillo.");
        assertEquals(Color.GREEN, ((LineBorder) casillas[5][0].getBorder()).getLineColor(),
                "El destino legal de una casilla debe resaltarse en verde.");
        assertEquals(Color.GREEN, ((LineBorder) casillas[4][0].getBorder()).getLineColor(),
                "El destino legal de dos casillas debe resaltarse en verde.");
        assertNotNull(logica.getCasillas()[6][0],
                "Seleccionar una casilla no debe mover la pieza.");
        assertNull(logica.getCasillas()[5][0]);
    }

    @Test
    void clicEnCasillaMueveLaPiezaAlDestinoLegal() throws ReflectiveOperationException {
        invocar("clicEnCasilla", new Class<?>[]{int.class, int.class}, 6, 0);
        invocar("clicEnCasilla", new Class<?>[]{int.class, int.class}, 5, 0);

        assertNull(logica.getCasillas()[6][0], "La casilla de origen debe quedar vacía.");
        assertNotNull(logica.getCasillas()[5][0], "La pieza debe quedar en el destino.");
        assertNotNull(casillas[5][0].getIcon(), "La interfaz debe mostrar la pieza movida.");
        assertNull(casillas[6][0].getIcon(), "La interfaz debe limpiar la casilla de origen.");
    }

    @Test
    void actualizarGuiLimpiaCasillasVaciasYPintaLasOcupadas()
            throws ReflectiveOperationException {
        Icon iconoAnterior = new ImageIcon();
        for (JButton[] fila : casillas) {
            for (JButton casilla : fila) {
                casilla.setIcon(iconoAnterior);
            }
        }

        invocar("actualizarGUI", new Class<?>[0]);

        assertNull(casillas[4][4].getIcon(), "Una casilla vacía debe quedar sin icono.");
        assertNotNull(casillas[6][0].getIcon(), "Una casilla ocupada debe mostrar su pieza.");
        assertNotNull(casillas[0][0].getIcon(), "También deben pintarse las piezas negras.");
    }

    private Field obtenerCampo(String nombre) throws ReflectiveOperationException {
        Field campo = CMTablero.class.getDeclaredField(nombre);
        campo.setAccessible(true);
        return campo;
    }

    private Object invocar(String nombre, Class<?>[] tipos, Object... argumentos)
            throws ReflectiveOperationException {
        Method metodo = CMTablero.class.getDeclaredMethod(nombre, tipos);
        metodo.setAccessible(true);
        return metodo.invoke(controlador, argumentos);
    }
}
