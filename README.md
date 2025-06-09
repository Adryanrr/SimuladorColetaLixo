# ♻️ Simulador de Coleta de Lixo Urbana

Bem-vindo ao **Simulador de Coleta de Lixo em Teresina**, um projeto acadêmico desenvolvido em Java com o objetivo de representar o fluxo de coleta, descarregamento e transferência de resíduos sólidos urbanos de forma realista e orientada a eventos.

---

## 🚀 Objetivo

Simular, de forma eficiente e flexível, o comportamento de caminhões de coleta de lixo em uma cidade com múltiplas zonas, respeitando regras como:

- Capacidade máxima de carga
- Tempo de viagem influenciado por horários de pico
- Número limitado de viagens por caminhão
- Tempo real de descarga e carregamento proporcional à carga
- Transferência de resíduos para estações e posterior envio ao aterro

---

## 🧠 Tecnologias utilizadas

- `Java 17` (padrão)
- Estruturas de dados próprias (Lista Duplamente Encadeada)
- Orientação a Objetos
- Arquitetura orientada a eventos (Event-Driven Simulation)

---

## ⚙️ Como funciona

1. A simulação inicia às **07:00 da manhã**.
2. Um ou mais caminhões pequenos são escalados para realizar coletas nas zonas da cidade.
3. Cada coleta consome tempo de viagem, que é impactado por **multiplicadores de horário de pico**.
4. Quando um caminhão atinge a capacidade máxima ou o limite diário de viagens:
   - Ele vai para a estação de transferência
   - Descarrega o lixo proporcional ao tempo por tonelada
5. Quando a estação acumula carga suficiente, um caminhão grande é enviado ao aterro sanitário.

---

## ⏱️ Modelagem de Tempo

- **Horário real da simulação:** convertido para `HH:mm`
- **Horários de pico:**
  - Manhã: 07:00 às 09:00
  - Tarde: 17:00 às 19:00
- **Multiplicador de tempo:**
  - Pico: `1.5x`
  - Fora de pico: `1.0x`
- **Tempos de operação:**
  - Viagens, cargas e descargas são proporcionalmente baseadas em toneladas

---

## 🛠️ Parâmetros configuráveis

Todos os parâmetros estão definidos na classe `ParametrosSimulacao.java`.

## 📦 Exemplos de saída

```txt
==================================================
TEMPO SIMULADO: 07:00
[Coleta] Caminhão 1
[Coleta] Coletou 2 toneladas. Carga atual: 2/4
[Viagens] Restam 2 viagens.
[Viagem] Tempo base: 30 min | Ajustado: 45 min
--------------------------------------------------
...
[Descarga] Descarregando 4 toneladas. Tempo estimado: 20 minutos.
```
## 📚 Desenvolvedores

<table>
  <tr>
    <td align="center"><a href="https://github.com/Adryanrr"><img src="https://github.com/Adryanrr.png" width="100px;" alt="Foto do Adryan Ryan"/><br /><sub><b>Adryan Ryan</b></sub></a></td>
    <td align="center"><a href="https://github.com/Adryanrr"><img src="https://github.com/FelipeDuan.png" width="100px;" alt="Foto do Felipe Duan"/><br /><sub><b>Felipe Duan</b></sub></a></td>
  </tr>
</table>

---

## 📝 Licença

Este projeto é de uso acadêmico e está aberto para estudos, melhorias e referências.
