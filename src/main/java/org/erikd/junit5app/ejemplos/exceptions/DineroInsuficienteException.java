package org.erikd.junit5app.ejemplos.exceptions;

public class DineroInsuficienteException extends RuntimeException{
    public DineroInsuficienteException(String message){
        super(message);
    }

}
