package simulador.eventos;

import estruturas.lista.Lista;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa a agenda global de eventos da simulação.
 * Utiliza uma lista ordenada para armazenar e processar os eventos futuros em ordem cronológica.
 * Suporta observadores para reagir à execução de eventos.
 */
public class AgendaEventos {
    private static Lista<Evento> eventos = new Lista<>();
    private static int tempoUltimoEvento = 0;
    private static Evento ultimoEventoExecutado = null;

    // ✅ Lista de observadores do tipo EventoObserver
    private static final List<EventoObserver> observadores = new ArrayList<>();

    /**
     * Adiciona um novo evento à agenda, mantendo a ordem de execução com base no tempo.
     */
    public static void adicionarEvento(Evento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("Evento não pode ser nulo");
        }

        eventos.adicionarOrdenado(evento, (e1, e2) -> {
            if (e1.getTempo() < e2.getTempo()) return -1;
            if (e1.getTempo() > e2.getTempo()) return 1;
            return 0;
        });
    }

    /**
     * Remove um evento específico da agenda.
     */
    public static boolean removerEvento(Evento evento) {
        return eventos.removerProcurado(evento);
    }

    /**
     * Processa todos os eventos da agenda em ordem cronológica.
     */
    public static void processarEventos() {
        while (temEventos()) {
            Evento evento = eventos.removerHead();
            tempoUltimoEvento = evento.getTempo();
            ultimoEventoExecutado = evento;
            evento.executar();

            // ✅ Notifica todos os observadores registrados
            for (EventoObserver obs : observadores) {
                obs.onEventoExecutado(evento);
            }
        }
    }

    /**
     * Limpa a agenda e reinicia os dados.
     */
    public static void resetar() {
        eventos = new Lista<>();
        tempoUltimoEvento = 0;
        ultimoEventoExecutado = null;
    }

    public static int getTempoUltimoEvento() {
        return tempoUltimoEvento;
    }

    public static Evento getUltimoEventoExecutado() {
        return ultimoEventoExecutado;
    }

    public static boolean temEventos() {
        return !eventos.estaVazia();
    }

    // ✅ MÉTODO NOVO: registra um novo observador
    public static void adicionarObserver(EventoObserver observer) {
        if (observer != null && !observadores.contains(observer)) {
            observadores.add(observer);
        }
    }
}
