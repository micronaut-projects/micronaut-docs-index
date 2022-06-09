package io.micronaut.website;

public interface Repository {
    String getSlug();
    String getTitle();
    String getDescription();
    boolean isSnapshot();
}
