package io.micronaut.website;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

public abstract class MicronautWebsiteDocsIndexPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(BasePlugin.class);
        TaskContainer tasks = project.getTasks();
        DirectoryProperty buildDirectory = project.getLayout().getBuildDirectory();
        Directory projectDirectory = project.getLayout().getProjectDirectory();
        TaskProvider<RenderMicronautWebsiteDocsIndexTask> renderDocsIndex = tasks.register("renderDocsIndex", RenderMicronautWebsiteDocsIndexTask.class, task -> {
            task.getModules().convention(projectDirectory.file("modules.yml"));
            task.getReleases().convention(projectDirectory.file("releases.yml"));
            task.getDestinationFile().convention(buildDirectory.map(dir -> dir.file("index.html")));
        });
    }
}
