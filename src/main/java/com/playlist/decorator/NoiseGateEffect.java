package com.playlist.decorator;

/**
 * Efeito que zera amostras cujo valor absoluto fica abaixo de um limiar.
 */
public final class NoiseGateEffect extends AudioEffect {

  /**
   * Cria o efeito de noise gate.
   *
   * @param wrapped áudio decorado.
   * @param threshold limiar de corte.
   */
  public NoiseGateEffect(AudioTrack wrapped, double threshold) {
    super(wrapped);
    throw new UnsupportedOperationException(
            "Exercício 4: implemente o construtor de NoiseGateEffect");
  }

  @Override
  protected String describe() {
    throw new UnsupportedOperationException("Exercício 4: implemente NoiseGateEffect.describe");
  }

  @Override
  public double[] getSamples() {
    throw new UnsupportedOperationException("Exercício 4: implemente NoiseGateEffect.getSamples");
  }
}
