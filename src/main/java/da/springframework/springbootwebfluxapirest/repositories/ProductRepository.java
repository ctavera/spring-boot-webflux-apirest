package da.springframework.springbootwebfluxapirest.repositories;

import da.springframework.springbootwebfluxapirest.model.documents.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
}
