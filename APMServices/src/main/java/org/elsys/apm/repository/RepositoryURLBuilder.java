package org.elsys.apm.repository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Class for constructing the URL object for files.
 * Implements the Builder design pattern.
 *
 * @author Rangel Ivanov
 */
public class RepositoryURLBuilder {

    private final String REPO_URL = System.getenv("github");

    private StringBuilder urlString;

    public RepositoryURLBuilder() {
        urlString = new StringBuilder();
    }

    /**
     * Add the repository root url
     *
     * @return The current instance
     */
    public RepositoryURLBuilder repoRoot() {
        urlString.append(REPO_URL);
        return this;
    }

    /**
     * Append the descriptor file to the url
     *
     * @return The current instance
     */
    public RepositoryURLBuilder repoDescriptor() {
        urlString.append("/descriptor.json");
        return this;
    }

    /**
     * Append a file to the url
     * Checks the file name for illegal characters
     *
     * @param file The file name
     * @return The current instance
     * @throws IllegalArgumentException If the file name contains illegal characters
     */
    public RepositoryURLBuilder target(String file) throws IllegalArgumentException {
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

    /**
     * Build the {@link URL} object from the constructed url
     *
     * @return The URL object
     * @throws MalformedURLException If the url string is invalid
     */
    public URL build() throws MalformedURLException {
        if (urlString.indexOf("://") == -1) {
            throw new MalformedURLException("Missing protocol");
        }
        return new URL(urlString.toString());
    }

}
