package com.playlist.adapter.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class LegacyVinylCatalog {

  private static final String DEFAULT_RESOURCE = "/legacy/vinyl-catalog.psv";

  private final List<String> rows;


  public LegacyVinylCatalog() {
    this.rows = readResource(DEFAULT_RESOURCE);
  }


  public LegacyVinylCatalog(List<String> rows) {
    this.rows = List.copyOf(rows);
  }


  public String[] fetchAllRecords() {
    return rows.toArray(new String[0]);
  }


  public String findRecordByCatalogNumber(String catalogNumber) {
    if (catalogNumber == null) {
      return null;
    }
    String wanted = catalogNumber.trim();
    for (String row : rows) {
      int separator = row.indexOf('|');
      String number = separator < 0 ? row : row.substring(0, separator);
      if (number.trim().equals(wanted)) {
        return row;
      }
    }
    return null;
  }

  private static List<String> readResource(String resource) {
    try (InputStream stream = LegacyVinylCatalog.class.getResourceAsStream(resource)) {
      if (stream == null) {
        throw new IllegalStateException("Arquivo do catálogo legado não encontrado: " + resource);
      }
      BufferedReader reader =
              new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
      List<String> lines = new ArrayList<>();
      String line = reader.readLine();
      while (line != null) {
        String trimmed = line.trim();
        if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
          lines.add(line);
        }
        line = reader.readLine();
      }
      return List.copyOf(lines);
    } catch (IOException exception) {
      throw new UncheckedIOException(exception);
    }
  }
}
