package com.playlist.adapter;

import com.playlist.adapter.external.LegacyVinylCatalog;
import com.playlist.core.Track;
import java.util.List;
import java.util.Optional;

/**
 * Adapter que converte os registros do {@link LegacyVinylCatalog} para {@link Track}.
 */
public class VinylCatalogAdapter implements TrackCatalog {

  /**
   * Cria o adapter em cima do sistema legado.
   *
   * @param legacyCatalog catálogo legado a ser adaptado. Não pode ser nulo.
   * @throws IllegalArgumentException se o catálogo for nulo.
   */
  public VinylCatalogAdapter(LegacyVinylCatalog legacyCatalog) {
    throw new UnsupportedOperationException(
            "Requisito 2: implemente o construtor de VinylCatalogAdapter");
  }

  @Override
  public List<Track> findAll() {
    throw new UnsupportedOperationException("Exercício 2: implemente VinylCatalogAdapter.findAll");
  }

  @Override
  public Optional<Track> findById(String id) {
    throw new UnsupportedOperationException("Exercício 2: implemente VinylCatalogAdapter.findById");
  }
}
