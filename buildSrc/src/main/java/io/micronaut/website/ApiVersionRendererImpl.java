package io.micronaut.website;

import java.io.IOException;

public class ApiVersionRendererImpl extends VersionRendererImpl implements ApiVersionRenderer {
    public ApiVersionRendererImpl() throws IOException {
        super("api-option.html");
    }
}
