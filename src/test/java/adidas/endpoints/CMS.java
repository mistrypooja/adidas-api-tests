package test.java.adidas.endpoints;

import com.jayway.jsonpath.JsonPath;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import net.minidev.json.JSONArray;
import org.json.simple.parser.ParseException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import test.java.adidas.config.Config;

import java.util.LinkedHashMap;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;


public class CMS {

    @BeforeClass
    private void setBasePath() {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = Config.BASE_URL;
    }

    private String PATH = "/pages/landing?path=/";

    @Test
    public void test_isAnalyticsNamePresent() throws ParseException {
        String componentPath = "component_presentations[%d]";
        int component_size = 0;
        int item_size = 0;
        int calls_to_action_size = 0;

        ExtractableResponse response = sendGetRequest( PATH );

        component_size = response.jsonPath().getList( "component_presentations" ).size();
        item_size = 0;
        calls_to_action_size = 0;

        for (int i = 0; i < component_size; i++) {
            //Checking if there is the key "analytics_name" anywhere inside items array in content
            assertThat( "Looks like analytics_name key was not present in the path: " + String.format(
                    componentPath, i ), JsonPath.parse( response.asString() ).read( String.format( "" +
                    ".component_presentations[%d]..content_fields" +
                    ".items.[?(@..analytics_name)]", i ) ), not( empty() ) );

            //If we want to be more specific in terms of json path and check every component has items and every item
            // has "analytics_name" at the following oath calls_to_action[0].supporting_fields
            // .supporting_fields.standard_metadata, Then it can be done using the following code.
            item_size = response.jsonPath().getList( String.format( "component_presentations[%d].component" +
                    ".content_fields.items", i ) ).size();
            for (int j = 0; j < item_size; j++) {
                calls_to_action_size = response.jsonPath().getList( String.format( "component_presentations[%d].component" +
                        ".content_fields.items[%d].calls_to_action", i, j ) ).size();
                for (int k = 0; k < calls_to_action_size; k++) {
                    assertThat( "analytics_name was not present in the path:" + String.format(
                            "component_presentations[0].component.content_fields.items[0].calls_to_action[0]" +
                                    ".supporting_fields.supporting_fields.standard_metadata", i, j, k ),
                            response.jsonPath().get(
                                    String.format(
                                            "component_presentations[%d].component." +
                                                    "content_fields.items[%d].calls_to_action[%d].supporting_fields.supporting_fields" +
                                                    ".standard_metadata", i, j, k ) ),
                            hasJsonPath( "analytics_name" ) );

                }

            }

        }

    }

    @Test
    public void test_checkImageUrls() throws ParseException {

        ExtractableResponse response = sendGetRequest( PATH );
        JSONArray desktopImages = (JSONArray) JsonPath.parse( response.asString() ).read( "$" +
                ".component_presentations..background_media" +
                ".desktop_image" );
        JSONArray tabletImages = (JSONArray) JsonPath.parse( response.asString() ).read( "$" +
                ".component_presentations..background_media" +
                ".tablet_image" );
        JSONArray mobileImages = (JSONArray) JsonPath.parse( response.asString() ).read( "$" +
                ".component_presentations..background_media" +
                ".mobile_image" );

        getUrlsFromArrayAndSendGetRequests( desktopImages );
        getUrlsFromArrayAndSendGetRequests( tabletImages );
        getUrlsFromArrayAndSendGetRequests( mobileImages );

    }

    private ExtractableResponse sendGetRequest(String url) {


        return given().
                expect().
                statusCode( 200 ).
                when().
                get( url ).
                then().
                time( lessThan(1000L) ).
                 and().
                        extract();

    }

    private void getUrlsFromArrayAndSendGetRequests(JSONArray getRequestArray) {

        for (Object object : getRequestArray) {
            LinkedHashMap<String, String> mappedObject = (LinkedHashMap<String, String>) object;
            sendGetRequest( mappedObject.get( "url" ) );

        }
    }


}
