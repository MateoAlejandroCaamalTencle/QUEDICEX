package CM_Tablero;

import Modelo.LogicaTablero;
import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CMTableroUiMethodsRefactorizedTest {

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
    void clicEnCasillaEsPrivadoYConservaLaSeleccionDeLaPieza()
            throws ReflectiveOperationException {
        Method clicEnCasilla = obtenerMetodo("clicEnCasilla", int.class, int.class);

        assertTrue(Modifier.isPrivate(clicEnCasilla.getModifiers()),
                "clicEnCasilla debe ser privado después de Hide Method.");
        clicEnCasilla.invoke(controlador, 6, 0);

        assertEquals(Color.YELLOW, ((LineBorder) casillas[6][0].getBorder()).getLineColor());
        assertEquals(Color.GREEN, ((LineBorder) casillas[5][0].getBorder()).getLineColor());
        assertNotNull(logica.getCasillas()[6][0]);
        assertNull(logica.getCasillas()[5][0]);
    }

    @Test
    void actualizarGuiEsPrivadoYConservaElRepintadoDelTablero()
            throws ReflectiveOperationException {
        Method actualizarGUI = obtenerMetodo("actualizarGUI");

        assertTrue(Modifier.isPrivate(actualizarGUI.getModifiers()),
                "actualizarGUI debe ser privado después de Hide Method.");
        actualizarGUI.invoke(controlador);

        assertNotNull(casillas[6][0].getIcon(), "Debe pintar las piezas blancas.");
        assertNotNull(casillas[0][0].getIcon(), "Debe pintar las piezas negras.");
        assertNull(casillas[4][4].getIcon(), "Las casillas vacías deben quedar sin icono.");
    }

    private Field obtenerCampo(String nombre) throws ReflectiveOperationException {
        Field campo = CMTablero.class.getDeclaredField(nombre);
        campo.setAccessible(true);
        return campo;
    }

    private Method obtenerMetodo(String nombre, Class<?>... tipos)
            throws ReflectiveOperationException {
        Method metodo = CMTablero.class.getDeclaredMethod(nombre, tipos);
        metodo.setAccessible(true);
        return metodo;
    }
}
