package com.playlist.evaluation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.playlist.adapter.TrackCatalog;
import com.playlist.composite.PlaylistNode;
import com.playlist.core.AccessDeniedException;
import com.playlist.core.Subscription;
import com.playlist.core.Track;
import com.playlist.core.TrackNotFoundException;
import com.playlist.decorator.AudioTrack;
import com.playlist.facade.PlaylistFacade;
import com.playlist.proxy.RemoteAudioStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Avalia o exercício 5: padrão Facade, integrando os demais padrões.
 */
@DisplayName("Exercício 5 - Facade")
class Req05FacadeTest {

  private static final double DELTA = 1e-9;

  private static final Track ROCK =
          new Track("VNL-0001", "Bohemian Rhapsody", "Freddie Mercury", 354, false);
  private static final Track POP =
          new Track("VNL-0005", "Imagine", "John Lennon", 183, true);

  /**
   * Catálogo em memória, para isolar a fachada do sistema legado.
   */
  private static class FakeCatalog implements TrackCatalog {
    @Override
    public List<Track> findAll() {
      return List.of(ROCK, POP);
    }

    @Override
    public Optional<Track> findById(String id) {
      return findAll().stream().filter(track -> track.id().equals(id)).findFirst();
    }
  }

  @BeforeEach
  void setUp() {
    RemoteAudioStream.resetInstanceCounter();
  }

  @Test
  @DisplayName("buildLibrary monta uma playlist com todas as faixas do catálogo")
  void buildLibraryUsesTheCatalog() {
    PlaylistFacade facade = new PlaylistFacade(new FakeCatalog(), Subscription.PREMIUM);

    PlaylistNode library = facade.buildLibrary("Biblioteca");

    assertEquals("Biblioteca", library.getName());
    assertEquals(2, library.getTrackCount());
    assertEquals(537, library.getDurationSeconds());
    assertEquals(List.of(ROCK, POP), library.flatten());
  }

  @Test
  @DisplayName("listen devolve os bytes da faixa e reaproveita a conexão já aberta")
  void listenReusesTheStream() {
    PlaylistFacade facade = new PlaylistFacade(new FakeCatalog(), Subscription.PREMIUM);

    byte[] first = facade.listen("VNL-0001");
    byte[] second = facade.listen("VNL-0001");

    assertArrayEquals("AUDIO::VNL-0001::354".getBytes(StandardCharsets.UTF_8), first);
    assertArrayEquals(first, second);
    assertEquals(1, RemoteAudioStream.getInstancesCreated());
  }

  @Test
  @DisplayName("listen respeita o plano e falha quando a faixa não existe")
  void listenEnforcesPlanAndExistence() {
    PlaylistFacade free = new PlaylistFacade(new FakeCatalog(), Subscription.FREE);

    assertThrows(TrackNotFoundException.class, () -> free.listen("VNL-9999"));
    assertThrows(AccessDeniedException.class, () -> free.listen("VNL-0005"));
  }

  @Test
  @DisplayName("preview aplica volume e fade in em cima do áudio da faixa")
  void previewAppliesEffects() {
    PlaylistFacade facade = new PlaylistFacade(new FakeCatalog(), Subscription.PREMIUM);

    AudioTrack preview = facade.preview("VNL-0001", 2.0, 4);

    byte[] bytes = "AUDIO::VNL-0001::354".getBytes(StandardCharsets.UTF_8);
    double[] expected = new double[bytes.length];
    for (int index = 0; index < bytes.length; index++) {
      double value = bytes[index] / 128.0 * 2.0;
      expected[index] = Math.max(-1.0, Math.min(1.0, value));
    }
    for (int index = 0; index < 4; index++) {
      expected[index] = expected[index] * (index / 4.0);
    }

    assertEquals("Bohemian Rhapsody", preview.getTitle());
    assertEquals("original -> volume(2.0) -> fadeIn(4)", preview.getEffectChain());
    assertArrayEquals(expected, preview.getSamples(), DELTA);
  }
}
