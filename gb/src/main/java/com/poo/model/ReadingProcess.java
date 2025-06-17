package com.poo.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.poo.util.Constantes.ARQUIVO_COMPUTACAO;

public class ReadingProcess extends Processo implements Serializable {
    private static final long serialVersionUID = 1L;

    private ContextoExecucao contexto;

    public ReadingProcess(int pid, ContextoExecucao contexto) {
        super(pid);
        this.contexto = contexto;
    }

    @Override
    public void executar() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_COMPUTACAO))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (contexto.getTotal() < contexto.getFila().length) {
                    contexto.getFila()[contexto.getTotal()] = new ComputingProcess(contexto.getPidCounter(), linha);
                    contexto.incrementarTotal();
                    contexto.incrementarPidCounter();
                }
            }
            new PrintWriter(ARQUIVO_COMPUTACAO).close();
            System.out.println("Expressões carregadas e computações criadas.");
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo de computações: " + e.getMessage());
        }
    }

    public ContextoExecucao getContexto() {
        return contexto;
    }
}