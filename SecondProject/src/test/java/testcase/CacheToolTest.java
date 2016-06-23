package testcase;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.oracle.sites.visitors.core.rest.CacheToolResource;
import com.oracle.sites.visitors.core.rest.beans.response.CacheToolResponse;
import com.oracle.sites.visitors.core.rest.resttest.Context;
import com.oracle.sites.visitors.core.rest.resttest.RestServiceEasyTest;

public class CacheToolTest extends RestServiceEasyTest
{

    @Override
    public void init(Context context)
    {
        context.addResource(CacheToolResource.class);
    }
    
    //@Test
    public void testGetList()
    {
        Response response = target("/cachetool/pageByQry/list").request().get(Response.class);
        assertEquals(500, response.getStatus());
        CacheToolResponse cacheToolResponse=response.readEntity(CacheToolResponse.class);
        assertEquals(null, cacheToolResponse.getStatus());
        response.close();
    }

}
