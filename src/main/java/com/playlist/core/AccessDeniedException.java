package com.playlist.core;

/**
 * Lançada quando o plano da pessoa usuária não permite ouvir uma faixa.
 */
public class AccessDeniedException extends RuntimeException {

  /**
   * Cria a exceção com uma mensagem descritiva.
   *
   * @param message mensagem de erro.
   */
  public AccessDeniedException(String message) {
    super(message);
  }
}
