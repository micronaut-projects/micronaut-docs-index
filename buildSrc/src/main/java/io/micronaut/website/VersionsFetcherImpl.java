package io.micronaut.website;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VersionsFetcherImpl implements VersionsFetcher {
    private final List<String> versions;

    public VersionsFetcherImpl(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }

    public VersionsFetcherImpl(InputStream inputStream) {
        Yaml yaml = new Yaml();
        Map<String, Object> obj = yaml.load(inputStream);
        Object releasesObj = obj.get("releases");
        if (releasesObj instanceof List) {
            List releasesList = (List) releasesObj;
            versions = (List<String>) releasesList.stream().map(Object::toString).collect(Collectors.toList());
        } else {
            versions = Collections.emptyList();
        }
    }
    @Override
    public List<String> versions() {
        return versions;
    }
}
