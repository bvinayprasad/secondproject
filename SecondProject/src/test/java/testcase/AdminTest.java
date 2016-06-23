package testcase;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.easymock.EasyMock;
import org.junit.Test;

import com.oracle.sites.visitors.api.responses.rest.RestResponse;
import com.oracle.sites.visitors.core.provider.synchronization.ProvidersSynchronizationService;
import com.oracle.sites.visitors.core.rest.AdminRestService;
import com.oracle.sites.visitors.core.rest.resttest.Context;
import com.oracle.sites.visitors.core.rest.resttest.RestServiceEasyTest;

import static javax.ws.rs.HttpMethod.*;

public class AdminTest extends RestServiceEasyTest {
	@Override
	public void init(Context context) {
		/**
		 * How to Use: ============ Resource Class(s) : e.g.
		 * AdminRestService.class , is the class which is RESTful service and
		 * have methods annotated such as @GET, @POST, @PUT , @DELETE etc.
		 * At-least one or more than one Resource class(s) can be added
		 * 
		 * Dependency Class(s) :e.g. ProvidersSynchronizationService.class , is
		 * the class which is injected into Resource class
		 * (AdminRestService.class), Need this class to be added as
		 * addDependency() to mock the object and its expected return value(s)
		 * 
		 */
		context.addResource(AdminRestService.class);
		context.addDependency(ProvidersSynchronizationService.class);
	}

	@Test
	public void testAll_MethodsNotAllowed() {
		this.checkNotAllowedMethods("/admin/accessProvider/synchronize", POST,
				PUT, DELETE);
	}

	@Test
	public void testSynchronizeAsync_True() {

		ProvidersSynchronizationService providersSynchronizationService = this
				.getMockedObject(ProvidersSynchronizationService.class);
		providersSynchronizationService.synchronizeForceAsync("accessProvider",
				"testName", "install");
		EasyMock.expectLastCall();
		EasyMock.replay(providersSynchronizationService);
		Response response = target("/admin/accessProvider/synchronize")
				.queryParam("itemName", "testName")
				.queryParam("action", "install").queryParam("async", true)
				.request().get();
		assertEquals(200, response.getStatus());
	}

	@Test
	public void testSynchronizeAsync_MediaType_Default() {

		ProvidersSynchronizationService providersSynchronizationService = this
				.getMockedObject(ProvidersSynchronizationService.class);
		EasyMock.expect(
				providersSynchronizationService.synchronizeForceSync(
						"accessProvider", "testName", "install")).andReturn(
				Boolean.TRUE);
		EasyMock.replay(providersSynchronizationService);
		
		/** MediaType.APPLICATION_JSON_TYPE is Default */
		Response response = target("/admin/accessProvider/synchronize")
				.queryParam("itemName", "testName")
				.queryParam("action", "install").queryParam("async", false)
				.request().get(Response.class);
		RestResponse restResponse = response.readEntity(RestResponse.class);
		assertEquals(200, response.getStatus());
		assertEquals("success", restResponse.getStatus());
		assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());

	}

	@Test
	public void testSynchronizeAsync_MediaType_XML() {

		ProvidersSynchronizationService providersSynchronizationService = this
				.getMockedObject(ProvidersSynchronizationService.class);
		EasyMock.expect(
				providersSynchronizationService.synchronizeForceSync(
						"accessProvider", "testName", "install")).andReturn(
				Boolean.TRUE);
		EasyMock.replay(providersSynchronizationService);
		Response response = target("/admin/accessProvider/synchronize")
				.queryParam("itemName", "testName")
				.queryParam("action", "install").queryParam("async", false)
				.request().accept(MediaType.APPLICATION_XML)
				.get(Response.class);
		RestResponse restResponse = response.readEntity(RestResponse.class);
		assertEquals(200, response.getStatus());
		assertEquals("success", restResponse.getStatus());
		assertEquals(MediaType.APPLICATION_XML_TYPE, response.getMediaType());

	}

	@Test
	public void testSynchronizeAsync_MediaType_JSON() {

		ProvidersSynchronizationService providersSynchronizationService = this
				.getMockedObject(ProvidersSynchronizationService.class);
		EasyMock.expect(
				providersSynchronizationService.synchronizeForceSync(
						"accessProvider", "testName", "install")).andReturn(
				Boolean.TRUE);
		EasyMock.replay(providersSynchronizationService);
		Response response = target("/admin/accessProvider/synchronize")
				.queryParam("itemName", "testName")
				.queryParam("action", "install").queryParam("async", false)
				.request().accept(MediaType.APPLICATION_JSON)
				.get(Response.class);
		RestResponse restResponse = response.readEntity(RestResponse.class);
		assertEquals(200, response.getStatus());
		assertEquals("success", restResponse.getStatus());
		assertThat(response.getMediaType(), is(MediaType.APPLICATION_JSON_TYPE));

	}

	@Test
	public void testSynchronizeAsyncFalseReturnTrue() {

		ProvidersSynchronizationService providersSynchronizationService = this
				.getMockedObject(ProvidersSynchronizationService.class);
		EasyMock.expect(
				providersSynchronizationService.synchronizeForceSync(
						"accessProvider", "testName", "install")).andReturn(
				Boolean.TRUE);
		EasyMock.replay(providersSynchronizationService);
		Response response = target("/admin/accessProvider/synchronize")
				.queryParam("itemName", "testName")
				.queryParam("action", "install").queryParam("async", false)
				.request().get(Response.class);
		assertEquals(200, response.getStatus());
		RestResponse restResponse = response.readEntity(RestResponse.class);
		assertEquals("success", restResponse.getStatus());
		response.close();
	}

	@Test
	public void testSynchronizeAsyncFalseReturnFalse() {
		ProvidersSynchronizationService providersSynchronizationService = this
				.getMockedObject(ProvidersSynchronizationService.class);
		EasyMock.expect(
				providersSynchronizationService.synchronizeForceSync(
						"accessProvider", "testName", "install")).andReturn(
				Boolean.FALSE);
		EasyMock.replay(providersSynchronizationService);
		Response response = target("/admin/accessProvider/synchronize")
				.queryParam("itemName", "testName")
				.queryParam("action", "install").queryParam("async", false)
				.request().get(Response.class);
		assertEquals(200, response.getStatus());
		RestResponse restResponse = response.readEntity(RestResponse.class);
		assertEquals("failure", restResponse.getStatus());
		response.close();
	}

}
