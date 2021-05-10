package personal.simonsen;

import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.util.List;

@Path("/fruits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResource {
    @Inject
    FruitRepository fruitRepository;

    @GET
    public Uni<List<Fruit>> getAll() {
        return fruitRepository.findAll();
    }

    @GET
    @Path("{id}")
    public Uni<Fruit> getSingle(Long id) {
        return fruitRepository.findById(id);
    }

    @POST
    public Uni<Response> create(Fruit fruit) {
        if (fruit == null || fruit.getId() != null) {
            return Uni.createFrom().item(Response.status(422).build());
        }
        return fruitRepository.save(fruit)
                .onItem().transform(a -> URI.create("/fruits/" + fruit.getId()))
                .onItem().transform(uri -> Response.created(uri).build());
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(Long id, Fruit fruit) {
        if (fruit == null || fruit.getName() == null) {
            return Uni.createFrom().item(Response.status(422).build());
        }
        return fruitRepository.findById(id).onItem().ifNotNull().invoke(storedFruit ->
                storedFruit.setName(fruit.getName())
        ).call(storedFruit -> fruitRepository.update(storedFruit))
                .onItem().ifNotNull().transform(storedFruit -> Response.ok(storedFruit).build())
                .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(Long id) {
        return fruitRepository.findById(id).onItem().ifNotNull().call(storedFruit -> fruitRepository.delete(storedFruit))
                .onItem().ifNotNull().transform(storedFruit -> Response.status(Status.NO_CONTENT).build())
                .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
    }
}
