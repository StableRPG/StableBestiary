package org.stablerpg.stablebestiary;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public final class BestiaryLoader implements PluginLoader {

  private static final List<String> LIBRARIES = List.of(
    "com.zaxxer:HikariCP:6.3.0",
    "com.h2database:h2:2.3.232",
    "org.mariadb.jdbc:mariadb-java-client:3.5.3"
  );

  @Override
  public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
    MavenLibraryResolver resolver = new MavenLibraryResolver();
    resolver.addRepository(new RemoteRepository.Builder("maven", "default", "https://repo1.maven.org/maven2/").build());
    LIBRARIES
      .stream()
      .map(DefaultArtifact::new)
      .map(artifact -> new Dependency(artifact, null))
      .forEach(resolver::addDependency);
    classpathBuilder.addLibrary(resolver);
  }

}