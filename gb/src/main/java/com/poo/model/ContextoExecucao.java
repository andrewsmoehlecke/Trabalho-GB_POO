package com.poo.model;

import java.io.Serializable;

public class ContextoExecucao implements Serializable {
    public Processo[] fila;
    public int total;
    public int pidCounter;

    public ContextoExecucao(Processo[] fila, int total, int pidCounter) {
        this.fila = fila;
        this.total = total;
        this.pidCounter = pidCounter;
    }
}