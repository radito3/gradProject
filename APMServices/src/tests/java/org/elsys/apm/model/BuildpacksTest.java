package org.elsys.apm.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BuildpacksTest {

    private List<String> languages = new ArrayList<>();

    @Before
    public void setUp() {
        languages.add("java");
        languages.add("go");
        languages.add("ruby");
        languages.add("python");
        languages.add("hwc");
        languages.add("dotnet");
        languages.add("php");
        languages.add("php");
        languages.add("binary");
        languages.add("staticfile");
    }

    @Test
    public void wrongLanguageTest() {
        assertEquals("Unsupported language", Buildpacks.getBuildpackUrl("asdf"));
    }

    @Test
    public void urlTest() {
        languages.forEach(lang -> assertNotNull(Buildpacks.getBuildpackUrl(lang)));
    }
}
