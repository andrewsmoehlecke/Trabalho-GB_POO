package com.poo.model;

import java.io.Serializable;

public class ComputingProcess extends Processo implements Serializable  {
    private Expressao expressao;

    public ComputingProcess(int pid, String exp) {
        super(pid);
        this.expressao = new Expressao(exp);
    }

    @Override
    public void executar() {
        System.out.printf("Expressão: %s = %s\n", getExpressaoString(), expressao.calcular());
    }

    public String getExpressaoString() {
        return expressao.toString();
    }
}
