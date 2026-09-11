    package com.playlist.composite;

    import com.playlist.core.Track;
    import java.util.List;


    public interface MediaItem {
      String getName();
      int getDurationSeconds();
      int getTrackCount();
      List<Track> flatten();
    }
