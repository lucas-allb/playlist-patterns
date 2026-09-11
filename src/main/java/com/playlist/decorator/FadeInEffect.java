package com.playlist.decorator;

/**
 * Efeito que aplica uma rampa linear de volume nas primeiras amostras.
 */
public final class FadeInEffect extends AudioEffect {

  /**
   * Cria o efeito de fade in.
   *
   * @param wrapped áudio decorado.
   * @param sampleCount quantidade de amostras usadas na rampa.
   */
  public FadeInEffect(AudioTrack wrapped, int sampleCount) {
    super(wrapped);
    throw new UnsupportedOperationException("Exercício 4: implemente o construtor de FadeInEffect");
  }

  @Override
  protected String describe() {
    throw new UnsupportedOperationException("Exercício 4: implemente FadeInEffect.describe");
  }

  @Override
  public double[] getSamples() {
    throw new UnsupportedOperationException("Exercício 4: implemente FadeInEffect.getSamples");
  }
}
