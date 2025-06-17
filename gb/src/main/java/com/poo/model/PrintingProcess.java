package com.poo.model;

import java.io.Serializable;
import java.util.List;

public class PrintingProcess extends Processo implements Serializable {
    private ContextoExecucao contexto;

    public PrintingProcess(int pid, ContextoExecucao contexto) {
        super(pid);
        this.contexto = contexto;
    }

    @Override
    public void executar() {
        System.out.println("Fila atual de processos:");
        for (int i = 0; i < contexto.total; i++) {
            Processo p = contexto.fila[i];
            System.out.println("PID: " + p.getPid() + " - Tipo: " + p.getClass().getSimpleName());
        }
    }

    public ContextoExecucao getContexto() {
        return contexto;
    }
}
