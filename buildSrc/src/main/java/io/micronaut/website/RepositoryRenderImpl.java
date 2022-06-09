package io.micronaut.website;

import java.io.IOException;
import java.io.InputStream;

public class RepositoryRenderImpl implements RepositoryRenderer {
    private String template;
    public RepositoryRenderImpl() throws IOException {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("repository.html");
        this.template = Utils.readFromInputStream(inputStream);
    }

    @Override
    public String renderAsHtml(Repository repository) {
        return template.replaceAll("@title@", repository.getTitle())
                .replaceAll("@slug@", repository.getSlug())
                .replaceAll("@description@", repository.getDescription())
                .replaceAll("@version@", repository.isSnapshot()  ? "snapshot" : "latest");
    }
}
