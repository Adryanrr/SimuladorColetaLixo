package simulador.caminhoes;

import estruturas.lista.Lista;
import simulador.eventos.EventoGerarCaminhaoGrande;
import simulador.zona.Zona;

/**
 * Representa um caminhão pequeno responsável por coletar lixo nas zonas urbanas.
 * <p>
 * Cada caminhão possui uma capacidade máxima, um número limitado de viagens diárias
 * e uma rota composta por zonas a serem percorridas. A coleta ocorre em etapas, e o
 * caminhão avança em sua rota conforme as viagens são realizadas.
 */
public class CaminhaoPequeno {

    // ========== ATRIBUTOS ==========
    private String id;
    private int capacidadeMaxima;
    private int cargaAtual;
    private int numeroDeViagensDiarias;

    private Lista<Zona> rota;
    private int indiceRota = 0;

    private Zona zonaAlvo;
    private EventoGerarCaminhaoGrande eventoAgendado;

    // ========== CONSTRUTOR ==========

    /**
     * Construtor do caminhão pequeno com uma rota pré-definida.
     *
     * @param id identificador único do caminhão
     * @param capacidadeMaxima capacidade máxima de carga (em toneladas)
     * @param numeroDeViagensDiarias número total de viagens permitidas por dia
     * @param rota lista ordenada de zonas que o caminhão visitará em sequência
     */
    public CaminhaoPequeno(String id, int capacidadeMaxima, int numeroDeViagensDiarias, Lista<Zona> rota) {
        this.id = id;
        this.capacidadeMaxima = capacidadeMaxima;
        this.numeroDeViagensDiarias = numeroDeViagensDiarias;
        this.cargaAtual = 0;
        this.rota = rota;
        this.indiceRota = 0;
        this.zonaAlvo = rota.getValor(0); // define zona inicial como a primeira da rota
    }

    // ========== GETTERS BÁSICOS ==========

    /**
     * @return identificador único do caminhão
     */
    public String getId() {
        return id;
    }

    /**
     * @return capacidade máxima em toneladas
     */
    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    /**
     * @return carga atual do caminhão em toneladas
     */
    public int getCargaAtual() {
        return cargaAtual;
    }

    /**
     * @return número restante de viagens permitidas no dia
     */
    public int getNumeroDeViagensDiarias() {
        return numeroDeViagensDiarias;
    }

    /**
     * Retorna a zona atualmente atribuída ao caminhão (usada em eventos legados).
     *
     * @return zona atual de coleta
     */
    public Zona getZonaAlvo() {
        return zonaAlvo;
    }

    // ========== ROTA ==========

    /**
     * Verifica se ainda há zonas na rota a serem visitadas.
     *
     * @return true se há mais zonas na rota; false caso contrário
     */
    public boolean temMaisZonasNaRota() {
        return indiceRota < rota.getTamanho() - 1;
    }

    /**
     * @return rota completa do caminhão (lista de zonas)
     */
    public Lista<Zona> getRota() {
        return rota;
    }

    /**
     * Atualiza a próxima zona da rota para onde o caminhão deve ir.
     *
     * @return {@code true} se encontrou uma nova zona válida com lixo, {@code false} se não há mais zonas com lixo na rota.
     */
    public boolean atualizarProximaZonaAlvo() {
        int tentativas = rota.getTamanho(); // Evita loop infinito

        for (int i = 0; i < tentativas; i++) {
            indiceRota = (indiceRota + 1) % rota.getTamanho();
            Zona proximaZona = rota.getValor(indiceRota);

            if (!proximaZona.estaLimpa()) {
                zonaAlvo = proximaZona;
                System.out.println("[CAMINHÃO " + id + "] Redirecionado para zona " + zonaAlvo.getNome());
                return true;
            }
        }

        // Nenhuma zona disponível com lixo
        return false;
    }

    /**
     * Atualiza a zona alvo para a próxima da rota, se houver.
     */
    public void atualizarZonaAlvo() {
        if (rota == null || rota.getTamanho() == 0) return;

        indiceRota++;
        if (indiceRota >= rota.getTamanho()) {
            indiceRota = 0; // volta para o início da rota (ou poderia encerrar)
        }

        zonaAlvo = rota.getValor(indiceRota);
    }

    /**
     * Redefine a zona alvo do caminhão, ignorando a rota original.
     * Usado em redirecionamentos automáticos de prioridade.
     *
     * @param novaZona nova zona prioritária atribuída ao caminhão
     */
    public void definirZonaAlvo(Zona novaZona) {
        this.zonaAlvo = novaZona;
    }


    // ========== COLETA ==========

    /**
     * Tenta coletar uma determinada quantidade de lixo.
     *
     * @param quantidade quantidade a ser coletada
     * @return true se conseguiu coletar, false se excederia a capacidade
     */
    public boolean coletar(int quantidade) {
        if (cargaAtual + quantidade <= capacidadeMaxima) {
            cargaAtual += quantidade;
            System.out.println("[CAMINHÃO " + id + "] Coletou " + quantidade + " toneladas");
            return true;
        }
        System.out.println("[CAMINHÃO " + id + "] Carga máxima atingida.");
        return false;
    }

    /**
     * Descarta toda a carga do caminhão.
     */
    public void descarregar() {
        this.cargaAtual = 0;
    }

    /**
     * Verifica se o caminhão ainda pode realizar viagens hoje.
     *
     * @return true se há viagens restantes, false caso contrário
     */
    public boolean podeRealizarNovaViagem() {
        return numeroDeViagensDiarias > 0;
    }

    /**
     * Registra uma viagem realizada, decrementando o contador de viagens disponíveis.
     */
    public void registrarViagem() {
        if (numeroDeViagensDiarias > 0) {
            numeroDeViagensDiarias--;
            System.out.println("[CAMINHÃO " + id + "] " + numeroDeViagensDiarias + " VIAGENS RESTANTES");
        }
    }

    // ========== EVENTO DE GERAÇÃO DE CAMINHÃO GRANDE ==========

    /**
     * @return evento vinculado de geração de caminhão grande (se houver)
     */
    public EventoGerarCaminhaoGrande getEventoAgendado() {
        return eventoAgendado;
    }

    /**
     * Define ou atualiza o evento de geração de caminhão grande.
     *
     * @param eventoAgendado o evento a ser vinculado
     */
    public void setEventoAgendado(EventoGerarCaminhaoGrande eventoAgendado) {
        this.eventoAgendado = eventoAgendado;
    }
}
