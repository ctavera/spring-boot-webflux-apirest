package da.springframework.springbootwebfluxapirest.controllers;

import da.springframework.springbootwebfluxapirest.model.documents.Product;
import da.springframework.springbootwebfluxapirest.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
}
