package io.micronaut.website;

import java.io.IOException;

public class DocVersionRendererImpl  extends VersionRendererImpl implements DocVersionRenderer {
    public DocVersionRendererImpl() throws IOException {
        super("doc-option.html");
    }
}
