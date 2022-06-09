package io.micronaut.website;

import java.io.IOException;
import java.io.InputStream;

public abstract class VersionRendererImpl {
    private final String template;
    protected VersionRendererImpl(String name) throws IOException {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("api-option.html");
        this.template = Utils.readFromInputStream(inputStream);
    }

    public String renderAsHtml(String version) {
        return template.replaceAll("@version@", version);
    }
}
