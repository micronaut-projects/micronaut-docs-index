package io.micronaut.website;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

public class CategoryRendererImpl implements CategoryRenderer {
    private final String template;
    private final RepositoryRenderer repositoryRenderer;
    public CategoryRendererImpl(RepositoryRenderer repositoryRenderer) throws IOException {
        this.repositoryRenderer = repositoryRenderer;
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("category.html");
        this.template = Utils.readFromInputStream(inputStream);
    }

    @Override
    public String renderAsHtml(Category category) {
        return template.replaceAll("@title@", category.getTitle())
                .replaceAll("@image@", category.getImage())
                .replaceAll("@repositories@", category.getRepositories()
                        .stream()
                        .map(repositoryRenderer::renderAsHtml)
                        .collect(Collectors.joining("\n")));
    }
}
