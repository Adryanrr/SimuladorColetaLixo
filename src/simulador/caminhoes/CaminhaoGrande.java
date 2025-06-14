package simulador.caminhoes;

/**
 * Representa um caminhão grande utilizado para transportar o lixo das estações de transferência até o aterro sanitário.
 * <p>
 * Cada caminhão grande possui um identificador único, capacidade máxima de carga e armazena o estado da sua carga atual.
 */
public class CaminhaoGrande {
    private static int contadorIds = 1;  // para gerar id automático
    private int id;
    private final int capacidadeMaxima = 20;
    private int cargaAtual;
    private boolean carregando;
    private int tempoMaximoEspera;

    /**
     * Construtor padrão que inicializa o caminhão com ID automático e carga zerada.
     */
    public CaminhaoGrande() {
        this.id = contadorIds++;
        this.cargaAtual = 0;
        this.carregando = true;
    }

    /**
     * Retorna o identificador único do caminhão.
     *
     * @return o ID do caminhão
     */
    public int getId() {
        return id;
    }

    /**
     * Verifica se o caminhão atingiu sua capacidade máxima de carga.
     *
     * @return {@code true} se o caminhão estiver cheio, {@code false} caso contrário
     */
    public boolean estaCheio() {
        return cargaAtual >= capacidadeMaxima;
    }

    /**
     * Recebe uma quantidade de carga. Se ultrapassar a capacidade, a carga é limitada ao máximo.
     *
     * @param quantidade a quantidade de carga a ser adicionada
     */
    public void receberCarga(int quantidade) {
        if (cargaAtual + quantidade > capacidadeMaxima) {
            cargaAtual = capacidadeMaxima;
        } else {
            cargaAtual += quantidade;
        }
    }

    /**
     * Executa o descarregamento do caminhão no aterro.
     * <p>
     * Zera a carga e marca que o caminhão finalizou o processo de carregamento.
     */
    public void descarregar() {
        System.out.println("Caminhão grande #" + id + " partiu para o aterro com " + cargaAtual + " unidades de carga.");
        cargaAtual = 0;
        carregando = false;
    }

}
