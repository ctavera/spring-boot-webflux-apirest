package da.springframework.springbootwebfluxapirest.services;

import da.springframework.springbootwebfluxapirest.model.documents.Category;
import da.springframework.springbootwebfluxapirest.model.documents.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Flux<Product> findAll();

    Flux<Product> findAllByNameUpperCase();

    Flux<Product> findAllByNameUpperCaseRepeat();

    Mono<Product> findById(String id);

    Mono<Product> save(Product product);

    Mono<Void> delete(Product product);

    Flux<Category> findAllCategories();

    Mono<Category> findCategoryById(String id);

    Mono<Category> saveCategory(Category category);

    Mono<Product> findByName(String name);

    Mono<Product> customObtainByName(String name);

    Mono<Category> findCategoryByName(String name);
}
