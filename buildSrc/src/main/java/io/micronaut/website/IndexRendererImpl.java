package io.micronaut.website;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class IndexRendererImpl implements IndexRenderer {
    private final String template;
    private final CategoryRenderer categoryRenderer;
    private final CategoryFetcher categoryFetcher;
    private final VersionsFetcher versionsFetcher;
    private final DocVersionRenderer docVersionRenderer;
    private final ApiVersionRenderer apiVersionRenderer;

    public IndexRendererImpl(CategoryRenderer categoryRenderer,
                             CategoryFetcher categoryFetcher,
                             VersionsFetcher versionsFetcher,
                             DocVersionRenderer docVersionRenderer,
    ApiVersionRenderer apiVersionRenderer) throws IOException {
        this.categoryRenderer = categoryRenderer;
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("index.html");
        this.template = Utils.readFromInputStream(inputStream);
        this.categoryFetcher = categoryFetcher;
        this.versionsFetcher = versionsFetcher;
        this.docVersionRenderer = docVersionRenderer;
        this.apiVersionRenderer = apiVersionRenderer;
    }

    @Override
    public String renderAsHtml() {
        List<String> versions = versionsFetcher.versions();
        String latestVersion = versions.get(0);
        List<String> oldVersions = versions.subList(1, versions.size());
        return template.replaceAll("@version@", versions.get(0))
                .replaceAll("@doc-options@", oldVersions.stream()
                        .map(version -> docVersionRenderer.renderAsHtml(version)).collect(Collectors.joining("\n")))
                .replaceAll("@api-options@", oldVersions.stream()
                        .map(version -> apiVersionRenderer.renderAsHtml(version)).collect(Collectors.joining("\n")))
                .replaceAll("@analytics@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.ANALYTICS).orElseThrow()))
                .replaceAll("@api@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.API).orElseThrow()))
                .replaceAll("@build@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.BUILD).orElseThrow()))
                .replaceAll("@cloud@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.CLOUD).orElseThrow()))
                .replaceAll("@data-access@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.DATA_ACCESS).orElseThrow()))
                .replaceAll("@database-migration@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.DATABASE_MIGRATION).orElseThrow()))
                .replaceAll("@languages@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.LANGUAGES).orElseThrow()))
                .replaceAll("@messaging@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.MESSAGING).orElseThrow()))
                .replaceAll("@misc@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.MISC).orElseThrow()))
                .replaceAll("@reactive@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.REACTIVE).orElseThrow()))
                .replaceAll("@views@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.VIEWS).orElseThrow()));
    }
}
