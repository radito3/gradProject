package org.elsys.apm;

/**
 * For future implementation of applications in languages other than Java
 */
enum Buildpacks {

    JAVA("https://github.com/cloudfoundry/java-buildpack.git"),

    RUBY("https://github.com/cloudfoundry/ruby-buildpack.git"),

    PYTHON("https://github.com/cloudfoundry/python-buildpack.git"),

    NODEJS("https://github.com/cloudfoundry/nodejs-buildpack.git"),

    GO("https://github.com/cloudfoundry/go-buildpack"),

    /**
     * Hosted Web Core applications
     * Windows applications
     */
    HWC("https://github.com/cloudfoundry/hwc-buildpack"),

    PHP("https://github.com/cloudfoundry/php-buildpack.git");

    private final String url;

    Buildpacks(String url) {
        this.url = url;
    }

    String getUrl() {
        return url;
    }
}
