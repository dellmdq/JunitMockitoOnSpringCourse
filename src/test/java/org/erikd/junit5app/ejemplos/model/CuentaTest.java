package org.erikd.junit5app.ejemplos.model;

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
        String esperado = "Pepe Albistu";

        //assertEquals(esperado,cuenta.getPersona());
        assertTrue(cuenta.getPersona().equals(esperado));

    }


}