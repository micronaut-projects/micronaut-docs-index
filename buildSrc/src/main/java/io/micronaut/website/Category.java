package io.micronaut.website;

import java.util.List;

public interface Category {
    String getTitle();
    List<Repository> getRepositories();
    String getImage();
}
