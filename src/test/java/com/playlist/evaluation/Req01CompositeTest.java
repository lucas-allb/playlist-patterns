package com.playlist.evaluation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.playlist.composite.MediaItem;
import com.playlist.composite.PlaylistNode;
import com.playlist.composite.TrackItem;
import com.playlist.core.Track;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Avalia o exercício 1: padrão Composite.
 */
@DisplayName("Exercício 1 - Composite")
class Req01CompositeTest {

  private static final Track ROCK = new Track("T1", "Bohemian Rhapsody", "Freddie Mercury", 354,
          false);
  private static final Track FOLK = new Track("T2", "Ponta de Areia", "Milton Nascimento", 278,
          false);
  private static final Track POP = new Track("T3", "Imagine", "John Lennon", 183, true);

  @Test
  @DisplayName("TrackItem expõe nome, duração, contagem e faixa da música")
  void trackItemExposesTrackData() {
    TrackItem item = new TrackItem(ROCK);

    assertEquals("Bohemian Rhapsody", item.getName());
    assertEquals(354, item.getDurationSeconds());
    assertEquals(1, item.getTrackCount());
    assertSame(ROCK, item.getTrack());
    assertEquals(List.of(ROCK), item.flatten());
  }

  @Test
  @DisplayName("Playlist vazia tem duração zero, contagem zero e flatten vazio")
  void emptyPlaylistIsNeutral() {
    PlaylistNode playlist = new PlaylistNode("Vazia");

    assertEquals("Vazia", playlist.getName());
    assertEquals(0, playlist.getDurationSeconds());
    assertEquals(0, playlist.getTrackCount());
    assertTrue(playlist.flatten().isEmpty());
    assertTrue(playlist.getChildren().isEmpty());
  }

  @Test
  @DisplayName("Playlist soma duração e contagem dos filhos diretos")
  void playlistSumsDirectChildren() {
    PlaylistNode playlist = new PlaylistNode("Favoritas");
    playlist.add(new TrackItem(ROCK));
    playlist.add(new TrackItem(FOLK));

    assertEquals(632, playlist.getDurationSeconds());
    assertEquals(2, playlist.getTrackCount());
    assertEquals(2, playlist.getChildren().size());
  }

  @Test
  @DisplayName("Playlists aninhadas são somadas recursivamente")
  void nestedPlaylistsAreAggregated() {
    PlaylistNode brasil = new PlaylistNode("Brasil");
    brasil.add(new TrackItem(FOLK));

    PlaylistNode raiz = new PlaylistNode("Biblioteca");
    raiz.add(new TrackItem(ROCK));
    raiz.add(brasil);
    raiz.add(new TrackItem(POP));

    assertEquals(815, raiz.getDurationSeconds());
    assertEquals(3, raiz.getTrackCount());
    assertEquals(3, raiz.getChildren().size());
  }

  @Test
  @DisplayName("flatten devolve as faixas em profundidade e na ordem de inserção")
  void flattenKeepsDepthFirstOrder() {
    PlaylistNode interna = new PlaylistNode("Interna");
    interna.add(new TrackItem(FOLK));
    interna.add(new TrackItem(POP));

    PlaylistNode raiz = new PlaylistNode("Raiz");
    raiz.add(new TrackItem(ROCK));
    raiz.add(interna);

    assertEquals(List.of(ROCK, FOLK, POP), raiz.flatten());
  }

  @Test
  @DisplayName("getChildren é imutável, remove e contains funcionam")
  void childrenAreImmutableAndRemovable() {
    TrackItem rockItem = new TrackItem(ROCK);
    PlaylistNode interna = new PlaylistNode("Interna");
    interna.add(new TrackItem(POP));

    PlaylistNode raiz = new PlaylistNode("Raiz");
    raiz.add(rockItem);
    raiz.add(interna);

    List<MediaItem> children = raiz.getChildren();
    assertThrows(UnsupportedOperationException.class, () -> children.add(new TrackItem(FOLK)));

    assertTrue(raiz.contains(rockItem));
    assertTrue(raiz.contains(interna));
    assertFalse(raiz.contains(new TrackItem(FOLK)));

    assertTrue(raiz.remove(rockItem));
    assertFalse(raiz.remove(rockItem));
    assertEquals(1, raiz.getChildren().size());
  }

  @Test
  @DisplayName("add rejeita nulo, a própria playlist e ciclos")
  void addRejectsInvalidItems() {
    PlaylistNode raiz = new PlaylistNode("Raiz");
    PlaylistNode filha = new PlaylistNode("Filha");
    raiz.add(filha);

    assertThrows(IllegalArgumentException.class, () -> raiz.add(null));
    assertThrows(IllegalArgumentException.class, () -> raiz.add(raiz));
    assertThrows(IllegalArgumentException.class, () -> filha.add(raiz));
  }
}
