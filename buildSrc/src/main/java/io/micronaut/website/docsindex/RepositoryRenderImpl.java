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

import org.gradle.api.logging.Logger;

import java.io.IOException;

public class RepositoryRenderImpl implements RepositoryRenderer {

    private final String template;
    private final Logger logger;
    private final VersionService versionService;

    public RepositoryRenderImpl(Logger logger, VersionService versionService) throws IOException {
        this.logger = logger;
        this.versionService = versionService;
        try(var stream = this.getClass().getClassLoader().getResourceAsStream("repository.html")) {
            this.template = new String(stream.readAllBytes());
        }
    }

    @Override
    public String renderAsHtml(Repository repository) {
        String version = repository.snapshot() ? "snapshot" : versionService.getReleaseVersion(repository);
        if (version == null) {
            logger.info("Skipping {} as no version found", repository.slug());
            return "";
        }
        if (repository.standardDocs()) {
            version += "/guide";
        }
        String url = repository.url() != null ?
                repository.url() :
                "https://micronaut-projects.github.io/@slug@/@version@"
                        .replaceAll("@slug@", repository.slug())
                        .replaceAll("@version@", version);
        return template.replaceAll("@title@", repository.title())
                .replaceAll("@url@", url)
                .replaceAll("@description@", repository.description());
    }
}
