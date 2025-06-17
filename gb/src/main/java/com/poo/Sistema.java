package com.poo;

import com.poo.model.*;

import java.io.*;
import java.util.*;

import static com.poo.util.Constantes.ARQUIVO_FILA;
import static com.poo.util.Constantes.MAX_PROCESSOS;

public class Sistema {
    public static Processo[] fila = new Processo[MAX_PROCESSOS];
    public static int total = 0;
    public static int pidCounter = 1;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("""
                Sistema de Gerenciamento de Processos
                    1. Criar processo
                    2. Executar próximo
                    3. Executar processo por PID
                    4. Salvar fila em arquivo
                    5. Carregar fila do arquivo
                    6. Mostrar fila
                    0. Sair 
                """);
            int op = sc.nextInt();
            sc.nextLine();

            switch (op) {
                case 1 -> criarProcesso(sc);
                case 2 -> executarProximo();
                case 3 -> executarPorPid(sc);
                case 4 -> salvarFila();
                case 5 -> carregarFila();
                case 6 -> mostrarFila();
                case 0 -> System.exit(0);
                default -> System.out.println("Opção inválida");
            }
        }
    }

    public static void criarProcesso(Scanner sc) {
        if (total >= MAX_PROCESSOS) {
            System.out.println("Fila cheia.");
            return;
        }
        System.out.println("Tipo do processo (1-Calculo, 2-Gravacao, 3-Leitura, 4-Impressao): ");
        int tipo = sc.nextInt();
        sc.nextLine();

        ContextoExecucao contexto = new ContextoExecucao(fila, total, pidCounter);
        Processo processo;

        switch (tipo) {
            case 1 -> {
                System.out.print("Informe a expressão (ex: 2 + 2): ");
                String exp = sc.nextLine();
                processo = new ComputingProcess(pidCounter++, exp);
            }
            case 2 -> {
                System.out.print("Informe expressão para gravar (ex: 2 + 2): ");
                String exp = sc.nextLine();
                processo = new WritingProcess(pidCounter++, exp);
            }
            case 3 -> processo = new ReadingProcess(pidCounter++, contexto);
            case 4 -> processo = new PrintingProcess(pidCounter++, contexto);
            default -> {
                System.out.println("Tipo inválido.");
                return;
            }
        }

        fila[total++] = processo;
        System.out.println("Processo (" + processo.getClass().getSimpleName() + ") criado com PID: " + processo.getPid());
    }

    public static void executarProximo() {
        if (total == 0) {
            System.out.println("Fila vazia.");
            return;
        }
        Processo processo = fila[0];
        if (processo != null) processo.executar();

        if (processo instanceof ReadingProcess rp) {
            atualizarContextoExecucao(rp.getContexto());
        } else {
            atualizarPidCounter();
        }

        System.arraycopy(fila, 1, fila, 0, total - 1); // System.arraycopy(Object src, int srcPos, Object dest, int destPos, int length);
        fila[--total] = null;
    }

    public static void executarPorPid(Scanner sc) {
        System.out.print("Informe o PID do processo a ser executado: ");
        int pid = sc.nextInt();
        boolean encontrado = false;

        for (int i = 0; i < total; i++) {
            if (fila[i] != null && fila[i].getPid() == pid) {
                Processo proc = fila[i];
                proc.executar();

                if (proc instanceof ReadingProcess rp) {
                    atualizarContextoExecucao(rp.getContexto());
                } else {
                    atualizarPidCounter();
                }

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

    public static void salvarFila() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARQUIVO_FILA))) {
            out.writeInt(total);
            for (int i = 0; i < total; i++) {
                out.writeObject(fila[i]);
            }
            System.out.println("Fila salva com sucesso.");
        } catch (IOException e) {
            System.out.println("Erro ao salvar a fila: " + e.getMessage());
        }
    }

    public static void carregarFila() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(ARQUIVO_FILA))) {
            int qtd = in.readInt();
            List<Processo> listaTemp = new ArrayList<>();
            for (int i = 0; i < qtd; i++) {
                listaTemp.add((Processo) in.readObject());
            }

            listaTemp.sort(Comparator.comparingInt(Processo::getPid)); // Ordena a lista por PID

            total = listaTemp.size();
            for (int i = 0; i < total; i++) {
                fila[i] = listaTemp.get(i);
            }
            atualizarPidCounter();
            System.out.println("Fila carregada e ordenada por PID.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar fila: " + e.getMessage());
        }
    }

    public static void mostrarFila() {
        if (total == 0) {
            System.out.println("Fila vazia.");
            return;
        }
        for (int i = 0; i < total; i++) {
            Processo p = fila[i];
            if (p != null) System.out.println("PID: " + p.getPid() + " - Tipo: " + p.getClass().getSimpleName());
        }
    }

    /*
     * Faz uma varredura na fila de processos e atualiza o pidCounter pra o maior PID + 1.
     */
    public static void atualizarPidCounter() {
        int maior = 0;
        for (int i = 0; i < total; i++) {
            if (fila[i] != null && fila[i].getPid() > maior) {
                maior = fila[i].getPid();
            }
        }
        pidCounter = maior + 1;
    }

    public static void atualizarContextoExecucao(ContextoExecucao contexto) {
        System.arraycopy(contexto.getFila(), 1, fila, 0, contexto.getTotal());
        pidCounter = contexto.getPidCounter();
        total = contexto.getTotal();
    }
}