package com.playlist.composite;


import com.playlist.core.Track;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PlaylistNode implements MediaItem {

  private final String name;
  private final List<MediaItem> children = new ArrayList<>();

  public PlaylistNode(String name) {
    if (name == null || name.trim().isEmpty()){
        throw new IllegalArgumentException();
    }
    this.name = name;
  }

  public PlaylistNode add(MediaItem item) {
    if (item == null){
        throw new IllegalArgumentException();
    }
    if (item == this){
        throw new IllegalArgumentException();
    }
    if (item instanceof PlaylistNode node && node.contains(this)){
        throw new IllegalArgumentException();
      }
    children.add(item);
    return this;
  }

  public boolean remove(MediaItem item) {
    return children.remove(item);
  }

  public List<MediaItem> getChildren() {
      return Collections.unmodifiableList(children);
  }

  public boolean contains(MediaItem item) {
    if (children.contains(item)){
        return true;
    }
    for (MediaItem child : children){
        if (child instanceof PlaylistNode node && node.contains(item)){
            return true;
        }
    }
    return false;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getDurationSeconds() {
    return children.stream().mapToInt(MediaItem::getDurationSeconds).sum();
  }

  @Override
  public int getTrackCount() {
    return children.stream().mapToInt(MediaItem::getTrackCount).sum();
  }

  @Override
  public List<Track> flatten() {
    List<Track> tracks = new ArrayList<>();
    for (MediaItem child : children){
        tracks.addAll(child.flatten());
    }
    return tracks;
  }
}
