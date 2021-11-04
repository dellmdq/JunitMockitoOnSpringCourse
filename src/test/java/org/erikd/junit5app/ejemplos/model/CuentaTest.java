package org.erikd.junit5app.ejemplos.model;

import org.erikd.junit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    //los test deben quedar encapsulados dentro del contexto de pruebas.
    //de ahi el modificador de acceso es default
    //para pruebas unitarias se separan las palabras con guion bajo

    @Test
    void testNombreCuenta(){
        Cuenta cuenta = new Cuenta("Pepe Albistur",new BigDecimal("1000.12345"));
        //cuenta.setPersona("Pepe Albistur");
        String esperado = "Pepe Albistur";
        assertNotNull(cuenta.getSaldo());
        assertEquals(esperado,cuenta.getPersona());
        assertTrue(cuenta.getPersona().equals(esperado));

    }

    @Test
    void testSaldoCuenta(){
        Cuenta cuenta = new Cuenta("Pepe", new BigDecimal("1000.12345"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345,cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);//chequendo que la cuenta tenga saldo mayor a cero
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);//validando lo mismo al revés. chequendo que la cuenta tenga saldo mayor a cero
    }

    @Test
    void testReferenciaCuenta() {//testeando por instancia
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("8900.9998"));//real
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9998"));//esperado
        assertNotNull(cuenta.getSaldo());
//        assertNotEquals(cuenta2, cuenta);
        assertEquals(cuenta2,cuenta);//falla porque son dos instancias distintas
    }
    /***
     *  Aqui vemos primero el test y después el desarrollo necesario en el metodo
     *  para cumplir con el resultado exigido por el método. Esto es Test Driven Development.TDD.
     *  Falta la implementacion de los metodos.
     */
    @Test
    void testDebitoCuenta(){
        Cuenta cuenta = new Cuenta("Marce Pala", new BigDecimal("1000.12345"));
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345",cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta(){
        Cuenta cuenta = new Cuenta("Marce Pala", new BigDecimal("1000.12345"));
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345",cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteExceptionCuenta() {
        Cuenta cuenta = new Cuenta("Marce Pala",new BigDecimal("1000.12345"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
           cuenta.debito(new BigDecimal(1500));
        });
        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado, actual);
    }
}