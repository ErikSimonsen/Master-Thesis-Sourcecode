package personal.simonsen;

import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.enterprise.event.Observes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

@Path("/greeting")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{name}")
    public Uni<String> greeting(String name) {
        return Uni.createFrom().item(name).onItem().transform(n -> String.format("hello %s", n));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{count}/{name}")
    public Multi<String> greetings(int count, String name) {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .onItem().transform(n -> String.format("hello %s - %d", name, n))
                .transform().byTakingFirstItems(count);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> hello() {
        System.out.println("CALL HELLO " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date(System.currentTimeMillis())));
        return Uni.createFrom().item("Hello RESTEasy Reactive");
    }

    void onStart(@Observes StartupEvent startup) {
        System.out.println("STARTUP " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()));
    }
}