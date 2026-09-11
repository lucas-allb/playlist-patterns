package com.playlist.evaluation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.playlist.adapter.TrackCatalog;
import com.playlist.adapter.VinylCatalogAdapter;
import com.playlist.adapter.external.LegacyVinylCatalog;
import com.playlist.core.Track;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Avalia o exercício 2: padrão Adapter.
 */
@DisplayName("Exercício 2 - Adapter")
class Req02AdapterTest {

  private static final List<String> ROWS = List.of(
          "VNL-1001|BOHEMIAN RHAPSODY|MERCURY, FREDDIE|354000|N",
          "VNL-1002|  SWEET CHILD O MINE  |  ROSE, AXL  |356999|N",
          "VNL-1003|IMAGINE|LENNON, JOHN|183000|Y",
          "VNL-1004|REGISTRO CORROMPIDO|SEM CAMPOS",
          "VNL-1005|DURACAO INVALIDA|SILVA, ANA|abc|N");

  private static TrackCatalog catalogOf(List<String> rows) {
    return new VinylCatalogAdapter(new LegacyVinylCatalog(rows));
  }

  /**
   * Stub que conta as chamadas feitas ao sistema legado.
   */
  private static class SpyLegacyCatalog extends LegacyVinylCatalog {
    private int fetchAllCalls;
    private int findByNumberCalls;

    SpyLegacyCatalog(List<String> rows) {
      super(rows);
    }

    @Override
    public String[] fetchAllRecords() {
      fetchAllCalls++;
      return super.fetchAllRecords();
    }

    @Override
    public String findRecordByCatalogNumber(String catalogNumber) {
      findByNumberCalls++;
      return super.findRecordByCatalogNumber(catalogNumber);
    }
  }

  @Test
  @DisplayName("findAll converte todos os registros válidos e ignora os malformados")
  void findAllConvertsValidRecordsOnly() {
    List<Track> tracks = catalogOf(ROWS).findAll();

    assertEquals(3, tracks.size());
    assertEquals(List.of("VNL-1001", "VNL-1002", "VNL-1003"),
            tracks.stream().map(Track::id).toList());
  }

  @Test
  @DisplayName("Título e artista são convertidos do formato legado")
  void convertsTitleAndArtist() {
    Track track = catalogOf(ROWS).findAll().get(0);

    assertEquals("Bohemian Rhapsody", track.title());
    assertEquals("Freddie Mercury", track.artist());
  }

  @Test
  @DisplayName("Espaços em excesso do sistema legado são removidos")
  void trimsLegacyWhitespace() {
    Track track = catalogOf(ROWS).findAll().get(1);

    assertEquals("Sweet Child O Mine", track.title());
    assertEquals("Axl Rose", track.artist());
  }

  @Test
  @DisplayName("Duração é convertida de milissegundos para segundos, truncando")
  void convertsDurationToSeconds() {
    List<Track> tracks = catalogOf(ROWS).findAll();

    assertEquals(354, tracks.get(0).durationSeconds());
    assertEquals(356, tracks.get(1).durationSeconds());
  }

  @Test
  @DisplayName("A marcação de faixa premium é traduzida corretamente")
  void convertsPremiumFlag() {
    List<Track> tracks = catalogOf(ROWS).findAll();

    assertFalse(tracks.get(0).premium());
    assertTrue(tracks.get(2).premium());
  }

  @Test
  @DisplayName("findById devolve a faixa convertida e Optional vazio quando não existe")
  void findByIdConvertsAndHandlesAbsence() {
    TrackCatalog catalog = catalogOf(ROWS);

    Optional<Track> found = catalog.findById("VNL-1003");
    assertTrue(found.isPresent());
    assertEquals("Imagine", found.get().title());
    assertEquals("John Lennon", found.get().artist());
    assertEquals(183, found.get().durationSeconds());

    assertTrue(catalog.findById("VNL-9999").isEmpty());
    assertTrue(catalog.findById("VNL-1004").isEmpty());
  }

  @Test
  @DisplayName("findById delega a busca ao sistema legado em vez de varrer o catálogo")
  void findByIdDelegatesToLegacyLookup() {
    SpyLegacyCatalog spy = new SpyLegacyCatalog(ROWS);
    TrackCatalog catalog = new VinylCatalogAdapter(spy);

    catalog.findById("VNL-1001");

    assertEquals(1, spy.findByNumberCalls);
    assertEquals(0, spy.fetchAllCalls);
  }

  @Test
  @DisplayName("O catálogo real embarcado no projeto é lido corretamente")
  void readsBundledCatalog() {
    TrackCatalog catalog = new VinylCatalogAdapter(new LegacyVinylCatalog());
    List<Track> tracks = catalog.findAll();

    assertEquals(7, tracks.size());
    assertEquals("Chico Buarque", catalog.findById("VNL-0006").orElseThrow().artist());
    assertEquals(278, catalog.findById("VNL-0009").orElseThrow().durationSeconds());
    assertTrue(catalog.findById("VNL-0003").orElseThrow().premium());
  }
}
