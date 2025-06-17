package com.poo.model;

import java.io.Serializable;

public class ContextoExecucao implements Serializable {
    private Processo[] fila;
    private int total;
    private int pidCounter;

    public ContextoExecucao(Processo[] fila, int total, int pidCounter) {
        this.fila = fila;
        this.total = total;
        this.pidCounter = pidCounter;
    }

    public Processo[] getFila() {
        return fila;
    }

    public void setFila(Processo[] fila) {
        this.fila = fila;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void incrementarTotal() {
        this.total++;
    }

    public int getPidCounter() {
        return pidCounter;
    }

    public void incrementarPidCounter() {
        this.pidCounter++;
    }

    public void setPidCounter(int pidCounter) {
        this.pidCounter = pidCounter;
    }
}