package personal.simonsen;

import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class FruitRepository {
    @Inject
    Mutiny.SessionFactory sessionFactory;

    public Uni<List<Fruit>> findAll() {
        return sessionFactory.withSession(
                session -> session.createQuery("SELECT f FROM Fruit f", Fruit.class).getResultList()
        );
    }

    public Uni<Fruit> findById(Long id) {
        return sessionFactory.withSession(
                session -> session.find(Fruit.class, id)
        );
    }

    public Uni<Void> save(Fruit fruit) {
        return sessionFactory.withTransaction((session, tx) -> session.persist(fruit));
    }

    public Uni<Fruit> update(Fruit fruit) {
        return sessionFactory.withTransaction(
                (session, tx) -> session.merge(fruit)
        );
    }

    public Uni<Void> delete(Fruit fruit) {
        //could also use find here instead of using find at the api endpoint then using merge here
        return sessionFactory.withTransaction(
                (session, tx) -> session.merge(fruit).call(f -> session.remove(f))
                        .onItem().transformToUni(f -> Uni.createFrom().voidItem()) //this is optional, otherwise delete would return Uni<Fruit> tho
        );
    }
}
