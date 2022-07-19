package da.springframework.springbootwebfluxapirest.repositories;

import da.springframework.springbootwebfluxapirest.model.documents.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoryRepository extends ReactiveMongoRepository<Category, String> {
}
