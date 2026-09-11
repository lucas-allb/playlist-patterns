package com.playlist.proxy;

/**
 * Subject do padrão Proxy: uma fonte de áudio que pode ser lida em bytes.
 */
public interface AudioStream {

  /**
   * Identificador da faixa associada ao stream.
   *
   * @return o id da faixa.
   */
  String getTrackId();

  /**
   * Lê o conteúdo de áudio.
   *
   * @return os bytes do áudio.
   */
  byte[] readBytes();
}
