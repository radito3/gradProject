package org.elsys.apm.repository;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class RepositoryURLBuilderTest {

    private RepositoryURLBuilder builder;

    @Before
    public void createBuilder() {
        builder = new RepositoryURLBuilder();
    }

    @Test
    public void getRepoRootUrl() {
        assertNotNull(builder.repoRoot());
    }

    @Test
    public void getDesciptor() {
        assertNotNull(builder.repoDescriptor());
    }

    @Test
    public void getRepoRootUrlIsBuilder() {
        assertTrue(builder.repoRoot() instanceof RepositoryURLBuilder);
    }

    @Test
    public void getRepoRootUrlBuilt() throws MalformedURLException {
        assertTrue(builder.repoRoot().build() instanceof URL);
    }

    @Test
    public void getRepoRootUrlContent() throws MalformedURLException {
        URL url = builder.repoRoot().build();
        assertEquals("https", url.getProtocol());
        assertEquals("raw.githubusercontent.com", url.getHost());
        assertEquals("/radito3/gradProject/github_dwnl/packages", url.getPath());
    }

    @Test
    public void getDescriptorUrlContent() throws IOException {
        URL actual = builder.repoRoot().repoDescriptor().build();

        RepositoryURLBuilder builder1 = new RepositoryURLBuilder();
        URL expected = new URL(builder1.repoRoot().repoDescriptor().build(), "");

        assertEquals(expected, actual);
    }

    @Test
    public void getFileUrlTest() throws IOException {
        URL actual = builder.repoRoot().target("my-file_").build();

        RepositoryURLBuilder builder1 = new RepositoryURLBuilder();
        URL expected = new URL(builder1.repoRoot().target("my-file_").build(), "");

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFileUrlFailsTest() {
        builder.target("myFi\\|\\/!@#$%^&*()_le");
    }

    @Test
    public void getFileUrlWrongExceptionTest() {
        try {
            builder.target("myFi\\|\\/!@#$%^&*()_le");
            fail("An exception should have been thrown") ;
        } catch (IllegalArgumentException e) {
            assertEquals("Illegal characters in file name", e.getMessage());
        }
    }

    @Test(expected = NullPointerException.class)
    public void getNullFileTest() {
        assertNotNull(builder.target(null));
    }

    @Test
    public void getNullFileWrongExceptionTest() {
        try {
            builder.target(null);
            fail("An exception should have been thrown") ;
        } catch (NullPointerException e) {
            assertEquals("File name is null", e.getMessage());
        }
    }

}
