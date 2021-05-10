package personal.simonsen;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;

import javax.persistence.*;

@Entity
@Table(name = "fruits")
public class Fruit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;

    public Fruit() {
        //default constructor
    }

    public Fruit(String name) {
        this.name = name;
    }

    public Fruit(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Multi<Fruit> findAll(PgPool client) {
        return client.query("SELECT id, name FROM fruits ORDER BY name ASC").execute()
                //Create a Multi from the Set of Rows
                // .onItem().transformToMulti(set -> Multi.createFrom().items(() -> StreamSupport.stream(set.spliterator(), false)))
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                //For each row create a fruit instance
                .onItem().transform(Fruit::from);
    }

    /**
     * public static Uni<Fruit> findById(PgPool client, Long id) {
     * return client.preparedQuery("SELECT id, name from fruits WHERE id = $1").execute(Tuple.of(id))
     * .onItem().transform(RowSet::iterator)
     * .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
     * }
     * <p>
     * public Uni<Long> save(PgPool client) {
     * return client.preparedQuery("INSERT INTO fruits (name) VALUES ($1) RETURNING (id)").execute(Tuple.of(name))
     * .onItem().transform(pgRowSet -> pgRowSet.iterator().next().getLong("id"));
     * }
     * //could also use the id of the Fruit Object, but then it would be required to pass in the Request Body (which is
     * //kinda redundant because it is already submitted in the URI)
     * public Uni<Boolean> update(PgPool client, Long id) {
     * return client.preparedQuery("UPDATE fruits SET name = $1 WHERE id = $2").execute(Tuple.of(name, id))
     * .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
     * }
     * <p>
     * public static Uni<Boolean> delete(PgPool client, Long id) {
     * return client.preparedQuery("DELETE FROM fruits WHERE id = $1").execute(Tuple.of(id))
     * .onItem().transform(pgRowSet -> pgRowSet.rowCount() == 1);
     * }
     */
    public static Fruit from(Row row) {
        return new Fruit(row.getLong("id"), row.getString("name"));
    }

}
