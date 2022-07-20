package da.springframework.springbootwebfluxapirest.config;

import da.springframework.springbootwebfluxapirest.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(ProductHandler productHandler) {

        //can map n routes to the same response
        return route(GET("/api/v2/products").or(GET("/api/v3/products")), productHandler::listProducts)
                .andRoute(GET("/api/v2/products/{id}"), productHandler::productDetail)
                .andRoute(POST("/api/v2/products"), productHandler::createProduct);
    }
}
