package personal.simonsen;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;

@Path("/environment")
public class EnvironmentResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getEnv() {
        StringBuilder stringBuilder = new StringBuilder();

        ProcessHandle processHandle = ProcessHandle.current();

        stringBuilder.append("pid: ").append(processHandle.pid()).append("\n");
        stringBuilder.append("\n");

        stringBuilder.append("commandLine: ").append(processHandle.info().commandLine().get()).append("\n");
        stringBuilder.append("\n");

        stringBuilder.append("thread-name: ").append(Thread.currentThread().getName()).append("\n");
        stringBuilder.append("\n");
        stringBuilder.append("stack trace: ").append("\n");

        Arrays.stream(Thread.currentThread().getStackTrace()).forEach(ste -> stringBuilder.append(ste.toString()).append("\n"));
        return stringBuilder.toString();
    }
}
