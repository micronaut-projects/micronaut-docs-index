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
package io.micronaut.website.docsindex;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.gradle.api.GradleException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionServiceImpl implements VersionService {

    private final String releaseVersion;
    private final VersionType versionType;
    private final Map<String, String> versions = new HashMap<>();

    public VersionServiceImpl(String version) {
        this.releaseVersion = version;
        versionType = versionType(releaseVersion);
        String url = switch (versionType) {
            case PLATFORM ->
                    "https://repo1.maven.org/maven2/io/micronaut/platform/micronaut-platform/%s/micronaut-platform-%s.toml".formatted(releaseVersion, releaseVersion);
            case CATALOG ->
                    "https://repo1.maven.org/maven2/io/micronaut/micronaut-bom/%s/micronaut-bom-%s.toml".formatted(releaseVersion, releaseVersion);
            case POM ->
                    "https://repo1.maven.org/maven2/io/micronaut/micronaut-bom/%s/micronaut-bom-%s.pom".formatted(releaseVersion, releaseVersion);
        };
        if (versionType == VersionType.POM) {
            readPom(url);
        } else {
            readToml(url);
        }
        // Patch for badly named things in 3.x
        patchVersions(Map.of(
                "micronaut-xml", "micronaut-jackson-xml",
                "micronaut-discovery", "micronaut-discovery-client",
                "micronaut-oraclecloud", "micronaut-oracle-cloud",
                "micronaut-mongo", "micronaut-mongodb",
                "micronaut-problem", "micronaut-problem-json"
        ));
    }

    private VersionType versionType(String releaseVersion) {
        int major = Integer.parseInt(releaseVersion.split("\\.")[0]);
        if (major <= 2) {
            return VersionType.POM;
        } else if (major < 4) {
            return VersionType.CATALOG;
        }
        return VersionType.PLATFORM;
    }

    private void readPom(String pom) {
        XmlMapper mapper = new XmlMapper();
        try (InputStream s = new URL(pom).openStream()) {
            var properties = (Map<String, String>) mapper.readValue(s, Map.class).get("properties");
            properties.entrySet()
                    .stream()
                    .map(e -> transform(e.getKey(), e.getValue()))
                    .filter(Objects::nonNull)
                    .forEach(e -> versions.put(e.getKey(), e.getValue()));
        } catch (IOException e) {
            throw new GradleException("Error parsing POM", e);
        }
    }

    private Map.Entry<String, String> transform(String key, String value) {
        Matcher matcher = Pattern.compile("micronaut\\.(\\S+?)\\.version").matcher(key);
        if (matcher.find()) {
            return Map.entry("micronaut-" + matcher.group(1).replaceAll("\\.", "-"), value);
        }
        return null;
    }

    private void readToml(String toml) {
        try (InputStream s = new URL(toml).openStream()) {
            versions.clear();
            Matcher matcher = Pattern.compile("(?m)^(micronaut-[^ =]+)[\\s]+=[\\s]+\\\"(.+)\\\"$").matcher(new String(s.readAllBytes()));
            while (matcher.find()) {
                versions.put(matcher.group(1), matcher.group(2));
            }
        } catch (IOException e) {
            throw new GradleException("Error parsing TOML", e);
        }
    }

    private void patchVersions(Map<String, String> patches) {
        patches.forEach((k, v) -> {
            if (versions.containsKey(k)) {
                versions.put(v, versions.get(k));
            }
        });
    }

    @Override
    public String getReleaseVersion(Repository module) {
        // micronaut starter isn't in the platform, and has the same version as the release
        if ("micronaut-starter".equals(module.slug())) {
            return releaseVersion;
        }
        // Pre platform versions are all the same
        if ("micronaut-core".equals(module.slug()) && versionType != VersionType.PLATFORM) {
            return releaseVersion;
        }
        String version = versions.get(module.slug());

        // TODO: Plugins are non-standard and not in the platform catalog
        if (version == null && !module.standardDocs()) {
            return "latest";
        }
        return version;
    }

    private enum VersionType {
        PLATFORM, CATALOG, POM
    }
}
