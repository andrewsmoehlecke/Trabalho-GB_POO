package com.poo.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

import static com.poo.util.Constantes.ARQUIVO_COMPUTACAO;

public class WritingProcess extends Processo implements Serializable  {
    private Expressao expressao;

    public WritingProcess(int pid, String exp) {
        super(pid);
        this.expressao = new Expressao(exp);
    }

    @Override
    public void executar() {
        try (FileWriter writer = new FileWriter(ARQUIVO_COMPUTACAO, true)) {
            writer.write(expressao.toString() + "\n");
            System.out.println("Expressão gravada.");
        } catch (IOException e) {
            System.out.println("Erro ao gravar no arquivo.");
        }
    }
}