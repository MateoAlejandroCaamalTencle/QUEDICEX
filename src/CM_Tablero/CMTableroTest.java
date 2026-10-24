package CM_Tablero;

import Modelo.LogicaTablero;
import Modelo.Peon;
import Modelo.Pieza;
import Modelo.Reina;
import Modelo.Torre;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CMTableroTest {

    // ---------------------------------------------------------
    // 1. Prueba para Rename Method (movimientoValido a esMovimientoPseudoLegal)
    // ---------------------------------------------------------
    @Test
    void reinaDebeValidarMovimientosConElNuevoNombreDelMetodo() {
        // Arrange
        Pieza[][] tablero = new Pieza[8][8];
        Reina reina = new Reina("blanco", 4, 4);
        tablero[4][4] = reina;
        tablero[4][6] = new Torre("negro", 4, 6); // Pieza bloqueando el camino

        // Act
        boolean movimientoHorizontalValido = reina.esMovimientoPseudoLegal(4, 4, 4, 5, tablero);
        boolean movimientoBloqueado = reina.esMovimientoPseudoLegal(4, 4, 4, 7, tablero);

        // Assert
        assertTrue(movimientoHorizontalValido, "La reina debe poder moverse a una casilla libre");
        assertFalse(movimientoBloqueado, "La reina no debe poder saltar sobre una pieza en su camino");
    }

    // ---------------------------------------------------------
    // 2. Prueba para Replace Conditional with Polymorphism
    // ---------------------------------------------------------
    @Test
    void piezasDebenDevolverSuIconoYClonarseUsandoPolimorfismo() {
        // Arrange
        Pieza peon = new Peon("blanco", 6, 0);

        // Act
        String rutaIcono = peon.getRutaIcono();
        Pieza peonClonado = peon.clonar();

        // Assert
        assertEquals("/IMGS/Ficha Peon.png", rutaIcono, "El peón debe devolver su ruta específica sin usar switch ni instanceof");
        assertTrue(peonClonado instanceof Peon, "El método clonar debe devolver una instancia exacta de Peon");
        assertEquals("blanco", peonClonado.getColor(), "El clon debe conservar el color original");
        assertEquals(6, peonClonado.getFila(), "El clon debe conservar la fila original");
    }

    // ---------------------------------------------------------
    // 3. Prueba para Move Field (turno y movedMap movidos a LogicaTablero)
    // ---------------------------------------------------------
    @Test
    void logicaTableroDebeGestionarElTurnoYElEstadoDeMovimiento() {
        // Arrange
        LogicaTablero logica = new LogicaTablero();

        // Act
        String turnoInicial = logica.getTurno();
        logica.alternarTurno();
        String turnoCambiado = logica.getTurno();
        
        boolean estadoReyBlanco = logica.getMovedMap().get("rey_blanco");

        // Assert
        assertEquals("blanco", turnoInicial, "El turno inicial alojado en el modelo debe ser blanco");
        assertEquals("negro", turnoCambiado, "El modelo debe alternar el turno correctamente a negro");
        assertFalse(estadoReyBlanco, "El estado inicial de movimiento del rey en el mapa debe ser falso");
    }
}
