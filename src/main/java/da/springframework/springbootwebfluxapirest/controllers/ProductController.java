package da.springframework.springbootwebfluxapirest.controllers;

import da.springframework.springbootwebfluxapirest.model.documents.Product;
import da.springframework.springbootwebfluxapirest.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @Value("${config.uploads.path}")
    private String path;

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
    public Mono<ResponseEntity<Map<String, Object>>> createProduct(@Valid @RequestBody Mono<Product> productMono) {

        Map<String, Object> response = new HashMap<>(); //needed for response of product or list of errors
        return productMono.flatMap(product -> {
            if (product.getCreationDate() == null){
                product.setCreationDate(new Date());
            }

            return productService.save(product).map(prod -> {
                response.put("product", prod);
                //Not necessary, only for example
                response.put("message", "Producto creado con éxito");
                response.put("timestamp", new Date());

                return ResponseEntity
                        .created(URI.create("/api/v1/products/".concat(prod.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            });
        }).onErrorResume(throwable -> Mono.just(throwable).cast(WebExchangeBindException.class) //cast the exception to a Mono<WebExchangeBindException>
                .flatMap(e -> Mono.just(e.getFieldErrors())) //transform into Mono<List>
                .flatMapMany(Flux::fromIterable)//transform into Flux to iterate errors
                .map(fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())//transform each error on String
                .collectList()//transform the Flux into Mono<List>
                .flatMap(list -> { //transform the Mono<List> in a Mono<ResponseEntity<Map>>
                    response.put("errors", list);
                    //Not necessary, only for example
                    response.put("timestamp", new Date());
                    response.put("status", HttpStatus.BAD_REQUEST.value());

                    return Mono.just(ResponseEntity.badRequest().body(response));
                })
        );
    }

    @PostMapping("/withPhoto")// anti-pattern, must be v2 by example
    public Mono<ResponseEntity<Product>> createProductWithPhoto(Product product, @RequestPart(name = "file") FilePart filePart) {

        if (product.getCreationDate() == null) {
            product.setCreationDate(new Date());
        }

        product.setPhoto(UUID.randomUUID() + "-" + filePart.filename()
                .replace(" ", "")
                .replace(":", "")
                .replace("\\", ""));

        return filePart.transferTo(new File(path + product.getPhoto())).then(productService.save(product))
                .map(prod -> ResponseEntity
                        .created(URI.create("/api/v1/products/".concat(prod.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(prod));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Product>> updateProduct (@RequestBody Product product, @PathVariable String id) {

        return productService.findById(id)
                .flatMap(prod -> {
                    prod.setName(product.getName());
                    prod.setPrice(product.getPrice());
                    prod.setCategory(product.getCategory());

                    return productService.save(prod);
                }).map(prod -> ResponseEntity
                        .created(URI.create("/api/v1/products".concat(prod.getId())))
                        .body(prod))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProduct (@PathVariable String id) {
        return productService.findById(id)
                .flatMap(product -> productService.delete(product)
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/upload/{id}")
    public Mono<ResponseEntity<Product>> uploadPhoto(@PathVariable String id, @RequestPart(name = "file") FilePart filePart) {
        return productService.findById(id)
                .flatMap(product -> {
                    product.setPhoto(UUID.randomUUID() + "-" + filePart.filename()
                            .replace(" ", "")
                            .replace(":", "")
                            .replace("\\", ""));

                    return filePart.transferTo(new File(path + product.getPhoto())).then(productService.save(product));
                }).map(product -> ResponseEntity.ok(product))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
