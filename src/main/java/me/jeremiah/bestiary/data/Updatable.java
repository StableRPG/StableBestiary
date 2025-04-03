package me.jeremiah.bestiary.data;

public interface Updatable {

  boolean hasBeenUpdated();

  void saveUpdates();

}
