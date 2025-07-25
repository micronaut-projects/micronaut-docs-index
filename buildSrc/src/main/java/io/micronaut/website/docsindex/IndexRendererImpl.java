/*
 * Copyright 2017-2024 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.website.docsindex;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class IndexRendererImpl implements IndexRenderer {

    private final String template;
    private final CategoryRenderer categoryRenderer;
    private final CategoryFetcher categoryFetcher;
    private final VersionService versionService;
    private final String platformVersion;
    private final List<String> allVersions;

    public IndexRendererImpl(
            CategoryRenderer categoryRenderer,
            CategoryFetcher categoryFetcher,
            VersionService versionService,
            String platformVersion,
            List<String> allVersions
    ) throws IOException {
        this.categoryRenderer = categoryRenderer;
        this.categoryFetcher = categoryFetcher;
        this.versionService = versionService;
        this.platformVersion = platformVersion;
        this.allVersions = allVersions;
        try (var stream = this.getClass().getClassLoader().getResourceAsStream("index.html")) {
            this.template = new String(stream.readAllBytes());
        }
    }

    private String versionToOption(String version) {
        String selected = platformVersion != null && platformVersion.equals(version) ? " selected" : "";
        return """
                <option%s value="%s.html">%s</option>""".formatted(selected, version, version);
    }

    private static String coreDomain(String version) {
        if (version.startsWith("4.0") ||
                version.startsWith("4.1") ||
                version.startsWith("4.2") ||
                version.startsWith("4.3") ||
                version.startsWith("4.4") ||
                version.startsWith("4.5") ||
                version.startsWith("4.6") ||
                version.startsWith("4.7")) {
            return "https://micronaut-projects.github.io/micronaut-docs-mn4";
        } else if (version.startsWith("3.")) {
            return "https://micronaut-projects.github.io/micronaut-docs-mn3";
        } else if (version.startsWith("2.")) {
            return "https://micronaut-projects.github.io/micronaut-docs-mn2";
        } else if (version.startsWith("1.")) {
            return "https://micronaut-projects.github.io/micronaut-docs-mn1";
        }
        return "https://docs.micronaut.io";
    }

    @Override
    public String renderAsHtml() {
        String coreVersion = versionService.getReleaseVersion(new Repository("micronaut-core", null, null, false, true));
        return template
                .replaceAll("@coreDomain@", coreDomain(coreVersion))
                .replaceAll("@version@", coreVersion)
                .replaceAll("@versionOptions@", allVersions.stream().map(this::versionToOption).collect(Collectors.joining()))
                .replaceAll("@analytics@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.ANALYTICS).orElseThrow()))
                .replaceAll("@api@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.API).orElseThrow()))
                .replaceAll("@build@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.BUILD).orElseThrow()))
                .replaceAll("@cloud@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.CLOUD).orElseThrow()))
                .replaceAll("@configuration@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.CONFIGURATION).orElseThrow()))
                .replaceAll("@data-access@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.DATA_ACCESS).orElseThrow()))
                .replaceAll("@database-migration@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.DATABASE_MIGRATION).orElseThrow()))
                .replaceAll("@errors@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.ERRORS).orElseThrow()))
                .replaceAll("@languages@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.LANGUAGES).orElseThrow()))
                .replaceAll("@messaging@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.MESSAGING).orElseThrow()))
                .replaceAll("@misc@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.MISC).orElseThrow()))
                .replaceAll("@reactive@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.REACTIVE).orElseThrow()))
                .replaceAll("@views@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.VIEWS).orElseThrow()))
                .replaceAll("@dev-and-test@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.DEV_AND_TEST).orElseThrow()))
                .replaceAll("@most-popular@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.MOST_POPULAR).orElseThrow()))
                .replaceAll("@validation@", categoryRenderer.renderAsHtml(categoryFetcher.fetch(Type.VALIDATION).orElseThrow()));
    }
}
