package org.elsys.apm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ApplicationUploaderTest {

    @Test
    public void correctLanguageTest() {
        ApplicationUploader.checkLanguage("java");
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsupportedLanguageTest() {
        ApplicationUploader.checkLanguage("Unsupported language");
    }

    @Test
    public void correctExceptionMessageOnLanguageTest() {
        try {
            ApplicationUploader.checkLanguage("Unsupported language");
            fail("An exception should have been thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Unsupported language", e.getMessage());
        }
    }

}
