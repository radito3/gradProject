package org.elsys.apm;

enum Buildpacks {

    JAVA("https://github.com/cloudfoundry/java-buildpack.git"),

    RUBY("https://github.com/cloudfoundry/ruby-buildpack.git"),

    PYTHON("https://github.com/cloudfoundry/python-buildpack.git"),

    NODEJS("https://github.com/cloudfoundry/nodejs-buildpack.git"),

    GO("https://github.com/cloudfoundry/go-buildpack.git"),

    /**
     * Hosted Web Core applications
     * Windows applications
     */
    HWC("https://github.com/cloudfoundry/hwc-buildpack.git"),

    /**
     * .NET Core applications
     */
    DOTNET("https://github.com/cloudfoundry/dotnet-core-buildpack.git"),

    PHP("https://github.com/cloudfoundry/php-buildpack.git");

    private final String url;

    Buildpacks(String url) {
        this.url = url;
    }

    String getUrl() {
        return url;
    }
}
