package estruturas.filas;

import estruturas.lista.No;

/**
     * Implementação de uma fila encadeada
     */
    public class FilaPrioridade<T> {
        private No<T> inicio;
        private No<T> fim;
        private int tamanho;

        public FilaPrioridade() {
            this.inicio = null;
            this.fim = null;
            this.tamanho = 0;
        }

        public void enfileirar(T valor) {
            No<T> novoNo = new No<>(valor);

            if (estaVazia()) {
                inicio = novoNo;
                fim = novoNo;
            } else {
                fim.setProx(novoNo);
                novoNo.setPrev(fim);
                fim = novoNo;
            }

            tamanho++;
        }

        public T desenfileirar() {
            if (estaVazia()) {
                return null;
            }

            No<T> noRemovido = inicio;
            inicio = inicio.getProx();

            if (inicio != null) {
                inicio.setPrev(null);
            } else {
                fim = null;
            }

            tamanho--;
            return noRemovido.getValor();
        }

        public T primeiro() {
            if (estaVazia()) {
                return null;
            }

            return inicio.getValor();
        }

        public boolean estaVazia() {
            return tamanho == 0;
        }

        public int tamanho() {
            return tamanho;
        }
    }