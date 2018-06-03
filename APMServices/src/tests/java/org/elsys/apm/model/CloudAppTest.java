package org.elsys.apm.model;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CloudAppTest {

    private JSONObject obj;

    @Before
    public void setUp() throws ParseException {
        String json = "{" +
                "\"language\": \"lang\"," +
                "\"dependencies\": []," +
                "\"file\": \"fileName\"," +
                "\"pkgVersion\": \"1.0.0\"" +
                "}";
        obj = (JSONObject) new JSONParser().parse(json);
    }

    @Test
    public void creationTest() {
        CloudApp app = new CloudApp(obj, "name");
        assertNotNull(app);
    }

    @Test
    public void simpleFieldsTest() {
        CloudApp app = new CloudApp(obj, "name");

        assertEquals("lang", app.getLanguage());
        assertEquals("fileName", app.getFileName());
        assertEquals("name", app.getName());
        assertEquals("1.0.0", app.getVersion());
    }

    @Test
    public void noDependenciesTest() {
        CloudApp app = new CloudApp(obj, "name");

        assertFalse(app.hasDependencies());
        assertNotNull(app.getDependencies());
    }

    private JSONObject objWithDependencies() throws ParseException {
        String json = "{" +
                "\"language\": \"lang\"," +
                "\"dependencies\": [\"one\", \"two\"]," +
                "\"file\": \"fileName\"," +
                "\"pkgVersion\": \"1.0.0\"" +
                "}";
        return (JSONObject) new JSONParser().parse(json);
    }

    @Test
    public void dependenciesTest() throws ParseException {
        CloudApp app = new CloudApp(objWithDependencies(), "name");

        assertTrue(app.hasDependencies());
        assertEquals("one", app.getDependencies().get(0));
        assertEquals("two", app.getDependencies().get(1));
    }
}
