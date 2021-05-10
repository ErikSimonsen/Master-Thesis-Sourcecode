package personal.simonsen;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.transaction.Transactional;
import java.util.List;

@RequestScoped
public class FruitRepository {
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    EntityManager em;

    public List<Fruit> findAll() {
        return em.createQuery("SELECT f from Fruit f", Fruit.class).getResultList();
    }

    public Fruit findById(Long id) {
        return em.find(Fruit.class, id);
    }

    @Transactional
    public void save(Fruit fruit) {
        em.persist(fruit);
    }

    @Transactional
    public void update(Fruit fruit) {
        em.merge(fruit);
    }

    @Transactional
    public void delete(Fruit fruit) {
        em.remove(em.merge(fruit));
    }
}
