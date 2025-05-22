package simulador.ui;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

import estruturas.filas.FilaPrioridade;

/**
 * Controlador principal da interface gráfica do simulador.
 * <p>
 * Esta classe foi aprimorada para incluir novas animações, melhor gerenciamento
 * de estados visuais e integração com componentes de status e estatísticas.
 * Também inclui sincronização de animações com o tempo da simulação.
 */
public class SimuladorFXController {

    private static SimuladorFXController instancia;

    private final Pane mapa;
    private final TextArea logArea;
    private final Slider velocidadeSlider;

    private final Map<String, CaminhaoView> caminhoes = new HashMap<>();
    private final Map<String, ZonaView> zonasView = new HashMap<>();
    private final Map<String, Point2D> posicoesEstacoes = new HashMap<>();

    private final FilaPrioridade<Runnable> filaAnimacoes = new FilaPrioridade<>();
    private boolean animacaoEmAndamento = false;

    // Referência ao painel de status dos caminhões
    private PainelStatusCaminhoes painelStatusCaminhoes;

    // Mapa para armazenar o lixo acumulado por zona
    private final Map<String, Integer> lixoAcumuladoPorZona = new HashMap<>();

    /**
     * Cria um novo controlador da interface gráfica.
     *
     * @param mapa             painel principal do mapa
     * @param logArea          área de texto para logs
     * @param velocidadeSlider controle deslizante de velocidade
     */
    public SimuladorFXController(Pane mapa, TextArea logArea, Slider velocidadeSlider) {
        this.mapa = mapa;
        this.logArea = logArea;
        this.velocidadeSlider = velocidadeSlider;

        instancia = this;
        inicializarZonas();

        // Inicializar painel de status dos caminhões
        this.painelStatusCaminhoes = new PainelStatusCaminhoes(mapa);

        // Adicionar listener para atualizar velocidade de animação quando o slider mudar
        velocidadeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            atualizarVelocidadeAnimacaoCaminhoes();
        });
    }

    /**
     * Retorna a instância única do controlador.
     *
     * @return instância do controlador
     */
    public static SimuladorFXController getInstancia() {
        return instancia;
    }

    /**
     * Verifica se o controlador já foi instanciado.
     *
     * @return true se já existe uma instância, false caso contrário
     */
    public static boolean isInstanciado() {
        return instancia != null;
    }

    /**
     * Inicializa as zonas no mapa.
     */
    private void inicializarZonas() {
        zonasView.put("Norte", new ZonaView("Norte", "#A5D6A7", 100, 50, mapa));
        zonasView.put("Sul", new ZonaView("Sul", "#90CAF9", 100, 200, mapa));
        zonasView.put("Centro", new ZonaView("Centro", "#FFAB91", 300, 70, mapa));
        zonasView.put("Sudeste", new ZonaView("Sudeste", "#FFF59D", 300, 230, mapa));
        zonasView.put("Leste", new ZonaView("Leste", "#CE93D8", 500, 150, mapa));

        posicoesEstacoes.put("Estação A", new Point2D(800, 150));
        posicoesEstacoes.put("Estação B", new Point2D(800, 250));

        addEstacaoVisual("Estação A", 800, 150);
        addEstacaoVisual("Estação B", 800, 250);

        // Inicializar lixo acumulado para cada zona
        for (String zona : zonasView.keySet()) {
            lixoAcumuladoPorZona.put(zona, 0);
        }
    }

    /**
     * Adiciona uma estação de transferência ao mapa.
     *
     * @param nome nome da estação
     * @param x    posição X
     * @param y    posição Y
     */
    private void addEstacaoVisual(String nome, double x, double y) {
        Rectangle ret = new Rectangle(100, 60, Color.LIGHTBLUE);
        ret.setLayoutX(x);
        ret.setLayoutY(y);
        ret.setId("estacao_" + nome.replace("Estação ", ""));

        Label lbl = new Label(nome);
        lbl.setLayoutX(x + 10);
        lbl.setLayoutY(y + 20);
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        // Adicionar indicador de lixo recebido
        ProgressBar barraLixo = new ProgressBar(0);
        barraLixo.setPrefWidth(80);
        barraLixo.setLayoutX(x + 10);
        barraLixo.setLayoutY(y + 45);
        barraLixo.setStyle("-fx-accent: #8B4513;");
        barraLixo.setId("barra_estacao_" + nome.replace("Estação ", ""));

        Label valorLixo = new Label("0T");
        valorLixo.setLayoutX(x + 45);
        valorLixo.setLayoutY(y + 5);
        valorLixo.setId("lixo_estacao_" + nome.replace("Estação ", ""));

        mapa.getChildren().addAll(ret, lbl, barraLixo, valorLixo);
    }

    /**
     * Adiciona uma animação à fila de execução.
     *
     * @param animacao ação a ser executada
     */
    private void enfileirarAnimacao(Runnable animacao) {
        filaAnimacoes.enfileirar(animacao);
        if (!animacaoEmAndamento) {
            executarProximaAnimacao();
        }
    }

    /**
     * Executa a próxima animação da fila.
     */
    private void executarProximaAnimacao() {
        Runnable proxima = filaAnimacoes.desenfileirar();
        if (proxima != null) {
            animacaoEmAndamento = true;
            Platform.runLater(proxima);
        } else {
            animacaoEmAndamento = false;
        }
    }

    /**
     * Calcula a duração visual de uma animação com base no tempo simulado.
     *
     * @param minutosSimulados tempo em minutos na simulação
     * @return duração da animação
     */
    private Duration calcularDuracaoVisual(int minutosSimulados) {
        int velocidade = (int) velocidadeSlider.getValue();
        double fator = switch (velocidade) {
            case 1 -> 1.0;
            case 2 -> 0.75;
            case 3 -> 0.5;
            case 4 -> 0.25;
            case 5 -> 0.1;
            default -> 1.0;
        };
        double segundos = minutosSimulados * fator / 10.0;
        return Duration.seconds(Math.max(0.5, segundos));
    }

    /**
     * Atualiza o fator de velocidade de animação para todos os caminhões.
     */
    private void atualizarVelocidadeAnimacaoCaminhoes() {
        int velocidade = (int) velocidadeSlider.getValue();
        double fator = switch (velocidade) {
            case 1 -> 1.0;
            case 2 -> 1.25;  // Mais rápido quando o slider aumenta
            case 3 -> 1.5;
            case 4 -> 2.0;
            case 5 -> 3.0;
            default -> 1.0;
        };

        // Atualizar velocidade de animação para todos os caminhões
        for (CaminhaoView caminhao : caminhoes.values()) {
            caminhao.setVelocidadeAnimacao(fator);
        }
    }

    /**
     * Anima a coleta de lixo por um caminhão em uma zona.
     *
     * @param caminhaoId    identificador do caminhão
     * @param zonaNome      nome da zona
     * @param capacidade    capacidade do caminhão
     * @param tempoTotalMin tempo total em minutos
     */
    public void animarColeta(String caminhaoId, String zonaNome, int capacidade, int tempoTotalMin) {
        enfileirarAnimacao(() -> {
            ZonaView zona = zonasView.get(zonaNome.replace("Zona ", ""));
            if (zona == null) {
                logArea.appendText("⚠ Zona desconhecida: " + zonaNome + "\n");
                executarProximaAnimacao();
                return;
            }

            CaminhaoView caminhao = obterOuCriarCaminhao(caminhaoId);
            Point2D destino = new Point2D(zona.getCenterX() - 20, zona.getCenterY() - 12);
            Duration duracao = calcularDuracaoVisual(tempoTotalMin);

            // Atualizar painel de status
            painelStatusCaminhoes.adicionarOuAtualizarCaminhao(
                caminhaoId,
                "coletando em " + zonaNome,
                caminhao.getCargaAtual(),
                caminhao.getCapacidadeMaxima(),
                "#ORANGE" // Cor para coleta
            );

            caminhao.moverPara(destino, duracao);
            caminhao.simularColeta(duracao);

            // Atualizar visualização de lixo na zona (simulado)
            String zonaKey = zonaNome.replace("Zona ", "");
            int lixoAtual = lixoAcumuladoPorZona.getOrDefault(zonaKey, 0);
            int novoLixo = Math.max(0, lixoAtual - 5); // Simulação de coleta
            lixoAcumuladoPorZona.put(zonaKey, novoLixo);
            zona.atualizarLixo(novoLixo, 100);

            new Thread(() -> {
                try {
                    Thread.sleep((long) duracao.toMillis());
                } catch (InterruptedException ignored) {
                }
                executarProximaAnimacao();
            }).start();
        });
    }

    /**
     * Anima a transferência de lixo de um caminhão para uma estação.
     *
     * @param caminhaoId    identificador do caminhão
     * @param estacaoNome   nome da estação
     * @param cargaAtual    carga atual do caminhão
     * @param capacidade    capacidade do caminhão
     * @param tempoTotalMin tempo total em minutos
     */
    public void animarTransferencia(String caminhaoId, String estacaoNome, int cargaAtual, int capacidade, int tempoTotalMin) {
        enfileirarAnimacao(() -> {
            CaminhaoView caminhao = obterOuCriarCaminhao(caminhaoId);
            Point2D destino = posicoesEstacoes.get(estacaoNome);

            if (destino == null) {
                logArea.appendText("⚠ Estação desconhecida: " + estacaoNome + "\n");
                executarProximaAnimacao();
                return;
            }

            Duration duracao = calcularDuracaoVisual(tempoTotalMin);

            // Atualizar painel de status
            painelStatusCaminhoes.adicionarOuAtualizarCaminhao(
                caminhaoId,
                "indo para " + estacaoNome,
                cargaAtual,
                capacidade,
                "#DEEPSKYBLUE" // Cor para transferência
            );

            caminhao.mudarCor("transferencia");
            caminhao.setStatus("indo para " + estacaoNome + "...");
            caminhao.atualizarCarga(cargaAtual, capacidade);
            caminhao.moverPara(destino, duracao);

            new Thread(() -> {
                try {
                    Thread.sleep((long) duracao.toMillis());
                } catch (InterruptedException ignored) {
                }
                // Após chegar na estação, animar a descarga
                Platform.runLater(() -> animarDescarga(caminhaoId, estacaoNome, cargaAtual, capacidade, 30));
            }).start();
        });
    }

    /**
     * Anima a descarga de lixo de um caminhão em uma estação.
     *
     * @param caminhaoId    identificador do caminhão
     * @param estacaoNome   nome da estação
     * @param cargaAnterior carga anterior à descarga
     * @param capacidade    capacidade do caminhão
     * @param tempoTotalMin tempo total em minutos
     */
    public void animarDescarga(String caminhaoId, String estacaoNome, int cargaAnterior, int capacidade, int tempoTotalMin) {
        enfileirarAnimacao(() -> {
            CaminhaoView caminhao = obterOuCriarCaminhao(caminhaoId);

            // Atualizar painel de status
            painelStatusCaminhoes.adicionarOuAtualizarCaminhao(
                caminhaoId,
                "descarregando em " + estacaoNome,
                0,
                capacidade,
                "#GREY" // Cor para descarga
            );

            // Efeito visual de descarga
            Rectangle efeitoDescarga = new Rectangle(30, 5, Color.BROWN);
            Point2D posicaoEstacao = posicoesEstacoes.get(estacaoNome);
            efeitoDescarga.setLayoutX(posicaoEstacao.getX() + 35);
            efeitoDescarga.setLayoutY(posicaoEstacao.getY() + 30);
            mapa.getChildren().add(efeitoDescarga);

            // Animação de esvaziamento
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), efeitoDescarga);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setCycleCount(3);

            // Atualização visual do caminhão
            caminhao.mudarCor("descarregando");
            caminhao.setStatus("descarregando...");
            caminhao.atualizarCarga(0, capacidade);
            caminhao.simularDescarga(Duration.seconds(2));

            // Execução da animação
            fadeOut.play();

            Duration duracao = calcularDuracaoVisual(tempoTotalMin);

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> mapa.getChildren().remove(efeitoDescarga));

                    // Após a descarga, agendar retorno para uma zona aleatória
                    String[] zonas = {"Norte", "Sul", "Centro", "Sudeste", "Leste"};
                    String zonaAleatoria = zonas[(int)(Math.random() * zonas.length)];

                    Platform.runLater(() -> animarRetorno(caminhaoId, "Zona " + zonaAleatoria, 45));
                } catch (InterruptedException ignored) {
                }
            }).start();
        });
    }

    /**
     * Anima o retorno de um caminhão para uma zona após descarga.
     *
     * @param caminhaoId    identificador do caminhão
     * @param zonaNome      nome da zona
     * @param tempoTotalMin tempo total em minutos
     */
    public void animarRetorno(String caminhaoId, String zonaNome, int tempoTotalMin) {
        enfileirarAnimacao(() -> {
            CaminhaoView caminhao = obterOuCriarCaminhao(caminhaoId);
            ZonaView zona = zonasView.get(zonaNome.replace("Zona ", ""));

            if (zona == null) {
                logArea.appendText("⚠ Zona desconhecida: " + zonaNome + "\n");
                executarProximaAnimacao();
                return;
            }

            Point2D destino = new Point2D(zona.getCenterX() - 20, zona.getCenterY() - 12);
            Duration duracao = calcularDuracaoVisual(tempoTotalMin);

            // Atualizar painel de status
            painelStatusCaminhoes.adicionarOuAtualizarCaminhao(
                caminhaoId,
                "retornando para " + zonaNome,
                0,
                caminhao.getCapacidadeMaxima(),
                "#DARKGREEN" // Cor para idle
            );

            caminhao.mudarCor("idle");
            caminhao.setStatus("retornando para " + zonaNome + "...");
            caminhao.atualizarCarga(0, caminhao.getCapacidadeMaxima());
            caminhao.moverPara(destino, duracao);

            new Thread(() -> {
                try {
                    Thread.sleep((long) duracao.toMillis());
                } catch (InterruptedException ignored) {
                }
                executarProximaAnimacao();
            }).start();
        });
    }

    /**
     * Atualiza a carga de um caminhão.
     *
     * @param caminhaoId identificador do caminhão
     * @param atual      carga atual
     * @param maximo     capacidade máxima
     */
    public void atualizarCargaCaminhao(String caminhaoId, int atual, int maximo) {
        CaminhaoView view = caminhoes.get(caminhaoId);
        if (view != null) {
            Platform.runLater(() -> {
                view.atualizarCarga(atual, maximo);

                // Atualizar também no painel de status
                String status = view.getStatus();
                String cor = atual > 0 ? "#ORANGE" : "#DARKGREEN"; // Laranja se com carga, verde se vazio

                painelStatusCaminhoes.adicionarOuAtualizarCaminhao(
                    caminhaoId,
                    status,
                    atual,
                    maximo,
                    cor
                );
            });
        }
    }

    /**
     * Atualiza o lixo acumulado em uma zona.
     *
     * @param zonaNome  nome da zona
     * @param quantidade quantidade de lixo
     * @param capacidade capacidade máxima de referência
     */
    public void atualizarLixoZona(String zonaNome, int quantidade, int capacidade) {
        ZonaView zona = zonasView.get(zonaNome.replace("Zona ", ""));
        if (zona != null) {
            String zonaKey = zonaNome.replace("Zona ", "");
            lixoAcumuladoPorZona.put(zonaKey, quantidade);
            zona.atualizarLixo(quantidade, capacidade);
        }
    }

    /**
     * Obtém ou cria um caminhão com o ID especificado.
     *
     * @param id identificador do caminhão
     * @return view do caminhão
     */
    private CaminhaoView obterOuCriarCaminhao(String id) {
        CaminhaoView existente = caminhoes.get(id);
        if (existente != null) return existente;

        CaminhaoView novo = new CaminhaoView(id);

        // Configurar velocidade de animação inicial
        atualizarVelocidadeAnimacaoCaminhoes();

        double offset = caminhoes.size() * 20;
        novo.getForma().setLayoutX(50 + offset);
        novo.getForma().setLayoutY(50);

        // Não precisamos mais posicionar o rótulo separadamente, pois ele faz parte do grupo visual

        caminhoes.put(id, novo);
        mapa.getChildren().add(novo.getForma());

        // Adicionar ao painel de status
        painelStatusCaminhoes.adicionarOuAtualizarCaminhao(
            id,
            "aguardando",
            0,
            novo.getCapacidadeMaxima(),
            "#DARKGREEN" // Cor para idle
        );

        return novo;
    }

    /**
     * Retorna o mapa de caminhões.
     *
     * @return mapa de caminhões
     */
    public Map<String, CaminhaoView> getCaminhoes() {
        return caminhoes;
    }

    /**
     * Retorna o mapa de zonas.
     *
     * @return mapa de zonas
     */
    public Map<String, ZonaView> getZonasView() {
        return zonasView;
    }

    /**
     * Retorna o mapa de posições das estações.
     *
     * @return mapa de posições das estações
     */
    public Map<String, Point2D> getPosicoesEstacoes() {
        return posicoesEstacoes;
    }
}
