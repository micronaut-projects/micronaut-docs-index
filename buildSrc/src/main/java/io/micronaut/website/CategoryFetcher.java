package io.micronaut.website;

import java.util.Optional;

@FunctionalInterface
public interface CategoryFetcher {
    Optional<Category> fetch(Type type);
}
