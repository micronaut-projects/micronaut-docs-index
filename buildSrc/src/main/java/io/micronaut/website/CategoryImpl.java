package io.micronaut.website;

import java.util.List;

public class CategoryImpl implements Category {
    private final String title;

    private final String image;

    private final List<Repository> repositories;

    public CategoryImpl(String title, String image, List<Repository> repositories) {
        this.title = title;
        this.image = image;
        this.repositories = repositories;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getImage() {
        return image;
    }

    @Override
    public List<Repository> getRepositories() {
        return repositories;
    }
}
