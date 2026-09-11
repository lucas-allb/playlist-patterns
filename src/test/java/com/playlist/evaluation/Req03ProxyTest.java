package com.playlist.evaluation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.playlist.core.AccessDeniedException;
import com.playlist.core.Subscription;
import com.playlist.core.Track;
import com.playlist.proxy.AudioStream;
import com.playlist.proxy.ProtectedAudioStreamProxy;
import com.playlist.proxy.RemoteAudioStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Avalia o exercício 3: padrão Proxy.
 */
@DisplayName("Exercício 3 - Proxy")
class Req03ProxyTest {

  private static final Track FREE_TRACK =
          new Track("VNL-0001", "Bohemian Rhapsody", "Freddie Mercury", 354, false);
  private static final Track PREMIUM_TRACK =
          new Track("VNL-0005", "Imagine", "John Lennon", 183, true);

  private AtomicInteger loaderCalls;

  @BeforeEach
  void setUp() {
    loaderCalls = new AtomicInteger();
    RemoteAudioStream.resetInstanceCounter();
  }

  private Supplier<AudioStream> countingLoader(Track track) {
    return () -> {
      loaderCalls.incrementAndGet();
      return new RemoteAudioStream(track);
    };
  }

  @Test
  @DisplayName("getTrackId responde sem criar o objeto real")
  void trackIdDoesNotTriggerLoading() {
    ProtectedAudioStreamProxy proxy =
            new ProtectedAudioStreamProxy(FREE_TRACK, Subscription.FREE, countingLoader(FREE_TRACK));

    assertEquals("VNL-0001", proxy.getTrackId());
    assertFalse(proxy.isLoaded());
    assertEquals(0, loaderCalls.get());
    assertEquals(0, RemoteAudioStream.getInstancesCreated());
  }

  @Test
  @DisplayName("O objeto real só é criado na primeira leitura do áudio")
  void loadsRealSubjectOnFirstRead() {
    ProtectedAudioStreamProxy proxy =
            new ProtectedAudioStreamProxy(FREE_TRACK, Subscription.FREE, countingLoader(FREE_TRACK));

    byte[] bytes = proxy.readBytes();

    assertTrue(proxy.isLoaded());
    assertEquals(1, loaderCalls.get());
    assertArrayEquals("AUDIO::VNL-0001::354".getBytes(StandardCharsets.UTF_8), bytes);
  }

  @Test
  @DisplayName("Leituras seguintes usam o cache, sem novo acesso remoto")
  void cachesAudioBytes() {
    RemoteAudioStream real = new RemoteAudioStream(FREE_TRACK);
    ProtectedAudioStreamProxy proxy = new ProtectedAudioStreamProxy(
            FREE_TRACK, Subscription.PREMIUM, () -> {
      loaderCalls.incrementAndGet();
      return real;
    });

    byte[] first = proxy.readBytes();
    byte[] second = proxy.readBytes();
    byte[] third = proxy.readBytes();

    assertArrayEquals(first, second);
    assertArrayEquals(first, third);
    assertEquals(1, loaderCalls.get());
    assertEquals(1, real.getFetchCount());
  }

  @Test
  @DisplayName("Faixa premium é bloqueada no plano gratuito e o objeto real não é criado")
  void blocksPremiumTrackForFreePlan() {
    ProtectedAudioStreamProxy proxy = new ProtectedAudioStreamProxy(
            PREMIUM_TRACK, Subscription.FREE, countingLoader(PREMIUM_TRACK));

    assertThrows(AccessDeniedException.class, proxy::readBytes);
    assertFalse(proxy.isLoaded());
    assertEquals(0, loaderCalls.get());
    assertEquals(0, RemoteAudioStream.getInstancesCreated());
  }

  @Test
  @DisplayName("Faixa premium é liberada para quem assina o plano premium")
  void allowsPremiumTrackForPremiumPlan() {
    ProtectedAudioStreamProxy proxy = new ProtectedAudioStreamProxy(
            PREMIUM_TRACK, Subscription.PREMIUM, countingLoader(PREMIUM_TRACK));

    byte[] bytes = proxy.readBytes();

    assertArrayEquals("AUDIO::VNL-0005::183".getBytes(StandardCharsets.UTF_8), bytes);
    assertEquals(1, loaderCalls.get());
  }

  @Test
  @DisplayName("O cache é protegido: alterar o array devolvido não corrompe o proxy")
  void returnsDefensiveCopy() {
    ProtectedAudioStreamProxy proxy =
            new ProtectedAudioStreamProxy(FREE_TRACK, Subscription.FREE, countingLoader(FREE_TRACK));

    byte[] first = proxy.readBytes();
    Arrays.fill(first, (byte) 0);
    byte[] second = proxy.readBytes();

    assertArrayEquals("AUDIO::VNL-0001::354".getBytes(StandardCharsets.UTF_8), second);
  }

  @Test
  @DisplayName("O construtor de conveniência usa o RemoteAudioStream, mas continua preguiçoso")
  void convenienceConstructorStaysLazy() {
    ProtectedAudioStreamProxy proxy =
            new ProtectedAudioStreamProxy(FREE_TRACK, Subscription.FREE);

    assertEquals("VNL-0001", proxy.getTrackId());
    assertEquals(0, RemoteAudioStream.getInstancesCreated());

    proxy.readBytes();
    proxy.readBytes();

    assertEquals(1, RemoteAudioStream.getInstancesCreated());
    assertArrayEquals("AUDIO::VNL-0001::354".getBytes(StandardCharsets.UTF_8), proxy.readBytes());
  }

  @Test
  @DisplayName("O construtor rejeita argumentos nulos")
  void rejectsNullArguments() {
    assertThrows(IllegalArgumentException.class,
            () -> new ProtectedAudioStreamProxy(null, Subscription.FREE, countingLoader(FREE_TRACK)));
    assertThrows(IllegalArgumentException.class,
            () -> new ProtectedAudioStreamProxy(FREE_TRACK, null, countingLoader(FREE_TRACK)));
    assertThrows(IllegalArgumentException.class,
            () -> new ProtectedAudioStreamProxy(FREE_TRACK, Subscription.FREE, null));
  }
}
