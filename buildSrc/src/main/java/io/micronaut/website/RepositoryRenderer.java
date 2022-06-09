package io.micronaut.website;

@FunctionalInterface
public interface RepositoryRenderer {
    String renderAsHtml(Repository repository);
}
