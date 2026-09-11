package com.playlist.decorator;

/**
 * Componente do padrão Decorator: um áudio pronto para tocar.
 */
public interface AudioTrack {

  /**
   * Título do áudio.
   *
   * @return o título, preservado por todos os efeitos.
   */
  String getTitle();

  /**
   * Amostras de áudio já processadas, no intervalo {@code [-1.0, 1.0]}.
   *
   * @return um array com as amostras resultantes.
   */
  double[] getSamples();

  /**
   * Descrição textual da cadeia de efeitos aplicada.
   *
   * @return por exemplo "original -> volume(2.0) -> fadeIn(4)".
   */
  String getEffectChain();
}
