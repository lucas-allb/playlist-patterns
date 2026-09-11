package com.playlist.facade;

import com.playlist.adapter.TrackCatalog;
import com.playlist.composite.PlaylistNode;
import com.playlist.composite.TrackItem;
import com.playlist.core.Subscription;
import com.playlist.core.Track;
import com.playlist.core.TrackNotFoundException;
import com.playlist.decorator.AudioTrack;
import com.playlist.decorator.FadeInEffect;
import com.playlist.decorator.RawAudioTrack;
import com.playlist.decorator.VolumeEffect;
import com.playlist.proxy.ProtectedAudioStreamProxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlaylistFacade {

    private final TrackCatalog catalog;
    private final Subscription plan;
    private final Map<String, ProtectedAudioStreamProxy> streamCache = new HashMap<>();

  public PlaylistFacade(TrackCatalog catalog, Subscription plan) {
    if (catalog == null || plan == null){
        throw new IllegalArgumentException();
    }
    this.catalog = catalog;
    this.plan = plan;
  }


  public PlaylistNode buildLibrary(String name) {
    if (name == null){
        throw new IllegalArgumentException();
    }
    PlaylistNode playlist = new PlaylistNode(name);
      List<Track> tracks = catalog.findAll();
      if (tracks != null){
          for (Track track : tracks){
              playlist.add(new TrackItem(track));
          }
      }
      return playlist;
  }


  public byte[] listen(String trackId) {
    if (trackId == null || trackId.trim().isEmpty()){
        throw new IllegalArgumentException();
    }
    ProtectedAudioStreamProxy proxy = streamCache.computeIfAbsent(trackId, id -> {
        Track track = catalog.findById(id)
                .orElseThrow(() -> new TrackNotFoundException(id));
        return new ProtectedAudioStreamProxy(track, plan);
    });
    return proxy.readBytes();
  }


  public AudioTrack preview(String trackId, double volume, int fadeInSamples) {
    if (trackId == null || trackId.trim().isEmpty()){
        throw new IllegalArgumentException();
    }
    Track track = catalog.findById(trackId).orElseThrow(() -> new TrackNotFoundException(trackId));

    byte[] audioData = listen(trackId);
    double[] samples = new double[audioData.length];
    for (int i = 0; i < audioData.length; i++) {
        samples[i] = audioData[i] / 128.0;
    }
      AudioTrack rawTrack = new RawAudioTrack(track.title(), samples);
      AudioTrack decorated = new VolumeEffect(rawTrack, volume);
      return new FadeInEffect(decorated, fadeInSamples);
  }
}
