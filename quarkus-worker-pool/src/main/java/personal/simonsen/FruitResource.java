package personal.simonsen;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/fruits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResource {

    @Inject
    FruitRepository fruitRepository;

    @GET
    public List<Fruit> getAll() {
        return fruitRepository.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getSingle(@PathParam("id") Long id) {
        Fruit fruit = fruitRepository.findById(id);
        if (fruit == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(fruit).build();
    }

    @POST
    public Response create(Fruit fruit) {
        if (fruit == null || fruit.getId() != null) {
            return Response.status(422).build();
        }
        fruitRepository.save(fruit);
        return Response.created(URI.create("/fruits/" + fruit.getId())).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(Fruit fruit, @PathParam("id") Long id) {
        if (fruit == null || fruit.getName() == null) {
            return Response.status(422).build();
        }

        Fruit storedFruit = fruitRepository.findById(id);
        if (storedFruit == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        storedFruit.setName(fruit.getName());
        fruitRepository.update(storedFruit);
        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        Fruit fruit = fruitRepository.findById(id);
        if (fruit == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        fruitRepository.delete(fruit);
        return Response.status(Response.Status.OK).build();
    }

}
