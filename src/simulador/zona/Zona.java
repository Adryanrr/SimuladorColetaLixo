package simulador.zona;

import java.util.Random;

/**
 * Representa uma zona geográfica da cidade onde há geração e acúmulo de lixo.
 * <p>
 * Cada zona possui um nome, intervalo de geração de lixo diário e um valor acumulado.
 * A zona pode gerar lixo, ter lixo coletado e informar seu estado atual.
 */
public class Zona {

    private String nome;
    private int lixoMinimo;
    private int lixoMaximo;
    private int lixoAcomulado;

    /**
     * Construtor da zona.
     *
     * @param nome        nome da zona (ex: "Leste", "Centro")
     * @param lixoMinimo  quantidade mínima de lixo gerada por dia
     * @param lixoMaximo  quantidade máxima de lixo gerada por dia
     */
    public Zona(String nome, int lixoMinimo, int lixoMaximo) {
        this.nome = nome;
        this.lixoMinimo = lixoMinimo;
        this.lixoMaximo = lixoMaximo;
        this.lixoAcomulado = 0;
    }

    /**
     * Gera uma nova quantidade de lixo aleatória para o dia.
     * <p>
     * O valor é gerado entre o mínimo e o máximo definidos na zona.
     * Substitui o valor atual de lixo acumulado.
     */
    public void gerarLixoDiario() {
        this.lixoAcomulado = new Random().nextInt(lixoMaximo - lixoMinimo + 1) + lixoMinimo;
        System.out.println("[Zona] " + nome + " gerou " + lixoAcomulado + " toneladas de lixo.");
    }

    /**
     * Coleta uma quantidade de lixo da zona, reduzindo o lixo acumulado.
     * <p>
     * Se a quantidade solicitada for maior do que o disponível, coleta apenas o máximo possível.
     *
     * @param quantidade a quantidade desejada para coleta
     * @return a quantidade efetivamente coletada
     */
    public int coletarLixo(int quantidade) {
        int coletado = Math.min(quantidade, lixoAcomulado);
        lixoAcomulado -= coletado;
        return coletado;
    }

    /**
     * Verifica se ainda há lixo acumulado na zona.
     *
     * @return {@code true} se houver lixo restante, {@code false} se estiver vazia
     */
    public boolean temLixoRestante() {
        return lixoAcomulado > 0;
    }

    /**
     * Retorna a quantidade atual de lixo acumulado.
     *
     * @return valor acumulado em toneladas
     */
    public int getLixoAcumulado() {
        return lixoAcomulado;
    }

    /**
     * Verifica se a zona está completamente limpa (sem lixo acumulado).
     *
     * @return {@code true} se o lixo acumulado for 0, caso contrário {@code false}
     */
    public boolean estaLimpa() {
        return lixoAcomulado == 0;
    }

    /**
     * Verifica se a zona precisa de coleta, com base em um limite mínimo definido.
     *
     * @param limiteMinimo o valor mínimo que define a necessidade de coleta
     * @return {@code true} se o lixo acumulado for igual ou maior ao limite
     */
    public boolean precisaDeColeta(int limiteMinimo) {
        return lixoAcomulado >= limiteMinimo;
    }

    /**
     * Retorna o nome da zona.
     *
     * @return nome da zona
     */
    public String getNome() {
        return nome;
    }
}
