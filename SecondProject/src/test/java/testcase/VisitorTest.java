/**
 * 
 */
package testcase;

import static javax.ws.rs.HttpMethod.DELETE;
import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.HttpMethod.HEAD;
import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.HttpMethod.PUT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.easymock.EasyMock;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.oracle.sites.visitors.api.exception.APIExceptionCodes;
import com.oracle.sites.visitors.api.responses.rest.ActivitiesResponse;
import com.oracle.sites.visitors.api.responses.rest.AggregatedProfileResponse;
import com.oracle.sites.visitors.api.responses.rest.BooleanResponse;
import com.oracle.sites.visitors.api.responses.rest.ErrorResponse;
import com.oracle.sites.visitors.api.responses.rest.IdProfilesResponse;
import com.oracle.sites.visitors.api.responses.rest.RestResponse;
import com.oracle.sites.visitors.api.utils.Requests;
import com.oracle.sites.visitors.api.visitor.Activity;
import com.oracle.sites.visitors.api.visitor.LinkedProfiles;
import com.oracle.sites.visitors.core.access.AccessService;
import com.oracle.sites.visitors.core.rest.RestService;
import com.oracle.sites.visitors.core.rest.VisitorResource;
import com.oracle.sites.visitors.core.rest.filters.access.CheckAccessFilter;
import com.oracle.sites.visitors.core.rest.filters.errors.APIExceptionMapper;
import com.oracle.sites.visitors.core.rest.resttest.ExecutionContext;
import com.oracle.sites.visitors.core.rest.resttest.Expectation;
import com.oracle.sites.visitors.core.rest.resttest.FilterContext;
import com.oracle.sites.visitors.core.rest.resttest.MapperContext;
import com.oracle.sites.visitors.core.rest.resttest.ResourceContext;
import com.oracle.sites.visitors.core.rest.resttest.RestServiceEasyTest;
import com.oracle.sites.visitors.core.rest.resttest.ReturnVal;
import com.oracle.sites.visitors.core.storage.visitor.activity.beans.ActivityImpl;

/**
 * @author napattan
 *
 */
@ResourceContext(names=VisitorResource.class,depends={RestService.class,Requests.class})
//@FilterContext(names=CheckAccessFilter.class,depends={AccessService.class})
//@MapperContext(names=APIExceptionMapper.class)
@ExecutionContext(port=9998,parallel=true)
public class VisitorTest extends RestServiceEasyTest{
	
	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = {HttpServletRequest.class}, 
			expect = @ReturnVal(type=Boolean.class, value = "true")
			)
	public void testAll_MethodsNotAllowed() {

		/**
		 * Methods Not Allowed, Will test the request methods not allowed for a
		 * particular REST resource method
		 */
		this.checkNotAllowedMethods("/visitor/getId", GET, PUT, DELETE, HEAD);
		this.checkNotAllowedMethods("/visitor/current/getId", POST, PUT, DELETE);
		this.checkNotAllowedMethods("/visitor/current/check/exist", POST, PUT,
				DELETE);
		this.checkNotAllowedMethods("/visitor/current/check/guest", POST, PUT,
				DELETE);
		this.checkNotAllowedMethods("/visitor/cut", GET, PUT, DELETE, HEAD);
		this.checkNotAllowedMethods("/visitor/id/sampleVisitorId/check/exist",
				POST, PUT, DELETE);
		this.checkNotAllowedMethods("/visitor/id/sampleVisitorId/check/guest",
				POST, PUT, DELETE);

		this.checkNotAllowedMethods(
				"/visitor/id/sampleVisitorId/profiles/linked", POST, PUT,
				DELETE);
		this.checkNotAllowedMethods(
				"/visitor/id/sampleVisitorId/profile/aggregated/sampleRule",
				POST, PUT, DELETE);
		this.checkNotAllowedMethods("/visitor/id/sampleVisitorId/activity/add",
				GET, PUT, DELETE, HEAD);
		this.checkNotAllowedMethods(
				"/visitor/id/sampleVisitorId/activity/type/sampleType/rated",
				POST, PUT, DELETE);
		this.checkNotAllowedMethods(
				"/visitor/id/sampleVisitorId/activity/type/sampleType/latest",
				POST, PUT, DELETE);
		this.checkNotAllowedMethods(
				"/visitor/id/sampleVisitorId/attributes/save", GET, PUT,
				DELETE, HEAD);
		this.checkNotAllowedMethods("/visitor/register", GET, PUT, DELETE, HEAD);
		this.checkNotAllowedMethods("/visitor/link", GET, PUT, DELETE, HEAD);
		this.checkNotAllowedMethods("/visitor/unlink", GET, PUT, DELETE, HEAD);
	}

	@Test
	@Expectation( 
			type = RestService.class,
			method = "getCurrentVisitorId",
			paramTypes = {HttpServletRequest.class}, 
			times =2,
			expect = @ReturnVal(type=String.class, value = "sampleVisitorId")
			)
	public void testGetCurrentVisitorId() {
		/** MediaType.APPLICATION_JSON_TYPE */
		Response response_json = target("/visitor/current/getId").request()
				.accept(MediaType.APPLICATION_JSON_TYPE).get(Response.class);
		IdProfilesResponse id_json = response_json
				.readEntity(IdProfilesResponse.class);
		assertEquals(200, response_json.getStatus());
		assertEquals("sampleVisitorId", id_json.getVisitorId());
		assertEquals(MediaType.APPLICATION_JSON_TYPE,
				response_json.getMediaType());
		response_json.close();

		/** MediaType.APPLICATION_XML_TYPE */
		Response response_xml = target("/visitor/current/getId").request()
				.accept(MediaType.APPLICATION_XML_TYPE).get(Response.class);
		IdProfilesResponse id_xml = response_xml
				.readEntity(IdProfilesResponse.class);
		assertEquals(200, response_xml.getStatus());
		assertEquals("sampleVisitorId", id_xml.getVisitorId());
		assertEquals(MediaType.APPLICATION_XML_TYPE,
				response_xml.getMediaType());
		response_xml.close();

	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = {HttpServletRequest.class}, 
			expect = @ReturnVal(type=Boolean.class, value = "false")
			)
	public void testGetVisitorId_Unauthorized() {
		/**
		 * Testing AccessService to Not Allow Authorized Resources (Response
		 * Status=401, Unauthorized)
		 */
		String input = "request={\"parameters\":\"{\\\"external_id\\\":[\\\"sample\\\"]}\",\"headers\":\"{}\",\"cookies\":\"[{}]\",\"header\":\"{}\"}";

		Response response_unautorized = target("/visitor/getId")
				.request()
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(input,
						MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertEquals(401, response_unautorized.getStatus());
		response_unautorized.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = {HttpServletRequest.class}, 
			expect = @ReturnVal(type=Boolean.class, value = "false")
			)
	public void testIsExist_Unauthorized() {
		/**
		 * Testing AccessService to Not Allow Authorized Resources (Response
		 * Status=401, Unauthorized)
		 */
		Response response_unautorized = target(
				"/visitor/id/sampleVisitorId/check/exist").request()
				.accept(MediaType.APPLICATION_JSON_TYPE).get();
		assertEquals(401, response_unautorized.getStatus());
		response_unautorized.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = {HttpServletRequest.class}, 
			expect = @ReturnVal(type=Boolean.class, value = "false")
			)
	public void testIsGuest_Unauthorized() {
		/**
		 * Testing AccessService to Not Allow Authorized Resources (Response
		 * Status=401, Unauthorized)
		 */
		Response response_unautorized = target(
				"/visitor/id/sampleVisitorId/check/guest").request()
				.accept(MediaType.APPLICATION_JSON_TYPE).get();
		assertEquals(401, response_unautorized.getStatus());
		response_unautorized.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = {HttpServletRequest.class}, 
			expect = @ReturnVal(type=Boolean.class, value = "false")
			)
	public void testGetLinkedProfiles_Unauthorized() {
		/**
		 * Testing AccessService to Not Allow Authorized Resources (Response
		 * Status=401, Unauthorized)
		 */
		Response response_unautorized = target(
				"/visitor/id/sampleVisitorId/profiles/linked").request()
				.accept(MediaType.APPLICATION_JSON_TYPE).get();
		assertEquals(401, response_unautorized.getStatus());
		response_unautorized.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = {HttpServletRequest.class}, 
			expect = @ReturnVal(type=Boolean.class, value = "false")
			)
	public void testGetAggregatedProfile_Unauthorized() {
		/**
		 * Testing AccessService to Not Allow Authorized Resources (Response
		 * Status=401, Unauthorized)
		 */
		Response response_unautorized = target(
				"/visitor/id/sampleVisitorId/profile/aggregated/sampleRule")
				.request().accept(MediaType.APPLICATION_JSON_TYPE).get();
		assertEquals(401, response_unautorized.getStatus());
		response_unautorized.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = {HttpServletRequest.class}, 
			expect = @ReturnVal(type=Boolean.class, value = "false")
			)
	public void testAddActivity_Unauthorized() {
		/**
		 * Testing AccessService to Not Allow Authorized Resources (Response
		 * Status=401, Unauthorized)
		 */
		MultivaluedMap<String, String> formParams = new MultivaluedHashMap<String, String>();
		Response response_unautorized = target(
				"/visitor/id/sampleVisitorId/activity/add").request().post(
				Entity.entity(formParams,
						MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertEquals(401, response_unautorized.getStatus());
		response_unautorized.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = {HttpServletRequest.class}, 
			expect = @ReturnVal(type=Boolean.class, value = "false")
			)
	public void testGetRated_Unauthorized() {
		/**
		 * Testing AccessService to Not Allow Authorized Resources (Response
		 * Status=401, Unauthorized)
		 */
		Response response_unautorized = target(
				"/visitor/id/sampleVisitorId/activity/type/sampleType/rated")
				.request().accept(MediaType.APPLICATION_JSON_TYPE).get();
		assertEquals(401, response_unautorized.getStatus());
		response_unautorized.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = {HttpServletRequest.class}, 
			expect = @ReturnVal(type=Boolean.class, value = "false")
			)
	public void testGetLatest_Unauthorized() {
		/**
		 * Testing AccessService to Not Allow Authorized Resources (Response
		 * Status=401, Unauthorized)
		 */
		Response response_unautorized = target(
				"/visitor/id/sampleVisitorId/activity/type/sampleType/latest")
				.request().accept(MediaType.APPLICATION_JSON_TYPE).get();
		assertEquals(401, response_unautorized.getStatus());
		response_unautorized.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = {HttpServletRequest.class}, 
			expect = @ReturnVal(type=Boolean.class, value = "false")
			)
	public void testSaveExtAttributes_Unauthorized() {
		/**
		 * Testing AccessService to Not Allow Authorized Resources (Response
		 * Status=401, Unauthorized)
		 */
		MultivaluedMap<String, String> formParams = new MultivaluedHashMap<String, String>();
		Response response_unautorized = target(
				"/visitor/id/sampleVisitorId/attributes/save").request().post(
				Entity.entity(formParams,
						MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertEquals(401, response_unautorized.getStatus());
		response_unautorized.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = {HttpServletRequest.class}, 
			expect = @ReturnVal(type=Boolean.class, value = "false")
			)
	public void testRegisterGuest_Unauthorized() {
		/**
		 * Testing AccessService to Not Allow Authorized Resources (Response
		 * Status=401, Unauthorized)
		 */
		Response response_unautorized = target("/visitor/register")
				.queryParam("id", "sampleGuestId").request()
				.post(this.getTestEntity());
		assertEquals(401, response_unautorized.getStatus());
		response_unautorized.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = {HttpServletRequest.class}, 
			expect = @ReturnVal(type=Boolean.class, value = "false")
			)
	public void testLinkProfiles_Unauthorized() {
		/**
		 * Testing AccessService to Not Allow Authorized Resources (Response
		 * Status=401, Unauthorized)
		 */
		Response response_unautorized = target("/visitor/link")
				.queryParam("ids", "sampleId1,sampleId2").request()
				.post(this.getTestEntity());
		assertEquals(401, response_unautorized.getStatus());
		response_unautorized.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = {HttpServletRequest.class}, 
			expect = @ReturnVal(type=Boolean.class, value = "false")
			)
	public void testUnLinkProfiles_Unauthorized() {
		/**
		 * Testing AccessService to Not Allow Authorized Resources (Response
		 * Status=401, Unauthorized)
		 */
		Response response_unautorized = target("/visitor/unlink")
				.queryParam("ids", "sampleId1,sampleId2").request()
				.post(this.getTestEntity());
		assertEquals(401, response_unautorized.getStatus());
		response_unautorized.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = {HttpServletRequest.class}, 
			expect = @ReturnVal(type=Boolean.class, value = "false")
			)
	public void testCutProfiles_Unauthorized() {
		/**
		 * Testing AccessService to Not Allow Authorized Resources (Response
		 * Status=401, Unauthorized)
		 */
		Response response_unautorized = target("/visitor/cut")
				.queryParam("ids", "sampleId1,sampleId2").request()
				.post(this.getTestEntity());
		assertEquals(401, response_unautorized.getStatus());
		response_unautorized.close();
	}

	//@Test
	public void testGetVisitorId_Unauthorized_Mock() {
		RestService restService = this.getMockedObject(RestService.class);
		AccessService accessService = this.getMockedObject(AccessService.class);
		String input = "request={\"parameters\":\"{\\\"external_id\\\":[\\\"sample\\\"]}\",\"headers\":\"{}\",\"cookies\":\"[{}]\",\"header\":\"{}\"}";
		EasyMock.expect(
				restService.getVisitorId(EasyMock
						.anyObject(HttpServletRequest.class)))
				.andReturn("sampleVisitorId").times(1);

		/**
		 * Testing AccessService to Not Allow Authorized Resources (Response
		 * Status=401, Unauthorized)
		 */

		EasyMock.expect(accessService.isGranted(EasyMock.anyObject()))
				.andReturn(Boolean.FALSE).times(1);
		EasyMock.replay(restService, accessService);
		Response response_unautorized = target("/visitor/getId")
				.request()
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(input,
						MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertEquals(401, response_unautorized.getStatus());
		response_unautorized.close();
	}

	@Test
	@Expectation( 
			type = RestService.class,
			method = "getVisitorId",
			paramTypes = HttpServletRequest.class, 
			expect = @ReturnVal(type=String.class, value = "sampleVisitorId_JSON")
			)
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = HttpServletRequest.class, 
			expect = @ReturnVal(type=Boolean.class, value = "true")
			)
	public void testGetVisitorId_JSON() {
		String input = "request={\"parameters\":\"{\\\"external_id\\\":[\\\"sample\\\"]}\",\"headers\":\"{}\",\"cookies\":\"[{}]\",\"header\":\"{}\"}";

		/**
		 * Testing AccessService to allow and Authorized Resources (Response
		 * Status=200, OK)
		 */
		Response response_json_authorized = target("/visitor/getId")
				.request()
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(input,
						MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		IdProfilesResponse idProfilesResponse_json_authorized = response_json_authorized
				.readEntity(IdProfilesResponse.class);
		assertEquals(200, response_json_authorized.getStatus());
		assertEquals("sampleVisitorId_JSON",
				idProfilesResponse_json_authorized.getVisitorId());
		assertEquals(MediaType.APPLICATION_JSON_TYPE,
				response_json_authorized.getMediaType());
		response_json_authorized.close();

	}

	@Test
	@Expectation( 
			type = RestService.class,
			method = "getVisitorId",
			paramTypes = HttpServletRequest.class, 
			expect = @ReturnVal(type=String.class, value = "sampleVisitorId_XML")
			)
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = HttpServletRequest.class, 
			expect = @ReturnVal(type=Boolean.class, value = "true")
			)
	public void testGetVisitorId_XML() {
		/** CheckAccess required to Allow Authorized methods */
		String input = "request={\"parameters\":\"{\\\"external_id\\\":[\\\"sample\\\"]}\",\"headers\":\"{}\",\"cookies\":\"[{}]\",\"header\":\"{}\"}";
		Response response_xml = target("/visitor/getId")
				.request()
				.accept(MediaType.APPLICATION_XML_TYPE)
				.post(Entity.entity(input,
						MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		IdProfilesResponse idProfilesResponse_xml = response_xml
				.readEntity(IdProfilesResponse.class);
		assertEquals(200, response_xml.getStatus());
		assertEquals("sampleVisitorId_XML",
				idProfilesResponse_xml.getVisitorId());
		assertEquals(MediaType.APPLICATION_XML_TYPE,
				response_xml.getMediaType());
		response_xml.close();

	}

	@Test
	@Expectation( 
			type = RestService.class,
			method = "getCurrentVisitorId",
			paramTypes = HttpServletRequest.class,
			expect = @ReturnVal(type=String.class, value = "sampleVisitorId")
			)
	@Expectation( 
			type = RestService.class,
			method = "isProfileExist",
			paramTypes = String.class,
			times=2,
			expect = @ReturnVal(type=Boolean.class, value = "true")
			)
	public void testIsCurrentExist() {
		/** MediaType.APPLICATION_JSON_TYPE */
		Response response_json = target("/visitor/current/check/exist")
				.request().accept(MediaType.APPLICATION_JSON_TYPE)
				.get(Response.class);
		BooleanResponse booleanResponse_json = response_json
				.readEntity(BooleanResponse.class);
		assertEquals(200, response_json.getStatus());
		assertEquals(Boolean.TRUE, booleanResponse_json.getResult());
		assertEquals(MediaType.APPLICATION_JSON_TYPE,
				response_json.getMediaType());
		response_json.close();

		/** MediaType.APPLICATION_XML_TYPE */
		Response response_xml = target("/visitor/current/check/exist")
				.request().accept(MediaType.APPLICATION_XML_TYPE)
				.get(Response.class);
		BooleanResponse booleanResponse_xml = response_xml
				.readEntity(BooleanResponse.class);
		assertEquals(200, response_xml.getStatus());
		assertEquals(Boolean.TRUE, booleanResponse_xml.getResult());
		assertEquals(MediaType.APPLICATION_XML_TYPE,
				response_xml.getMediaType());
		response_xml.close();

	}

	@Test
	@Expectation( 
			type = RestService.class,
			method = "getCurrentVisitorId",
			paramTypes = HttpServletRequest.class,
			expect = @ReturnVal(type=String.class, value = "sampleVisitorId")
			)
	@Expectation( 
			type = RestService.class,
			method = "isGuest",
			paramTypes = String.class,
			times=2,
			expect = @ReturnVal(type=Boolean.class, value = "true")
			)
	public void testIsCurrentGuest() {

		/** MediaType.APPLICATION_JSON_TYPE */
		Response response_json = target("/visitor/current/check/guest")
				.request().accept(MediaType.APPLICATION_JSON_TYPE)
				.get(Response.class);
		BooleanResponse booleanResponse_json = response_json
				.readEntity(BooleanResponse.class);
		assertEquals(200, response_json.getStatus());
		assertEquals(Boolean.TRUE, booleanResponse_json.getResult());
		assertEquals(MediaType.APPLICATION_JSON_TYPE,
				response_json.getMediaType());
		response_json.close();

		/** MediaType.APPLICATION_XML_TYPE */
		Response response_xml = target("/visitor/current/check/guest")
				.request().accept(MediaType.APPLICATION_XML_TYPE)
				.get(Response.class);
		BooleanResponse booleanResponse_xml = response_xml
				.readEntity(BooleanResponse.class);
		assertEquals(200, response_xml.getStatus());
		assertEquals(Boolean.TRUE, booleanResponse_xml.getResult());
		assertEquals(MediaType.APPLICATION_XML_TYPE,
				response_xml.getMediaType());
		response_xml.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = {HttpServletRequest.class}, 
			expect = @ReturnVal(type=Boolean.class, value = "true")
			)
	@Expectation( 
			type = RestService.class,
			method = "cut",
			paramTypes = String[].class, 
			expect = @ReturnVal(type=Integer.class, value = "1")
			)
	public void testCutProfiles() {
		/** CheckAccess required to Allow Authorized methods */

		final String idsStr = "sampleVisitorId1,sampleVisitorId2";
		Response response = target("/visitor/cut").queryParam("ids", idsStr)
				.request().post(this.getTestEntity());
		RestResponse restResponse = response.readEntity(RestResponse.class);
		assertThat(response.getMediaType(), is(MediaType.APPLICATION_JSON_TYPE));
		assertEquals(200, response.getStatus());
		assertEquals("success", restResponse.getStatus());
		assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
		response.close();

	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = HttpServletRequest.class, 
			times=2,
			expect = @ReturnVal(type=Boolean.class, value = "true")
			)
	@Expectation( 
			type = RestService.class,
			method = "cut",
			paramTypes = String[].class, 
			times=2,
			expect = @ReturnVal(type=Integer.class, value = "1")
			)
	public void testCutProfilesNull() {
		/** CheckAccess required to Allow Authorized methods */
		/** Testing ids null MediaType.APPLICATION_JSON_TYPE is Default */
		final String idsStrNull = null;
		Response response_json = target("/visitor/cut")
				.queryParam("ids", idsStrNull).request()
				.accept(MediaType.APPLICATION_JSON_TYPE).post(null);
		ErrorResponse errorResponse_json = response_json
				.readEntity(ErrorResponse.class);
		assertEquals(200, response_json.getStatus());
		assertEquals(400, errorResponse_json.getErrorCode());
		assertEquals(APIExceptionCodes.VISITOR_IDS_REQUIRED.getMessageKey(),
				errorResponse_json.getErrorMessage());
		assertEquals(MediaType.APPLICATION_JSON_TYPE,
				response_json.getMediaType());
		response_json.close();

		/** MediaType.APPLICATION_XML_TYPE */
		/** CheckAccess required to Allow Authorized methods */
		Response response_xml = target("/visitor/cut")
				.queryParam("ids", idsStrNull).request()
				.accept(MediaType.APPLICATION_XML_TYPE).post(null);
		ErrorResponse errorResponse_xml = response_xml
				.readEntity(ErrorResponse.class);
		assertEquals(200, response_xml.getStatus());
		assertEquals(400, errorResponse_xml.getErrorCode());
		assertEquals(APIExceptionCodes.VISITOR_IDS_REQUIRED.getMessageKey(),
				errorResponse_xml.getErrorMessage());
		assertEquals(MediaType.APPLICATION_XML_TYPE,
				response_xml.getMediaType());
		response_xml.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = HttpServletRequest.class, 
			expect = @ReturnVal(type=Boolean.class, value = "true")
			)
	@Expectation( 
			type = RestService.class,
			method = "getAggregatedProfile",
			paramTypes = {String.class,String.class,Boolean.class},
			expect = @ReturnVal(type=JsonObject.class)
			)
	public void testGetAggregatedProfile() {
		/** CheckAccess required to Allow Authorized methods */
		/** MediaType JSON is default */
		/** Can't Mock JsonObject , because of final class */
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("test", Boolean.TRUE);

		Response response = target(
				"/visitor/id/sampleVisitorId/profile/aggregated/sampleRule")
				.queryParam("updated", Boolean.TRUE).request()
				.accept(MediaType.APPLICATION_JSON_TYPE).get(Response.class);
		AggregatedProfileResponse aggregatedProfileResponse = response
				.readEntity(AggregatedProfileResponse.class);
		assertEquals(200, response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
		response.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = HttpServletRequest.class, 
			expect = @ReturnVal(type=Boolean.class, value = "true")
			)
	@Expectation( 
			type = RestService.class,
			method = "addActivity",
			paramTypes = {String.class,MultivaluedMap.class},
			expect = @ReturnVal()
			)
	public void testAddActivity() {

		/** CheckAccess required to Allow Authorized methods */
		/** MediaType JSON is default */
		MultivaluedMap<String, String> formParams = new MultivaluedHashMap<String, String>();
		Response response = target("/visitor/id/sampleVisitorId/activity/add")
				.request().post(
						Entity.entity(formParams,
								MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		RestResponse restResponse = response.readEntity(RestResponse.class);
		assertEquals(200, response.getStatus());
		assertEquals("success", restResponse.getStatus());
		assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
		response.close();
	}

	//@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = HttpServletRequest.class, 
			expect = @ReturnVal(type=Boolean.class, value = "true")
			)
	@Expectation( 
			type = RestService.class,
			method = "getRated",
			paramTypes = {String.class,String.class,Integer.class},
			expect = @ReturnVal(type=List.class)
			)
	public void testGetRated() {
		/** MediaType JSON is default */
		// TODO: Need to Refactor ActivitiesResponse.class for method,
		// @XmlAnyElement public List<Activity> getActivities()
		//RestService restService = this.getMockedObject(RestService.class);
		//final String visitorId = "sampleVisitorId";
	//	Activity activity = new ActivityImpl(visitorId, "sampleType",
	//			"sampleData");// .createNiceMock(Activity.class);
	//	activity.setId(1L);
	//	activity.setRating(2L);
	//	List<Activity> activities = new ArrayList<Activity>();
	//	activities.add(activity);
	//	EasyMock.expect(restService.getRated(visitorId, "sampleType", 1))
	//			.andReturn(activities).times(1);
	//	EasyMock.replay(restService);
		///id/{visitor_id}/activity/type/{type}/rated
		Response response = target("/visitor/id/sampleVisitorId/activity/type/sampleType/rated").request().get(Response.class);
		assertEquals(200, response.getStatus());

		ActivitiesResponse activitiesResponse = response
				.readEntity(ActivitiesResponse.class);
		// assertEquals(activities, activitiesResponse.getActivities());
		response.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = HttpServletRequest.class, 
			times =2,
			expect = @ReturnVal(type=Boolean.class, value = "true")
			)
	@Expectation( 
			type = RestService.class,
			method = "saveExtendedAttributes",
			paramTypes = {String.class,MultivaluedMap.class},
			times =2,
			expect = @ReturnVal(type=String.class,value="sampleVisitorId")
			)
	public void testSaveExtAttributes() {
		/** CheckAccess required to Allow Authorized methods */
		/** MediaType JSON is default */
		MultivaluedMap<String, String> formParams = new MultivaluedHashMap<String, String>();
		Response response_json = target(
				"/visitor/id/sampleVisitorId/attributes/save").request().post(
				Entity.entity(formParams,
						MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		RestResponse restResponse_json = response_json
				.readEntity(RestResponse.class);
		assertEquals(200, response_json.getStatus());
		assertEquals("success", restResponse_json.getStatus());
		assertEquals(MediaType.APPLICATION_JSON_TYPE,
				response_json.getMediaType());
		response_json.close();

		/** MediaType XML */
		/** CheckAccess required to Allow Authorized methods */

		Response response_xml = target(
				"/visitor/id/sampleVisitorId/attributes/save")
				.request()
				.accept(MediaType.APPLICATION_XML_TYPE)
				.post(Entity.entity(formParams,
						MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		RestResponse restResponse_xml = response_xml
				.readEntity(RestResponse.class);
		assertEquals(200, response_xml.getStatus());
		assertEquals("success", restResponse_xml.getStatus());
		assertEquals(MediaType.APPLICATION_XML_TYPE,
				response_xml.getMediaType());
		response_xml.close();


	}
	
	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = HttpServletRequest.class, 
			expect = @ReturnVal(type=Boolean.class, value = "true")
			)
	@Expectation( 
			type = RestService.class,
			method = "saveExtendedAttributes",
			paramTypes = {String.class,MultivaluedMap.class},
			expect = @ReturnVal(type=String.class)
			)
	public void testSaveExtAttributes_Null() {

		/** Returning null */
		/** CheckAccess required to Allow Authorized methods */
		MultivaluedMap<String, String> formParams = new MultivaluedHashMap<String, String>();
		Response response = target(
				"/visitor/id/sampleVisitorId/attributes/save")
				.request()
				.accept(MediaType.APPLICATION_XML_TYPE)
				.post(Entity.entity(formParams,
						MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		RestResponse restResponse = response.readEntity(RestResponse.class);
		assertEquals(200, response.getStatus());
		assertEquals(null, restResponse.getStatus());
		assertEquals(MediaType.APPLICATION_XML_TYPE, response.getMediaType());
		response.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = HttpServletRequest.class, 
			times =2,
			expect = @ReturnVal(type=Boolean.class, value = "true")
			)
	@Expectation( 
			type = RestService.class,
			method = "registerProfile",
			paramTypes = {String.class},
			times =2,
			expect = @ReturnVal(type=String.class,value="sampleVisitorId")
			)
	public void testRegister() {
		/** CheckAccess required to Allow Authorized methods */
		final String guestId = "sampleGuestId";
		/** MediaType.APPLICATION_JSON_TYPE is Default */
		Response response_json = target("/visitor/register")
				.queryParam("id", guestId).request().post(this.getTestEntity());
		IdProfilesResponse idProfilesResponse_json = response_json
				.readEntity(IdProfilesResponse.class);
		assertEquals(200, response_json.getStatus());
		assertEquals("sampleVisitorId", idProfilesResponse_json.getVisitorId());
		assertEquals(MediaType.APPLICATION_JSON_TYPE,
				response_json.getMediaType());
		response_json.close();

		/** MediaType.APPLICATION_XML_TYPE */
		/** CheckAccess required to Allow Authorized methods */
		Response response_xml = target("/visitor/register")
				.queryParam("id", guestId).request()
				.accept(MediaType.APPLICATION_XML_TYPE)
				.post(this.getTestEntity());
		IdProfilesResponse idProfilesResponse_xml = response_xml
				.readEntity(IdProfilesResponse.class);
		assertEquals(200, response_xml.getStatus());
		assertEquals("sampleVisitorId", idProfilesResponse_xml.getVisitorId());
		assertEquals(MediaType.APPLICATION_XML_TYPE,
				response_xml.getMediaType());
		response_xml.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = HttpServletRequest.class, 
			times =2,
			expect = @ReturnVal(type=Boolean.class, value = "true")
			)
	@Expectation( 
			type = RestService.class,
			method = "unlink",
			paramTypes = String[].class,
			times =2,
			expect = @ReturnVal()
			)
	public void testUnlinkProfiles() {
		final String idsStr = "sampleVisitorId1,sampleVisitorId2";
		/** MediaType.APPLICATION_JSON_TYPE is Default */
		/** CheckAccess required to Allow Authorized methods */
		Response response_json = target("/visitor/unlink")
				.queryParam("ids", idsStr).request().post(this.getTestEntity());
		RestResponse restResponse_json = response_json
				.readEntity(RestResponse.class);
		assertEquals(200, response_json.getStatus());
		assertEquals("success", restResponse_json.getStatus());
		assertEquals(MediaType.APPLICATION_JSON_TYPE,
				response_json.getMediaType());
		response_json.close();

		/** MediaType.APPLICATION_XML_TYPE */
		/** CheckAccess required to Allow Authorized methods */
		Response response_xml = target("/visitor/unlink")
				.queryParam("ids", idsStr).request()
				.accept(MediaType.APPLICATION_XML_TYPE)
				.post(this.getTestEntity());
		RestResponse restResponse_xml = response_xml
				.readEntity(RestResponse.class);
		assertEquals(200, response_xml.getStatus());
		assertEquals("success", restResponse_xml.getStatus());
		assertEquals(MediaType.APPLICATION_XML_TYPE,
				response_xml.getMediaType());
		response_xml.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = HttpServletRequest.class, 
			times =2,
			expect = @ReturnVal(type=Boolean.class, value = "true")
			)
	@Expectation( 
			type = RestService.class,
			method = "link",
			paramTypes = {String[].class},
			times =2,
			expect = @ReturnVal(type=Integer.class,value="1")
			)
	public void testLinkProfiles() {
		/** CheckAccess required to Allow Authorized methods */
		final String idsStr = "sampleVisitorId1,sampleVisitorId2";
		/** MediaType.APPLICATION_JSON_TYPE is Default */
		Response response_json = target("/visitor/link")
				.queryParam("ids", idsStr).request().post(this.getTestEntity());
		RestResponse restResponse_json = response_json
				.readEntity(RestResponse.class);
		assertEquals(200, response_json.getStatus());
		assertEquals("success", restResponse_json.getStatus());
		assertEquals(MediaType.APPLICATION_JSON_TYPE,
				response_json.getMediaType());
		response_json.close();

		/** MediaType.APPLICATION_XML_TYPE */
		/** CheckAccess required to Allow Authorized methods */
		Response response_xml = target("/visitor/link")
				.queryParam("ids", idsStr).request()
				.accept(MediaType.APPLICATION_XML_TYPE)
				.post(this.getTestEntity());
		RestResponse restResponse_xml = response_xml
				.readEntity(RestResponse.class);
		assertEquals(200, response_xml.getStatus());
		assertEquals("success", restResponse_xml.getStatus());
		assertEquals(MediaType.APPLICATION_XML_TYPE,
				response_xml.getMediaType());
		response_xml.close();
	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = HttpServletRequest.class, 
			times =2,
			expect = @ReturnVal(type=Boolean.class, value = "true")
			)
	@Expectation( 
			type = RestService.class,
			method = "link",
			paramTypes = {String[].class},
			times =2,
			expect = @ReturnVal(type=Integer.class,value="1")
			)
	public void testLinkProfilesNull() {
		/** CheckAccess required to Allow Authorized methods */
		/** Testing ids null */
		final String idsStrNull = null;

		/** MediaType.APPLICATION_JSON_TYPE is Default */
		Response response_json = target("/visitor/link")
				.queryParam("ids", idsStrNull).request().post(null);
		ErrorResponse errorResponse_json = response_json
				.readEntity(ErrorResponse.class);
		assertEquals(200, response_json.getStatus());
		assertEquals(400, errorResponse_json.getErrorCode());
		assertEquals(APIExceptionCodes.VISITOR_IDS_REQUIRED.getMessageKey(),
				errorResponse_json.getErrorMessage());
		assertEquals(MediaType.APPLICATION_JSON_TYPE,
				response_json.getMediaType());
		response_json.close();

		/** MediaType.APPLICATION_XML_TYPE */
		/** CheckAccess required to Allow Authorized methods */
		Response response_xml = target("/visitor/link")
				.queryParam("ids", idsStrNull).request()
				.accept(MediaType.APPLICATION_XML_TYPE).post(null);
		ErrorResponse errorResponse_xml = response_xml
				.readEntity(ErrorResponse.class);
		assertEquals(200, response_xml.getStatus());
		assertEquals(400, errorResponse_xml.getErrorCode());
		assertEquals(APIExceptionCodes.VISITOR_IDS_REQUIRED.getMessageKey(),
				errorResponse_xml.getErrorMessage());
		assertEquals(MediaType.APPLICATION_XML_TYPE,
				response_xml.getMediaType());
		response_xml.close();

	}

	@Test
	@Expectation( 
			type = AccessService.class,
			method = "isGranted",
			paramTypes = HttpServletRequest.class, 
			times =2,
			expect = @ReturnVal(type=Boolean.class, value = "true")
			)
	@Expectation( 
			type = RestService.class,
			method = "unlink",
			paramTypes = {String[].class},
			times =2,
			expect = @ReturnVal()
			)
	public void testUnlinkProfilesNull() {
		/** CheckAccess required to Allow Authorized methods */
		/** Testing ids null */
		final String idsStrNull = null;
		/** MediaType.APPLICATION_JSON_TYPE is Default */
		Response response_json = target("/visitor/unlink")
				.queryParam("ids", idsStrNull).request().post(null);
		ErrorResponse errorResponse_json = response_json
				.readEntity(ErrorResponse.class);
		assertEquals(200, response_json.getStatus());
		assertEquals(400, errorResponse_json.getErrorCode());
		assertEquals(APIExceptionCodes.VISITOR_IDS_REQUIRED.getMessageKey(),
				errorResponse_json.getErrorMessage());
		assertEquals(MediaType.APPLICATION_JSON_TYPE,
				response_json.getMediaType());
		response_json.close();

		/** MediaType.APPLICATION_XML_TYPE */
		/** CheckAccess required to Allow Authorized methods */
		Response response_xml = target("/visitor/unlink")
				.queryParam("ids", idsStrNull).request()
				.accept(MediaType.APPLICATION_XML_TYPE).post(null);
		ErrorResponse errorResponse_xml = response_xml
				.readEntity(ErrorResponse.class);
		assertEquals(200, response_xml.getStatus());
		assertEquals(400, errorResponse_xml.getErrorCode());
		assertEquals(APIExceptionCodes.VISITOR_IDS_REQUIRED.getMessageKey(),
				errorResponse_xml.getErrorMessage());
		assertEquals(MediaType.APPLICATION_XML_TYPE,
				response_xml.getMediaType());
		response_xml.close();

	}


}
