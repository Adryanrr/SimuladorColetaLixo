package simulador.util;

import simulador.configuracao.ParametrosSimulacao;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Classe utilitária para conversão e cálculo de tempos na simulação.
 * <p>
 * Fornece métodos para manipulação, formatação e estimativa de tempos com base nas regras
 * de operação da coleta, horários de pico e deslocamento urbano.
 */
public class TempoUtil {

    /**
     * Converte os minutos decorridos desde o início da simulação (às 07:00)
     * para uma representação de horário real no formato "HH:mm".
     *
     * @param minutosDecorridos tempo desde as 07:00 (minutos)
     * @return String com o horário simulado formatado
     * @throws IllegalArgumentException se minutosDecorridos for negativo
     */
    public static String formatarHorarioSimulado(int minutosDecorridos) {
        if (minutosDecorridos < 0) {
            throw new IllegalArgumentException("Minutos não podem ser negativos");
        }
        int hora = 7 + (minutosDecorridos / 60);
        int minuto = minutosDecorridos % 60;
        return String.format("%02d:%02d", hora, minuto);
    }

    /**
     * Converte uma duração (em minutos) para uma representação textual amigável.
     * <p>
     * Exemplo: 135 → "2h 15min"
     *
     * @param duracaoMinutos duração total em minutos
     * @return String formatada como "Xh Ym" ou "Zmin"
     */
    public static String formatarDuracao(int duracaoMinutos) {
        int horas = duracaoMinutos / 60;
        int minutos = duracaoMinutos % 60;
        if (horas > 0) {
            return String.format("%dh %02dmin", horas, minutos);
        } else {
            return String.format("%dmin", minutos);
        }
    }

    /**
     * Calcula o tempo real de viagem considerando os horários de pico.
     * <p>
     * Para cada minuto da viagem base, aplica o multiplicador de tempo correspondente
     * ao horário atual da simulação (pico ou fora de pico).
     *
     * @param tempoAtual  tempo atual da simulação (em minutos)
     * @param duracaoBase duração da viagem em minutos (sem considerar pico)
     * @return duração total ajustada com multiplicadores de tráfego
     * @throws IllegalArgumentException se qualquer parâmetro for negativo
     */
    public static int calcularTempoRealDeViagem(int tempoAtual, int duracaoBase) {
        if (tempoAtual < 0 || duracaoBase < 0) {
            throw new IllegalArgumentException("Parâmetros de tempo não podem ser negativos");
        }

        int tempoRestante = duracaoBase;
        int tempoSimulado = tempoAtual;
        int tempoFinal = 0;

        while (tempoRestante > 0) {
            int hora = 7 + (tempoSimulado / 60);

            double multiplicador = ParametrosSimulacao.isHorarioDePico(hora)
                    ? ParametrosSimulacao.MULTIPLICADOR_TEMPO_PICO
                    : ParametrosSimulacao.MULTIPLICADOR_TEMPO_FORA_PICO;

            tempoFinal += (int) Math.round(multiplicador);
            tempoSimulado++;
            tempoRestante--;
        }

        return tempoFinal;
    }

    /**
     * Calcula o tempo total de uma operação de coleta ou transferência.
     * <p>
     * Considera tempo de coleta (se não estiver carregado), deslocamento com base
     * em horário de pico, e um tempo extra se o caminhão estiver carregado.
     *
     * @param tempoAtual     tempo atual da simulação (minutos desde as 07:00)
     * @param cargaToneladas quantidade transportada ou coletada (em toneladas)
     * @param carregado      {@code true} se o caminhão estiver indo descarregar na estação
     * @return instância de {@link TempoDetalhado} com os tempos de coleta, deslocamento e total
     */
    public static TempoDetalhado calcularTempoDetalhado(int tempoAtual, int cargaToneladas, boolean carregado) {
        int hora = 7 + (tempoAtual / 60);
        boolean pico = ParametrosSimulacao.isHorarioDePico(hora);

        int min = pico ? ParametrosSimulacao.TEMPO_MIN_PICO : ParametrosSimulacao.TEMPO_MIN_FORA_PICO;
        int max = pico ? ParametrosSimulacao.TEMPO_MAX_PICO : ParametrosSimulacao.TEMPO_MAX_FORA_PICO;

        // Tempo de deslocamento básico aleatório entre mínimo e máximo
        int tempoBase = ThreadLocalRandom.current().nextInt(min, max + 1);

        int tempoDeslocamento = calcularTempoRealDeViagem(tempoAtual, tempoBase);

        // Tempo de coleta só é considerado se o caminhão estiver coletando
        int tempoColeta = carregado ? 0 : cargaToneladas * ParametrosSimulacao.TEMPO_COLETA_POR_TONELADA;

        // Tempo extra simula lentidão por peso ao estar carregado
        int tempoExtraCarregado = carregado ? (int) (tempoDeslocamento * 0.3) : 0;
        int tempoTotal = tempoColeta + tempoDeslocamento + tempoExtraCarregado;

        return new TempoDetalhado(tempoColeta, tempoDeslocamento, tempoExtraCarregado);
    }
}
