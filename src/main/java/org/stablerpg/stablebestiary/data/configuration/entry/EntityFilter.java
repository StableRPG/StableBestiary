package org.stablerpg.stablebestiary.data.configuration.entry;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

@Setter
@Getter
public class EntityFilter {

  private EntityType[] entityTypes;

  public EntityFilter() {

  }

  public boolean matches(LivingEntity entity) {
    for (EntityType entityType : entityTypes)
      if (entityType.equals(entity.getType()))
        return true;
    return false;
  }

}
