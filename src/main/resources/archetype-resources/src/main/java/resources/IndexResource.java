#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.InputStream;

/**
 * Resource that serves up the index page.
 */
@Path("/")
public class IndexResource {
    @GET
    @Produces("text/html")
    public InputStream index() {
        return getClass().getResourceAsStream("/index.html");
    }

    @GET
    @Path("/input.xml")
    @Produces("text/xml")
    public InputStream inputTestData() {
        return getClass().getResourceAsStream("/input.xml");
    }
}
