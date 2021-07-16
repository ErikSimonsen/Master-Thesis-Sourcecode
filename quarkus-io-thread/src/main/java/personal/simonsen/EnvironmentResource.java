package personal.simonsen;

import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/environment")
public class EnvironmentResource {
    @ConfigProperty(name = "quarkus.vertx.worker-pool-size") 
    int workerPoolSize;
    @ConfigProperty(name = "quarkus.vertx.event-loops-pool-size") 
    int eventLoopPoolSize;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> env() {
        StringBuilder stringBuilder = new StringBuilder();

        ProcessHandle processHandle = ProcessHandle.current();

        stringBuilder.append("pid: ").append(processHandle.pid()).append("\n");
        stringBuilder.append("\n");

        stringBuilder.append("commandLine: ").append(processHandle.info().commandLine().get()).append("\n");
        stringBuilder.append("\n");

        stringBuilder.append("thread-name: ").append(Thread.currentThread().getName()).append("\n");
        stringBuilder.append("\n");
        stringBuilder.append("stack trace: ").append("\n");
        stringBuilder.append("worker-pool-size: ").append(workerPoolSize).append("\n");
        stringBuilder.append("eventLoop-pool-size: ").append(eventLoopPoolSize).append("\n");
        
        Arrays.stream(Thread.currentThread().getStackTrace()).forEach(ste -> stringBuilder.append(ste.toString()).append("\n"));

        return Uni.createFrom().item(stringBuilder.toString());
    }
}
