package org.elsys.apm;

/**
 * For future implementation of applications in languages other than Java
 *
 * @author Rangel Ivanov
 */
enum Buildpacks {

    JAVA("https://github.com/cloudfoundry/java-buildpack.git"),

    CPP(""),

    RUBY(""),

    PYTHON(""),

    C(""),

    GO(""),

    CSHARP("");

    private final String url;

    Buildpacks(String url) {
        this.url = url;
    }

    String getUrl() {
        return url;
    }
}
