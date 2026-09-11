package com.playlist.decorator;

import java.util.Locale;

public final class NoiseGateEffect extends AudioEffect {

    private final double threshold;

  public NoiseGateEffect(AudioTrack wrapped, double threshold) {
    super(wrapped);
    if (threshold < 0.0 || threshold >1.0){
        throw new IllegalArgumentException();
    }
    this.threshold = threshold;
  }

  public NoiseGateEffect(AudioTrack wrapped, int threshold){
      this(wrapped, (double) threshold);
  }

  @Override
  protected String describe() {
      if (threshold == (long) threshold) {
          return String.format(Locale.US, "noiseGate(%d)", (long) threshold);
      }
      return String.format(Locale.US, "noiseGate(%.2f)", threshold);
  }

  @Override
  public double[] getSamples() {
    double[] original = wrapped.getSamples();
    double[] processed = new double[original.length];
    for (int i = 0; i < original.length; i++){
        if (Math.abs(original[i]) < threshold){
            processed[i] = 0.0;
        } else{
            processed[i] = original[i];
        }
    }
    return processed;
  }
}
