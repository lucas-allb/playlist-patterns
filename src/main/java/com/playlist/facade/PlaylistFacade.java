package com.playlist.facade;

import com.playlist.adapter.TrackCatalog;
import com.playlist.composite.PlaylistNode;
import com.playlist.core.Subscription;
import com.playlist.core.TrackNotFoundException;
import com.playlist.decorator.AudioTrack;

/**
 * Fachada que esconde do mundo externo a colaboração entre catálogo, playlists,
 * streams protegidos e efeitos de áudio.
 *
 * Quem usa a Playlist precisa conhecer apenas esta classe.
 */
public class PlaylistFacade {

  /**
   * Monta a fachada.
   *
   * @param catalog catálogo de faixas já adaptado.
   * @param plan plano de assinatura de quem está usando o sistema.
   * @throws IllegalArgumentException se qualquer argumento for nulo.
   */
  public PlaylistFacade(TrackCatalog catalog, Subscription plan) {
    throw new UnsupportedOperationException("Exercício 5: implemente o construtor de PlaylistFacade");
  }

  /**
   * Monta uma playlist com todas as faixas do catálogo, na ordem em que o catálogo as devolve.
   *
   * @param name nome da playlist criada.
   * @return a playlist preenchida.
   */
  public PlaylistNode buildLibrary(String name) {
    throw new UnsupportedOperationException("Exercício 5: implemente PlaylistFacade.buildLibrary");
  }

  /**
   * Devolve os bytes de áudio de uma faixa, respeitando o plano de assinatura.
   *
   * @param trackId identificador da faixa.
   * @return os bytes do áudio.
   * @throws TrackNotFoundException se a faixa não existir no catálogo.
   */
  public byte[] listen(String trackId) {
    throw new UnsupportedOperationException("Exercício 5: implemente PlaylistFacade.listen");
  }

  /**
   * Monta uma prévia da faixa com volume ajustado e fade in.
   *
   * @param trackId identificador da faixa.
   * @param volume fator de volume aplicado primeiro.
   * @param fadeInSamples quantidade de amostras do fade in, aplicado depois.
   * @return o áudio já decorado.
   * @throws TrackNotFoundException se a faixa não existir no catálogo.
   */
  public AudioTrack preview(String trackId, double volume, int fadeInSamples) {
    throw new UnsupportedOperationException("Exercício 5: implemente PlaylistFacade.preview");
  }
}
