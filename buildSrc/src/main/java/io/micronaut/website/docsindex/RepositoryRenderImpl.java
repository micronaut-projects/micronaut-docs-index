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

public class RepositoryRenderImpl implements RepositoryRenderer {

    private final String template;
    private final VersionService versionService;

    public RepositoryRenderImpl(VersionService versionService) throws IOException {
        this.versionService = versionService;
        try(var stream = this.getClass().getClassLoader().getResourceAsStream("repository.html")) {
            this.template = new String(stream.readAllBytes());
        }
    }

    @Override
    public String renderAsHtml(Repository repository) {
        String version = repository.snapshot() ? "snapshot" : versionService.getReleaseVersion(repository);
        if (version == null) {
            System.out.printf("Skipping %s as no version found%n", repository.slug());
            return "";
        }
        if (repository.standardDocs()) {
            version += "/guide";
        }
        return template.replaceAll("@title@", repository.title())
                .replaceAll("@slug@", repository.slug())
                .replaceAll("@description@", repository.description())
                .replaceAll("@version@", version);
    }
}
