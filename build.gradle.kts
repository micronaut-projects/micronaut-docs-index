apply<io.micronaut.website.gradle.MicronautWebsiteDocsIndexPlugin>()

tasks.register<Copy>("copyAssets") {
    from(layout.projectDirectory.dir("assets"))
    into(layout.buildDirectory.dir("assets"))
}
tasks.findByName("build")?.dependsOn("copyAssets")