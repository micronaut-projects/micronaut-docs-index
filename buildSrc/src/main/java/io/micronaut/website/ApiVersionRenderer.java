package io.micronaut.website;

@FunctionalInterface
public interface ApiVersionRenderer {
    String renderAsHtml(String version);
}
