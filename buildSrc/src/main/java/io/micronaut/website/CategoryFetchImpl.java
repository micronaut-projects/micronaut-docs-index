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
package io.micronaut.website;

import org.gradle.api.tasks.Input;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CategoryFetchImpl implements CategoryFetcher {

    private static final String KEY_TITLE = "title";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_REPOSITORIES = "repositories";
    private static final String KEY_CATEGORIES = "categories";
    private static final String KEY_SLUG = "slug";
    private static final String KEY_DESCRIPTION = "description";

    Map<Type, Category> typeToCategory = new HashMap<>();

    public CategoryFetchImpl(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }

    public CategoryFetchImpl(InputStream inputStream) {
        Yaml yaml = new Yaml();
        Map<String, Object> obj = yaml.load(inputStream);
        for (Type type : Type.values()) {
            Optional<Category> optionalCategory = byType(obj, type);
            optionalCategory.ifPresent(category -> typeToCategory.put(type, category));
        }
    }

    @Override
    public Optional<Category> fetch(Type type) {
        return Optional.ofNullable(typeToCategory.get(type));
    }

    public Optional<Category> byType(Map<String, Object> obj, Type type) {
        Object categoriesObj = obj.get(KEY_CATEGORIES);
        if (categoriesObj instanceof Map) {
            Map categoriesMap = (Map) categoriesObj;
            Object categoryObj = categoriesMap.get(type.toString());
            if (categoryObj instanceof Map) {
                Map categoryMap = (Map) categoryObj;
                List<Repository> result = new ArrayList<>();
                Object repositoriesObj = categoryMap.get(KEY_REPOSITORIES);
                if (repositoriesObj instanceof Map) {
                    for (Object repositoryObj : ((Map) repositoriesObj).values()) {
                        if (repositoryObj instanceof Map) {
                            Map repositoryMap = (Map) repositoryObj;
                            result.add(new RepositoryImpl(
                                    parseString(repositoryMap, KEY_SLUG),
                                    parseString(repositoryMap, KEY_TITLE),
                                    parseString(repositoryMap, KEY_DESCRIPTION),
                                    parseBoolean(repositoryMap, "snapshot", false)));
                        }
                    }
                }
                return Optional.of(new CategoryImpl(
                        parseString(categoryMap, KEY_TITLE),
                        parseString(categoryMap, KEY_IMAGE),
                        result
                ));
            }
        }
        return Optional.empty();
    }

    private static String parseString(Map m, String k) {
        Object obj = m.get(k);
        if (obj != null) {
            return obj.toString();
        }
        return null;
    }

    private static boolean parseBoolean(Map m, String k, boolean defaultValue) {
        Object obj = m.get(k);
        if (obj != null && obj instanceof Boolean) {
            return (boolean) obj;
        }
        return defaultValue;
    }
}
