package org.stablerpg.stablebestiary.data;

public interface Updatable {

  boolean hasBeenUpdated();

  void saveUpdates();

}
