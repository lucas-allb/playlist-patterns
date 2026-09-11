package com.playlist.adapter;

import com.playlist.adapter.external.LegacyVinylCatalog;
import com.playlist.core.Track;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VinylCatalogAdapter implements TrackCatalog {

  private final LegacyVinylCatalog legacyVinylCatalog;

  public VinylCatalogAdapter(LegacyVinylCatalog legacyCatalog) {
    if (legacyCatalog == null){
        throw new IllegalArgumentException();
    }
    this.legacyVinylCatalog = legacyCatalog;
  }

  @Override
  public List<Track> findAll() {
    String[] records = legacyVinylCatalog.fetchAllRecords();
    List<Track> tracks = new ArrayList<>();
    for (String record : records){
        parseRecord(record).ifPresent(tracks::add);
    }
    return tracks;
  }

  @Override
  public Optional<Track> findById(String id) {
    if (id == null || id.trim().isEmpty()){
        return Optional.empty();
    }
    String record = legacyVinylCatalog.findRecordByCatalogNumber(id);
    if (record == null){
        return Optional.empty();
    }
    return parseRecord(record);
  }

  private Optional<Track> parseRecord(String record){
      if (record == null){
          return Optional.empty();
      }

      String[] parts = record.split("\\|", -1);
      if (parts.length !=5){
          return Optional.empty();
      }

      String id = parts[0].trim();
      String rawTitle = parts[1].trim();
      String rawArtist = parts[2].trim();
      String rawMs = parts[3].trim();
      String rawPremium = parts[4].trim();

      if (id.isEmpty() || rawTitle.isEmpty()){
          return Optional.empty();
      }

      int ms;
      try {
          ms = Integer.parseInt(rawMs);
          if (ms < 0) {
              return Optional.empty();
          }
      } catch (NumberFormatException e) {
          return Optional.empty();
      }

      int durationSeconds = ms / 1000;
      boolean isPremium = "Y".equalsIgnoreCase(rawPremium);

      String title = formatTitle(rawTitle);
      String artist = formatArtist(rawArtist);

      return Optional.of(new Track(id, title, artist, durationSeconds, isPremium));
  }

  private String formatTitle(String rawTitle){
      return Arrays.stream(rawTitle.split("\\s+"))
              .filter(s -> !s.isEmpty())
              .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
              .collect(Collectors.joining(" "));
  }

  private String formatArtist(String rawArtist){
      String[] parts = rawArtist.split(",");
      if (parts.length != 2) {
          return rawArtist;
      }
      String lastname = formatTitle(parts[0].trim());
      String firstName = formatTitle(parts[1]).trim();
      return firstName + " " + lastname;
  }
}
