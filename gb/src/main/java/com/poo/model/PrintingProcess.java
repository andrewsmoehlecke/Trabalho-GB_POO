package com.poo.model;

import java.io.Serializable;

public class PrintingProcess extends Processo implements Serializable {
    private static final long serialVersionUID = 1L;

    private ContextoExecucao contexto;

    public PrintingProcess(int pid, ContextoExecucao contexto) {
        super(pid);
        this.contexto = contexto;
    }

    @Override
    public void executar() {
        System.out.println("Fila atual de processos:");
        for (int i = 0; i < contexto.getTotal(); i++) {
            Processo p = contexto.getFila()[i];
            if (p != null)
                System.out.println("PID: " + p.getPid() + " - Tipo: " + p.getClass().getSimpleName());
        }
    }

    public ContextoExecucao getContexto() {
        return contexto;
    }
}
