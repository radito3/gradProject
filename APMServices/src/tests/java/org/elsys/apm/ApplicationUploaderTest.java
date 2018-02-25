package org.elsys.apm;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.MissingResourceException;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class ApplicationUploaderTest {

    @Mock
    private CloudClient client;

    @Before
    public void setUp() {

    }

    @Test
    public void correctLanguageTest() {

    }

    @Test
    public void unsupportedLanguageTest() {

    }

    @Test(expected = ApplicationUploader.ExistingAppException.class)
    public void existingAppTest() {

    }

    @Test
    public void installWithNoDependenciesTest() {

    }

    @Test
    public void installWithDependenciesTest() {

    }

    @Test
    public void checkInstalledDependenciesTest() {

    }

    @Test(expected = MissingResourceException.class)
    public void chceckMissingDependenciesTest() {

    }
}
