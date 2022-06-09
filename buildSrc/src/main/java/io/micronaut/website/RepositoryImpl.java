package io.micronaut.website;

public class RepositoryImpl implements Repository {
    private final String slug;

    private final String title;

    private final String description;

    private final boolean snapshot;

    public RepositoryImpl(String slug, String title, String description, boolean snapshot) {
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.snapshot = snapshot;
    }

    @Override
    public String getSlug() {
        return slug;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isSnapshot() {
        return snapshot;
    }
}
