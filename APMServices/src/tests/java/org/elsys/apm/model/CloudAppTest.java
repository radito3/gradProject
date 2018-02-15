package org.elsys.apm.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
        new CloudApp(obj, "name");
    }

    @Test
    public void filedsTest() {
        CloudApp app = new CloudApp(obj, "name");

        assertEquals("lang", app.getLanguage());
        assertFalse(app.hasDependencies());
        assertEquals("fileName", app.getFileName());
        assertEquals("name", app.getName());
        assertEquals("1.0.0", app.getVersion());
        assertEquals(new JSONArray(), app.getDependencies());
    }
}
