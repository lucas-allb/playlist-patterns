package com.playlist.decorator;
import java.util.Locale;


public final class FadeInEffect extends AudioEffect {

  private final double durationSeconds;

  public FadeInEffect(AudioTrack wrapped, double durationSeconds) {
    super(wrapped);
      if (durationSeconds < 0.0) {
          throw new IllegalArgumentException("A duração do FadeIn não pode ser negativa.");
      }
      this.durationSeconds = durationSeconds;
  }

  public FadeInEffect(AudioTrack wrapped, int durationSeconds){
      this(wrapped, (double) durationSeconds);
  }

  @Override
  protected String describe() {
      if (durationSeconds == (long) durationSeconds) {
          return String.format(Locale.US, "fadeIn(%d)", (long) durationSeconds);
      }
      return String.format(Locale.US, "fadeIn(%.1f)", durationSeconds);
  }

  @Override
  public double[] getSamples() {
      double[] original = wrapped.getSamples();
      double[] processed = new double[original.length];
      int total = original.length;

      if (total == 0) {
          return processed;
      }

      for (int i = 0; i < total; i++) {
          double factor = (double) i / total;
          processed[i] = original[i] * factor;
      }

      return processed;
  }
}
