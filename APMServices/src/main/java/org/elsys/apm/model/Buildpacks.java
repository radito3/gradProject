package org.elsys.apm.model;

public enum Buildpacks {

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

    PHP("https://github.com/cloudfoundry/php-buildpack.git"),
    
    BINARY("https://github.com/cloudfoundry/binary-buildpack.git");

    private final String url;

    Buildpacks(String url) {
        this.url = url;
    }

    static public String getBuildpackUrl(String appLang) {
        switch (appLang) {
            case "java": return JAVA.url;
            case "python": return PYTHON.url;
            case "ruby": return RUBY.url;
            case "nodejs": return NODEJS.url;
            case "go": return GO.url;
            case "php": return PHP.url;
            case "hwc": return HWC.url;
            case "dotnet": return DOTNET.url;
            case "binary": return BINARY.url;
            default: return "Unsupported language";
        }
    }
}
