package io.micronaut.website;

@FunctionalInterface
public interface DocVersionRenderer {
    String renderAsHtml(String version);
}
