/*
 * Copyright 2017-2022 original authors
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
import java.io.InputStream;

public class RepositoryRenderImpl implements RepositoryRenderer {
    private String template;
    public RepositoryRenderImpl() throws IOException {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("repository.html");
        this.template = Utils.readFromInputStream(inputStream);
        inputStream.close();
    }

    @Override
    public String renderAsHtml(Repository repository) {
        return template.replaceAll("@title@", repository.getTitle())
                .replaceAll("@slug@", repository.getSlug())
                .replaceAll("@description@", repository.getDescription())
                .replaceAll("@version@", repository.isSnapshot()  ? "snapshot" : "latest");
    }
}
