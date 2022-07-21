package da.springframework.springbootwebfluxapirest.repositories;

import da.springframework.springbootwebfluxapirest.model.documents.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CategoryRepository extends ReactiveMongoRepository<Category, String> {

    Mono<Category> findByName(String name);
}
