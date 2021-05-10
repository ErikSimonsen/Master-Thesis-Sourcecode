package personal.simonsen;

import io.quarkus.runtime.StartupEvent;

import javax.enterprise.event.Observes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.Date;

@Path("/greeting")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{name}")
    public String greeting(@PathParam("name") String name) {
        return "hello " + name;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        System.out.println("CALL HELLO " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date(System.currentTimeMillis())));
        return "Hello RESTEasy";
    }

    void onStart(@Observes StartupEvent startup) {
        System.out.println("STARTUP " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
    }

}