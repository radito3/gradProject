package org.elsys.apm.repository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class RepositoryURLBuilder {

    public static final String REPO_URL = System.getenv("github");

    private StringBuilder urlString;

    public RepositoryURLBuilder() {
        urlString = new StringBuilder();
    }

    public RepositoryURLBuilder repoRoot() {
        urlString.append(REPO_URL);
        return this;
    }

    public RepositoryURLBuilder repoDescriptor() {
        urlString.append("/descriptor.json");
        return this;
    }

    public RepositoryURLBuilder target(String file) throws IllegalArgumentException, NullPointerException {
        if (file == null) {
            throw new NullPointerException("File name is null");
        }

        Pattern pattern = Pattern.compile("[^-_.a-zA-Z0-9]");
        if (pattern.matcher(file).find()) {
            throw new IllegalArgumentException("Illegal characters in file name");
        }

        urlString.append('/').append(file);
        return this;
    }

    public URL build() throws MalformedURLException {
        if (urlString.indexOf("://") == -1) {
            throw new MalformedURLException("Missing protocol");
        }
        return new URL(urlString.toString());
    }

}
