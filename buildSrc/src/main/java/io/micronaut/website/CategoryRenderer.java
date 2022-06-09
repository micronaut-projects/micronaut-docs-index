package io.micronaut.website;

@FunctionalInterface
public interface CategoryRenderer {
    String renderAsHtml(Category category);
}
