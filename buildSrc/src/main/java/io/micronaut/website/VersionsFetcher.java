package io.micronaut.website;

import java.util.List;

@FunctionalInterface
public interface VersionsFetcher {
    List<String> versions();
}
