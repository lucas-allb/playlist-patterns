package com.playlist.decorator;

/**
 * Decorator abstrato: envolve outro {@link AudioTrack} e acrescenta um efeito.
 */
public abstract class AudioEffect implements AudioTrack {

  /**
   * O áudio decorado por este efeito.
   */
  protected final AudioTrack wrapped;

  /**
   * Guarda o áudio que será decorado.
   *
   * @param wrapped áudio decorado. Não pode ser nulo.
   * @throws IllegalArgumentException se {@code wrapped} for nulo.
   */
  protected AudioEffect(AudioTrack wrapped) {
    throw new UnsupportedOperationException("Exercício 4: implemente o construtor de AudioEffect");
  }

  /**
   * Nome do efeito, já formatado, usado na cadeia de efeitos.
   *
   * @return por exemplo {@code "volume(2.0)"}.
   */
  protected abstract String describe();

  @Override
  public String getTitle() {
    throw new UnsupportedOperationException("Exercício 4: implemente AudioEffect.getTitle");
  }

  @Override
  public String getEffectChain() {
    throw new UnsupportedOperationException("Exercício 4: implemente AudioEffect.getEffectChain");
  }
}
