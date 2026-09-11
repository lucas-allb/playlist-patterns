package com.playlist.core;

/**
 * Lançada quando uma faixa não é encontrada no catálogo.
 */
public class TrackNotFoundException extends RuntimeException {

  /**
   * Cria a exceção com uma mensagem descritiva.
   *
   * @param message mensagem de erro.
   */
  public TrackNotFoundException(String message) {
    super(message);
  }
}

