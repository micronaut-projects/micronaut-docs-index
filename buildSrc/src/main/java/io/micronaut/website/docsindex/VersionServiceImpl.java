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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionServiceImpl implements VersionService {

    private final String releaseVersion;
    private final boolean prePlatform;
    private final Map<String, String> versions = new HashMap<>();

    public VersionServiceImpl(String version) {
        this.releaseVersion = version;
        prePlatform = isPrePlatform(releaseVersion);
        String toml = prePlatform
                ? "https://repo1.maven.org/maven2/io/micronaut/micronaut-bom/%s/micronaut-bom-%s.toml".formatted(releaseVersion, releaseVersion)
                : "https://repo1.maven.org/maven2/io/micronaut/platform/micronaut-platform/%s/micronaut-platform-%s.toml".formatted(releaseVersion, releaseVersion);
        readToml(toml);
    }

    private boolean isPrePlatform(String releaseVersion) {
        return Integer.parseInt(releaseVersion.split("\\.")[0]) < 4;
    }

    private void readToml(String toml) {
        try (InputStream s = new URL(toml).openStream()) {
            versions.clear();
            Matcher matcher = Pattern.compile("(?m)^(micronaut-[^ =]+)[\\s]+=[\\s]+\\\"(.+)\\\"$").matcher(new String(s.readAllBytes()));
            while (matcher.find()) {
                versions.put(matcher.group(1), matcher.group(2));
            }

            // Patch for badly named things in 3.x
            patchVersions(Map.of(
                    "micronaut-xml", "micronaut-jackson-xml",
                    "micronaut-discovery", "micronaut-discovery-client",
                    "micronaut-oraclecloud", "micronaut-oracle-cloud",
                    "micronaut-mongo", "micronaut-mongodb",
                    "micronaut-problem", "micronaut-problem-json"
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        if ("micronaut-core".equals(module.slug()) && prePlatform) {
            return releaseVersion;
        }
        String version = versions.get(module.slug());

        // TODO: Plugins are non-standard and not in the platform catalog
        if (version == null && !module.standardDocs()) {
            return "latest";
        }
        return version;
    }
}
