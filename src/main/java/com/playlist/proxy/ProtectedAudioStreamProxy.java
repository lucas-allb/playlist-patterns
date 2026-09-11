package com.playlist.proxy;

import com.playlist.core.AccessDeniedException;
import com.playlist.core.Subscription;
import com.playlist.core.Track;
import java.util.function.Supplier;

/**
 * Proxy que controla o acesso ao {@link RemoteAudioStream}.
 *
 * Ele acumula três responsabilidades clássicas do padrão: proteção
 * (bloqueia faixas premium para o plano gratuito), lazy loading (só cria o
 * objeto real quando o áudio é realmente pedido) e cache (não baixa o mesmo
 * áudio duas vezes).
 */
public class ProtectedAudioStreamProxy implements AudioStream {

  /**
   * Cria o proxy com uma fábrica explícita do objeto real.
   *
   * @param track faixa que será transmitida.
   * @param plan plano de assinatura de quem está ouvindo.
   * @param loader fábrica que cria o stream real. Só pode ser chamada quando o
   *     áudio for realmente necessário.
   * @throws IllegalArgumentException se qualquer argumento for nulo.
   */
  public ProtectedAudioStreamProxy(Track track, Subscription plan, Supplier<AudioStream> loader) {
    throw new UnsupportedOperationException(
            "Exercício 3: implemente o construtor de ProtectedAudioStreamProxy");
  }

  /**
   * Cria o proxy usando {@link RemoteAudioStream} como objeto real.
   *
   * @param track faixa que será transmitida.
   * @param plan plano de assinatura de quem está ouvindo.
   */
  public ProtectedAudioStreamProxy(Track track, Subscription plan) {
    throw new UnsupportedOperationException(
            "Exercício 3: implemente o construtor de conveniência de ProtectedAudioStreamProxy");
  }

  /**
   * Indica se o objeto real já foi criado.
   *
   * @return {@code true} apenas depois que o stream real tiver sido carregado.
   */
  public boolean isLoaded() {
    throw new UnsupportedOperationException(
            "Exercício 3: implemente ProtectedAudioStreamProxy.isLoaded");
  }

  @Override
  public String getTrackId() {
    throw new UnsupportedOperationException(
            "Exercício 3: implemente ProtectedAudioStreamProxy.getTrackId");
  }

  /**
   * Devolve os bytes do áudio, respeitando plano, carga preguiçosa e cache.
   *
   * @return uma cópia dos bytes do áudio.
   * @throws AccessDeniedException se a faixa for premium e o plano for
   *     {@link Subscription#FREE}.
   */
  @Override
  public byte[] readBytes() {
    throw new UnsupportedOperationException(
            "Exercício 3: implemente ProtectedAudioStreamProxy.readBytes");
  }
}

