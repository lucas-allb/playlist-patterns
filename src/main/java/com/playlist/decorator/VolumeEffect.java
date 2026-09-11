package com.playlist.decorator;

/**
 * Efeito que multiplica o volume das amostras, com corte em {@code [-1.0, 1.0]}.
 */
public final class VolumeEffect extends AudioEffect {

  /**
   * Cria o efeito de volume.
   *
   * @param wrapped áudio decorado.
   * @param factor fator multiplicador do volume.
   */
  public VolumeEffect(AudioTrack wrapped, double factor) {
    super(wrapped);
    throw new UnsupportedOperationException("Exercício 4: implemente o construtor de VolumeEffect");
  }

  @Override
  protected String describe() {
    throw new UnsupportedOperationException("Exercício 4: implemente VolumeEffect.describe");
  }

  @Override
  public double[] getSamples() {
    throw new UnsupportedOperationException("Exercício 4: implemente VolumeEffect.getSamples");
  }
}
