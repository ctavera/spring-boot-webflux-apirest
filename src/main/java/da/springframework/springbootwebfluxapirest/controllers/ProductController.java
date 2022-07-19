package da.springframework.springbootwebfluxapirest.controllers;

import da.springframework.springbootwebfluxapirest.model.documents.Product;
import da.springframework.springbootwebfluxapirest.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

//    @GetMapping
//    public Flux<Product> listProducts() {
//        return productService.findAll();
//    }

    @GetMapping// same as above
    public Mono<ResponseEntity<Flux<Product>>> listProducts() {
        return Mono.just(
//                ResponseEntity.ok(productService.findAll())
                ResponseEntity.ok() // same as above
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(productService.findAll())
        );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> productDetail(@PathVariable String id) {
        return productService.findById(id)
                .map(product -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(product))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Product>> saveProduct(@RequestBody Product product) {

        if (product.getCreationDate() == null){
            product.setCreationDate(new Date());
        }

        return productService.save(product).map(prod -> ResponseEntity
                        .created(URI.create("/api/v1/products/".concat(prod.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(prod));
    }
}
