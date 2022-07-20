package da.springframework.springbootwebfluxapirest.handler;

import da.springframework.springbootwebfluxapirest.model.documents.Product;
import da.springframework.springbootwebfluxapirest.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@RequiredArgsConstructor
@Component
public class ProductHandler {

    private final ProductService productService;

    public Mono<ServerResponse> listProducts(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productService.findAll(), Product.class);
    }

    public Mono<ServerResponse> productDetail(ServerRequest request) {

        String id = request.pathVariable("id");
        return productService.findById(id)
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(product)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createProduct(ServerRequest request) {

        Mono<Product> productMono = request.bodyToMono(Product.class);
        return productMono.flatMap(product -> {

            if (product.getCreationDate() == null) {
                product.setCreationDate(new Date());
            }

            return productService.save(product);
        }).flatMap(product -> ServerResponse.created(URI.create("/api/v2/products" + product.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(product))
        );
    }

    public Mono<ServerResponse> updateProduct(ServerRequest request) {

        Mono<Product> productMono = request.bodyToMono(Product.class);
        String id = request.pathVariable("id");

        Mono<Product> productMonoDB = productService.findById(id);

        return productMonoDB.zipWith(productMono, (productDB, productRequest) -> {
            productDB.setName(productRequest.getName());
            productDB.setPrice(productRequest.getPrice());
            productDB.setCategory(productRequest.getCategory());

            return productDB;
        }).flatMap(product -> ServerResponse.created(URI.create("/api/v2/products" + product.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(productService.save(product), Product.class)
        ).switchIfEmpty(ServerResponse.notFound().build());
    }
}
