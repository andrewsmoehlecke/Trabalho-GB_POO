package com.poo.model;

import java.io.Serializable;

import static com.poo.util.Constantes.*;

public class Expressao implements Serializable  {
    private double operando1;
    private double operando2;
    private String operador;

    public Expressao(String expressao) {
        String[] partes = expressao.trim().split(" ");
        this.operando1 = Double.parseDouble(partes[0]);
        this.operando2 = Double.parseDouble(partes[2]);
        this.operador = partes[1];
    }

    public double calcular() {
        return switch (operador) {
            case OPERADOR_ADICAO -> operando1 + operando2;
            case OPERADOR_SUBTRACAO -> operando1 - operando2;
            case OPERADOR_MULTIPLICACAO -> operando1 * operando2;
            case OPERADOR_DIVISAO -> operando2 != 0 ? operando1 / operando2 : 0;
            default -> throw new IllegalArgumentException("Operador inválido");
        };
    }

    @Override
    public String toString() {
        return operando1 + " " + operador + " " + operando2;
    }
}
