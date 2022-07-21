package da.springframework.springbootwebfluxapirest;

import da.springframework.springbootwebfluxapirest.model.documents.Category;
import da.springframework.springbootwebfluxapirest.model.documents.Product;
import da.springframework.springbootwebfluxapirest.services.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootWebfluxApirestApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductService productService;

    @Test
    void testListProducts() {

        webTestClient.get()
                .uri("/api/v2/products")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Product.class)
                .consumeWith(listEntityExchangeResult -> {
                    List<Product> products = listEntityExchangeResult.getResponseBody();
                    products.forEach(product -> {
                        System.out.println(product.getName());
                    });

                    Assertions.assertTrue(products.size() > 0);
                });
//                .hasSize(9); //same as de Assertions
    }

    @Test
    void testProductDetail() {

        Product product = productService.findByName("TV Panasonic Pantalla LCD").block();

        webTestClient.get()
                .uri("/api/v2/products/{id}", Collections.singletonMap("id", product.getId()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Product.class)
                .consumeWith(productEntityExchangeResult -> {
                    Product productResult = productEntityExchangeResult.getResponseBody();

                    Assertions.assertNotNull(productResult.getId());
                    Assertions.assertTrue(productResult.getId().length() > 0);
                    Assertions.assertEquals("TV Panasonic Pantalla LCD", productResult.getName());
                });
//                .expectBody() //same as above
//                .jsonPath("$.id").isNotEmpty()
//                .jsonPath("$.name").isEqualTo("TV Panasonic Pantalla LCD");
    }

    @Test
    void testCreateProduct() {

        Category category = productService.findCategoryByName("Electrónico").block();
        Product product = new Product("PS5", 599.99, category);

        webTestClient.post().uri("/api/v2/products")
                .contentType(MediaType.APPLICATION_JSON) //request MediaType
                .accept(MediaType.APPLICATION_JSON) //response MediaType
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("PS5")
                .jsonPath("$.category.name").isEqualTo("Electrónico");
    }

    @Test
    void testCreateProduct2() {

        Category category = productService.findCategoryByName("Electrónico").block();
        Product product = new Product("PS5", 599.99, category);

        webTestClient.post().uri("/api/v2/products")
                .contentType(MediaType.APPLICATION_JSON) //request MediaType
                .accept(MediaType.APPLICATION_JSON) //response MediaType
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Product.class)
                .consumeWith(productEntityExchangeResult -> {
                    Product productResult = productEntityExchangeResult.getResponseBody();

                    Assertions.assertNotNull(productResult.getId());
                    Assertions.assertEquals("PS5", productResult.getName());
                    Assertions.assertEquals("Electrónico", productResult.getCategory().getName());
                });
    }

    @Test
    void testUpdateProduct() {
        Product product = productService.findByName("Mica Cómoda 5 Cajones").block();
        Category category = productService.findCategoryByName("Electrónico").block();

        Product editedProduct = new Product("PS5", 599.99, category);

        webTestClient.put().uri("/api/v2/products/{id}", Collections.singletonMap("id", product.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(editedProduct), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isEqualTo("PS5")
                .jsonPath("$.category.name", "Electrónico");
    }

    @Test
    void testDeleteProduct() {

        Product product = productService.findByName("HP Notebook Omen 17").block();

        webTestClient.delete().uri("/api/v2/products/{id}", Collections.singletonMap("id", product.getId()))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        webTestClient.get().uri("/api/v2/products/{id}", Collections.singletonMap("id", product.getId()))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }
}
