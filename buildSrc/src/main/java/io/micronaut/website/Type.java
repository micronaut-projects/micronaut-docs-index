package io.micronaut.website;

public enum Type {
    ANALYTICS("analytics"),
    API("api"),
    BUILD("build"),
    CLOUD("cloud"),
    DATA_ACCESS("data-access"),
    DATABASE_MIGRATION("database-migration"),
    LANGUAGES("languages"),
    MESSAGING("messaging"),
    MISC("misc"),
    REACTIVE("reactive"),
    VIEWS("views");

    private final String value;

    Type(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
