package tv.superawesome.lib.sanetwork.request;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by gabriel.coman on 30/04/2018.
 */

public class TestSANetworkUtils {

    private SANetworkUtils utils;

    @Before
    public void setUp () {
        utils = new SANetworkUtils();
    }

    @After
    public void tearDown () {
        utils = null;
    }

    @Test
    public void test_SANetworkUtils_IsJSONEmpty_WithNonEmptyJson () throws Exception {
        // given
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("some", "value");

        // when
        boolean isEmpty = utils.isJSONEmpty(jsonObject);

        // then
        Assert.assertFalse(isEmpty);
    }

    @Test
    public void test_SANetworkUtils_IsJSONEmpty_WithEmptyJson () {
        // given
        JSONObject jsonObject = new JSONObject();

        // when
        boolean isEmpty = utils.isJSONEmpty(jsonObject);

        // then
        Assert.assertTrue(isEmpty);
    }

    @Test
    public void test_SANetworkUtils_IsJSONEmpty_WithNullJson () {
        // given
        JSONObject jsonObject = null;

        // when
        boolean isEmpty = utils.isJSONEmpty(jsonObject);

        // then
        Assert.assertTrue(isEmpty);
    }

    @Test
    public void test_SANetworkUtils_FormGetQueryFromDict_WithNullDict () {
        // given
        JSONObject jsonObject = null;

        // when
        String query = utils.formGetQueryFromDict(jsonObject);

        // then
        Assert.assertEquals("", query);
    }

    @Test
    public void test_SANetworkUtils_FormGetQueryFromDict_WithEmptyDict () {
        // given
        JSONObject jsonObject = new JSONObject();

        // when
        String query = utils.formGetQueryFromDict(jsonObject);

        // then
        Assert.assertEquals("", query);
    }

    @Test
    public void test_SANetworkUtils_FormGetQueryFromDict_WithValidDict () throws Exception {
        // given
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("some", "value");
        jsonObject.put("other", "23");

        // when
        String query = utils.formGetQueryFromDict(jsonObject);

        // then
        Assert.assertEquals("some=value&other=23", query);
    }

}
