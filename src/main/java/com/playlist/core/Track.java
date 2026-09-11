package com.playlist.core;

/**
 * Representa uma faixa musical dentro do catálogo de Playlist.
 *
 * @param id identificador único da faixa (ex.: "VNL-0001").
 * @param title título da faixa.
 * @param artist nome do artista no formato "Nome Sobrenome".
 * @param durationSeconds duração em segundos.
 * @param premium indica se a faixa é exclusiva de assinantes.
 */
public record Track(String id, String title, String artist, int durationSeconds, boolean premium) {

  /**
   * Valida os dados da faixa no momento da criação.
   */
  public Track {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("id da faixa não pode ser vazio");
    }
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("título da faixa não pode ser vazio");
    }
    if (durationSeconds < 0) {
      throw new IllegalArgumentException("duração não pode ser negativa");
    }
  }
}

