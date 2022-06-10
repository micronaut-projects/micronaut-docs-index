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
package io.micronaut.website.gradle;

import io.micronaut.website.docsindex.ApiVersionRendererImpl;
import io.micronaut.website.docsindex.CategoryFetchImpl;
import io.micronaut.website.docsindex.CategoryRendererImpl;
import io.micronaut.website.docsindex.DocVersionRendererImpl;
import io.micronaut.website.docsindex.IndexRenderer;
import io.micronaut.website.docsindex.IndexRendererImpl;
import io.micronaut.website.docsindex.RepositoryRenderImpl;
import io.micronaut.website.docsindex.VersionsFetcherImpl;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class RenderMicronautWebsiteDocsIndexTask extends DefaultTask {

    @InputFile
    @PathSensitive(PathSensitivity.NONE)
    public abstract RegularFileProperty getModules();

    @InputFile
    @PathSensitive(PathSensitivity.NONE)
    public abstract RegularFileProperty getReleases();

    @OutputFile
    public abstract RegularFileProperty getDestinationFile();

    @TaskAction
    void render() {
        try {
            File modulesFile = getModules().getAsFile().get();
            File releasesFile = getReleases().getAsFile().get();
            IndexRenderer indexRenderer = new IndexRendererImpl(new CategoryRendererImpl(new RepositoryRenderImpl()),
                    new CategoryFetchImpl(modulesFile),
                    new VersionsFetcherImpl(releasesFile),
                    new DocVersionRendererImpl(),
                    new ApiVersionRendererImpl());
            String html = indexRenderer.renderAsHtml();
            try (FileOutputStream fos = new FileOutputStream(getDestinationFile().getAsFile().get())) {
                fos.write(html.getBytes(StandardCharsets.UTF_8.name()));
            }
        } catch (IOException e) {
            throw new GradleException("IO Exception rendering index");
        }
    }
}
