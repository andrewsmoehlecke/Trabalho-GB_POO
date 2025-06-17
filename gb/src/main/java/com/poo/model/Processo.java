package com.poo.model;

import java.io.Serializable;

public abstract class Processo implements Serializable {
    protected int pid;

    public Processo(int pid) {
        this.pid = pid;
    }

    public int getPid() {
        return pid;
    }

    public abstract void executar();
}
