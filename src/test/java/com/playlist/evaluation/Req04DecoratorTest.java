package com.playlist.evaluation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.playlist.decorator.AudioTrack;
import com.playlist.decorator.FadeInEffect;
import com.playlist.decorator.NoiseGateEffect;
import com.playlist.decorator.RawAudioTrack;
import com.playlist.decorator.VolumeEffect;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Avalia o exercício 4: padrão Decorator.
 */
@DisplayName("Exercício 4 - Decorator")
class Req04DecoratorTest {

  private static final double DELTA = 1e-9;
  private static final double[] BASE = {0.5, -0.25, 0.8, 0.05, -0.6};

  private static AudioTrack raw() {
    return new RawAudioTrack("Ponta de Areia", BASE.clone());
  }

  @Test
  @DisplayName("VolumeEffect multiplica as amostras e corta em [-1.0, 1.0]")
  void volumeEffectAmplifiesAndClamps() {
    AudioTrack track = new VolumeEffect(raw(), 2.0);

    assertArrayEquals(new double[] {1.0, -0.5, 1.0, 0.1, -1.0}, track.getSamples(), DELTA);
  }

  @Test
  @DisplayName("FadeInEffect aplica rampa linear nas primeiras amostras")
  void fadeInEffectAppliesLinearRamp() {
    AudioTrack track = new FadeInEffect(raw(), 3);

    assertArrayEquals(
            new double[] {0.0, -0.25 / 3.0, 0.8 * 2.0 / 3.0, 0.05, -0.6},
            track.getSamples(),
            DELTA);
  }

  @Test
  @DisplayName("NoiseGateEffect zera as amostras abaixo do limiar")
  void noiseGateSilencesQuietSamples() {
    AudioTrack track = new NoiseGateEffect(raw(), 0.3);

    assertArrayEquals(new double[] {0.5, 0.0, 0.8, 0.0, -0.6}, track.getSamples(), DELTA);
  }

  @Test
  @DisplayName("A cadeia de efeitos preserva o título da faixa")
  void chainPreservesTitle() {
    AudioTrack track = new NoiseGateEffect(new FadeInEffect(new VolumeEffect(raw(), 1.5), 2), 0.1);

    assertEquals("Ponta de Areia", track.getTitle());
  }

  @Test
  @DisplayName("getEffectChain descreve os efeitos na ordem em que foram aplicados")
  void chainDescriptionFollowsApplicationOrder() {
    AudioTrack track = new NoiseGateEffect(new FadeInEffect(new VolumeEffect(raw(), 2.0), 3), 0.3);

    assertEquals("original -> volume(2.0) -> fadeIn(3) -> noiseGate(0.30)", track.getEffectChain());
    assertEquals("original", raw().getEffectChain());
    assertEquals("original -> fadeIn(0)", new FadeInEffect(raw(), 0).getEffectChain());
  }

  @Test
  @DisplayName("Os efeitos não alteram o áudio original nem entre chamadas")
  void effectsDoNotMutateTheSource() {
    AudioTrack source = raw();
    AudioTrack track = new VolumeEffect(source, 2.0);

    double[] first = track.getSamples();
    double[] second = track.getSamples();

    assertArrayEquals(first, second, DELTA);
    assertArrayEquals(BASE, source.getSamples(), DELTA);
  }

  @Test
  @DisplayName("A ordem dos decoradores muda o resultado")
  void decoratorOrderMatters() {
    double[] samples = {0.5, 0.8, 0.5};

    AudioTrack volumeThenFade =
            new FadeInEffect(new VolumeEffect(new RawAudioTrack("x", samples), 2.0), 2);
    AudioTrack fadeThenVolume =
            new VolumeEffect(new FadeInEffect(new RawAudioTrack("x", samples), 2), 2.0);

    assertArrayEquals(new double[] {0.0, 0.5, 1.0}, volumeThenFade.getSamples(), DELTA);
    assertArrayEquals(new double[] {0.0, 0.8, 1.0}, fadeThenVolume.getSamples(), DELTA);
  }

  @Test
  @DisplayName("O decorador abstrato rejeita um áudio nulo")
  void rejectsNullInnerTrack() {
    assertThrows(IllegalArgumentException.class, () -> new VolumeEffect(null, 2.0));
    assertThrows(IllegalArgumentException.class, () -> new FadeInEffect(null, 2));
    assertThrows(IllegalArgumentException.class, () -> new NoiseGateEffect(null, 0.1));
  }
}
