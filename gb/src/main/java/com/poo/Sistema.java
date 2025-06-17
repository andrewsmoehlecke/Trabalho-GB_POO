package com.poo;

import com.poo.model.*;

import java.io.*;
import java.util.*;

import static com.poo.util.Constantes.*;

public class Sistema {
    private static Processo[] fila = new Processo[MAX_PROCESSOS];
    private static int total = 0;
    private static int pidCounter = PID_INICIAL;

    public static void main(String[] args) {
        limparTerminal();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("""
            \nSistema de Gerenciamento de Processos
                1. Criar processo
                2. Executar próximo
                3. Executar processo por PID
                4. Salvar fila em arquivo
                5. Carregar fila do arquivo
                6. Mostrar fila
                0. Sair \n""");

            int op = sc.nextInt(); sc.nextLine();
            switch (op) {
                case 1 -> criarProcesso(sc);
                case 2 -> executarProximo();
                case 3 -> executarPorPid(sc);
                case 4 -> salvarFila();
                case 5 -> carregarFila();
                case 6 -> mostrarFila();
                case 0 -> System.exit(0);
            }
        }
    }

    private static void mostrarFila() {
        if (total == 0) {
            System.out.println("Fila vazia.");
            return;
        }
        for (int i = 0; i < total; i++) {
            System.out.printf("PID: %d - Tipo: %s%n", fila[i].getPid(), fila[i].getClass().getSimpleName());
        }
    }

    private static void criarProcesso(Scanner sc) {
        if (total >= MAX_PROCESSOS) {
            System.out.println("Fila cheia.");
            return;
        }

        System.out.println("Tipo do processo (1-Calculo, 2-Gravacao, 3-Leitura, 4-Impressao): ");
        int tipo = sc.nextInt(); sc.nextLine();

        Processo proc = null;

        switch (tipo) {
            case 1 -> {
                System.out.print("Informe expressão (ex: 2 + 2): ");
                String exp = sc.nextLine();
                proc = new ComputingProcess(pidCounter++, exp);
                System.out.printf("Processo (ComputingProcess) criado com PID: %d\n", proc.getPid());
            }
            case 2 -> {
                System.out.print("Informe expressão para gravar (ex: 2 + 2): ");
                String exp = sc.nextLine();
                proc = new WritingProcess(pidCounter++, exp);
                System.out.printf("Processo (WritingProcess) criado com PID: %d\n", proc.getPid());
            }
            case 3 -> {
                proc = new ReadingProcess(
                        pidCounter++,
                        new ContextoExecucao(fila, total, pidCounter)
                );
                System.out.printf("Processo (ReadingProcess) criado com PID: %d%s", proc.getPid(), "\n");
            }
            case 4 -> {
                proc = new PrintingProcess(
                        pidCounter++,
                        new ContextoExecucao(fila, total, pidCounter)
                );
                System.out.printf("Processo (PrintingProcess) criado com PID: %d%s", proc.getPid(), "\n");
            }
        }

        if (proc == null) {
            System.out.println("Tipo de processo inválido.");
            return;
        }

        fila[total++] = proc;
    }

    private static void executarProximo() {
        if (total == 0) {
            System.out.println("Fila vazia.");
            return;
        }

        Processo processo = fila[0];
        processo.executar();

        if (processo instanceof ReadingProcess rp) {
            atualizarContextoExecucao(rp.getContexto());
        } else {
            atualizarPidCounter();
        }

        System.arraycopy(fila, 1, fila, 0, total - 1);
        total--;
    }

    private static void executarPorPid(Scanner sc) {
        System.out.print("Informe o PID do processo a ser executado: ");
        int pidExecutar = sc.nextInt();
        boolean encontrado = false;

        for (int i = 0; i < total; i++) {
            if (fila[i].getPid() == pidExecutar) {
                Processo proc = fila[i];
                proc.executar();

                if (proc instanceof ReadingProcess rp) {
                    atualizarContextoExecucao(rp.getContexto());
                } else {
                    atualizarPidCounter();
                }

                // Agora sim: remover o processo executado da fila
                for (int j = i; j < total - 1; j++) {
                    fila[j] = fila[j + 1];
                }
                fila[--total] = null;

                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            System.out.println("PID não encontrado.");
        }
    }

    public static void atualizarContextoExecucao(ContextoExecucao contexto) {
        total = contexto.total;
        pidCounter = contexto.pidCounter;
    }

    /*
     * Não deve ser chamado após a execução de um ReadingProcess.
     * Atualiza o contador de PIDs com o proximo valor disponível para evitar duplicação de PIDs
     */
    private static void atualizarPidCounter() {
        int maior = 0;
        for (int i = 0; i < total; i++) {
            if (fila[i] != null && fila[i].getPid() > maior) {
                maior = fila[i].getPid();
            }
        }
        pidCounter = maior + 1;
    }

    private static void salvarFila() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_FILA))) {
            oos.writeInt(total);
            for (int i = 0; i < total; i++) {
                oos.writeObject(fila[i]);
            }
            oos.flush();
            oos.close();
            System.out.println("Fila salva.");
        } catch (IOException e) {
            System.out.println("Erro ao salvar: " + e.getMessage());
        }
    }

    private static void carregarFila() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARQUIVO_FILA))) {
            int qtd = ois.readInt();
            List<Processo> listaTemp = new ArrayList<>();

            for (int i = 0; i < qtd; i++) {
                Processo p = (Processo) ois.readObject();
                listaTemp.add(p);
            }

            listaTemp.sort(Comparator.comparingInt(Processo::getPid)); // Ordenar pelo PID

            // Copiar para o array
            total = listaTemp.size();
            for (int i = 0; i < total; i++) {
                fila[i] = listaTemp.get(i);
            }

            atualizarPidCounter();

            System.out.println("Fila carregada e ordenada por PID.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar: " + e.getMessage());
        }
    }

    private static void limparTerminal() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            System.out.println("\n\n\n");
        }
    }
}