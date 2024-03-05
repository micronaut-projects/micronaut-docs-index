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
package io.micronaut.website.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MicronautWebsiteDocsIndexPlugin implements Plugin<Project> {

    private static final Pattern VERSION_PATTERN_CLEAN = Pattern.compile("^(?:v?)(\\d+\\.\\d+\\.\\d+)$");
    private static final String MICRONAUT_RELEASE_VERSION = "MICRONAUT_RELEASE_VERSION";
    private static final String DOCUMENTATION_INDEX = "Documentation Index";

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(BasePlugin.class);
        TaskContainer tasks = project.getTasks();
        var layout = project.getLayout();

        DirectoryProperty buildDirectory = layout.getBuildDirectory();
        Directory projectDirectory = layout.getProjectDirectory();

        Provider<String> micronautReleaseVersion = project
                .getProviders()
                .environmentVariable(MICRONAUT_RELEASE_VERSION)
                .map(this::cleanupVersion);

        TaskProvider<RenderMicronautWebsiteDocsIndexTask> renderDocsIndex = tasks.register("renderDocsIndex", RenderMicronautWebsiteDocsIndexTask.class, task -> {
            task.setGroup(DOCUMENTATION_INDEX);
            task.setDescription("Render the index.html, or a specific version if " + MICRONAUT_RELEASE_VERSION + " env var is set");
            task.getModules().convention(projectDirectory.file("modules.yml"));
            task.getReleaseVersion().set(micronautReleaseVersion);
            task.getDestinationFile().convention(
                    buildDirectory.dir("generated").flatMap(dir -> micronautReleaseVersion
                            .map(v -> dir.file(v + ".html"))
                            .orElse(dir.file("index.html"))
                    )
            );
        });
        var copyAssets = tasks.register("copyAssets", Copy.class, task -> {
            task.setGroup(DOCUMENTATION_INDEX);
            task.setDescription("Copy assets and the generated html to the build/dist directory");
            task.from(layout.getProjectDirectory().dir("assets"), copy -> copy.into("assets"));
            task.from(layout.getBuildDirectory().dir("generated"));
            task.into(layout.getBuildDirectory().dir("dist"));
            task.mustRunAfter(renderDocsIndex);
        });
        tasks.register("renderReleasesDocsIndex", RenderMicronautWebsiteReleasesDocsIndexTask.class, task -> {
            task.setGroup(DOCUMENTATION_INDEX);
            task.setDescription("Renders every release version from releases.yml and copies the result to build/dist");
            task.getModules().convention(projectDirectory.file("modules.yml"));
            task.getReleases().convention(projectDirectory.file("releases.yml"));
            task.getDestinationDirectory().convention(buildDirectory.dir("generated"));
            task.finalizedBy(copyAssets);
        });
        tasks.findByName("build").dependsOn(renderDocsIndex, copyAssets);
    }

    private String cleanupVersion(String version) {
        Matcher matcher = VERSION_PATTERN_CLEAN.matcher(version);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid release version: '" + version + "'");
        }
        return matcher.group(1);
    }
}
