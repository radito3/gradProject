package org.elsys.apm.repository;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class RepositoryURLBuilderTest {

    @Test
    public void getRepoRootUrl() {
        RepositoryURLBuilder factory = new RepositoryURLBuilder();
        assertNotNull(factory.repoRoot());
    }

    @Test
    public void getRepoRootUrlIsBuilder() {
        RepositoryURLBuilder factory = new RepositoryURLBuilder();
        assertTrue(factory.repoRoot() instanceof RepositoryURLBuilder);
    }

    @Test
    public void getRepoRootUrlBuilt() throws MalformedURLException {
        RepositoryURLBuilder factory = new RepositoryURLBuilder();
        assertTrue(factory.repoRoot().build() instanceof URL);
    }

    @Test
    public void getRepoRootUrlContent() throws MalformedURLException {
        RepositoryURLBuilder factory = new RepositoryURLBuilder();
        URL url = factory.repoRoot().build();
        assertEquals("https", url.getProtocol());
        assertEquals("raw.githubusercontent.com", url.getHost());
        assertEquals("/radito3/gradProject/github_dwnl/packages", url.getPath());
    }

    @Test
    public void getDescriptorUrlContent() throws MalformedURLException {
        RepositoryURLBuilder factory = new RepositoryURLBuilder();
        assertEquals(new URL(RepositoryURLBuilder.REPO_URL.concat("/descriptor.json")),
                factory.repoRoot().repoDescriptor().build());
    }

    @Test
    public void getFileUrlTest() throws MalformedURLException {
        RepositoryURLBuilder factory = new RepositoryURLBuilder();
        assertEquals(new URL(RepositoryURLBuilder.REPO_URL.concat("/my-file")),
                factory.repoRoot().target("my-file").build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFileUrlFailsTest() {
        RepositoryURLBuilder factory = new RepositoryURLBuilder();
        assertNotNull(factory.target("myFi\\|\\/!@#$%^&*()_le"));
    }

    @Test
    public void getFileUrlWrongExceptionTest() {
        RepositoryURLBuilder factory = new RepositoryURLBuilder();
        try {
            assertNotNull(factory.target("myFi\\|\\/!@#$%^&*()_le"));
            fail("An exception should have been thrown") ;
        } catch (IllegalArgumentException e) {
            assertEquals("Illegal characters in file name", e.getMessage());
        }
    }

    @Test(expected = NullPointerException.class)
    public void getNullFileTest() {
        RepositoryURLBuilder factory = new RepositoryURLBuilder();
        assertNotNull(factory.target(null));
    }

    @Test
    public void getNullFileWrongExceptionTest() {
        RepositoryURLBuilder factory = new RepositoryURLBuilder();
        try {
            assertNotNull(factory.target(null));
            fail("An exception should have been thrown") ;
        } catch (NullPointerException e) {
            assertEquals("File name is null", e.getMessage());
        }
    }

}
