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

import io.micronaut.website.docsindex.CategoryFetchImpl;
import io.micronaut.website.docsindex.CategoryRendererImpl;
import io.micronaut.website.docsindex.IndexRenderer;
import io.micronaut.website.docsindex.IndexRendererImpl;
import io.micronaut.website.docsindex.RepositoryRenderImpl;
import io.micronaut.website.docsindex.VersionService;
import io.micronaut.website.docsindex.VersionServiceImpl;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

@CacheableTask
public abstract class RenderMicronautWebsiteDocsIndexTask extends DefaultTask {

    @Input
    @Optional
    public abstract Property<String> getReleaseVersion();

    @InputFile
    @PathSensitive(PathSensitivity.NONE)
    public abstract RegularFileProperty getModules();

    @OutputFile
    public abstract RegularFileProperty getDestinationFile();

    @TaskAction
    void render() {
        try {
            File modulesFile = getModules().getAsFile().get();

            VersionService versionService = getReleaseVersion()
                    .map(v -> (VersionService) new VersionServiceImpl(v))
                    .getOrElse(VersionService.LATEST_VERSION_SERVICE);

            IndexRenderer indexRenderer = new IndexRendererImpl(
                    new CategoryRendererImpl(new RepositoryRenderImpl(versionService)),
                    new CategoryFetchImpl(modulesFile)
            );
            String html = indexRenderer.renderAsHtml();
            try (FileOutputStream fos = new FileOutputStream(getDestinationFile().getAsFile().get())) {
                fos.write(html.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new GradleException("IO Exception rendering index");
        }
    }
}
