package com.playlist.decorator;


public final class RawAudioTrack implements AudioTrack {

  private final String title;
  private final double[] samples;

  public RawAudioTrack(String title, double[] samples) {
    if (title == null || samples == null) {
      throw new IllegalArgumentException("title e samples não podem ser nulos");
    }
    this.title = title;
    this.samples = samples.clone();
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public double[] getSamples() {
    return samples.clone();
  }

  @Override
  public String getEffectChain() {
    return "original";
  }
}
