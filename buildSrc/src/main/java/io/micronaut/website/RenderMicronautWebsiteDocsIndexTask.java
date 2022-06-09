package io.micronaut.website;

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
