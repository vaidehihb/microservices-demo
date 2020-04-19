package com.microservices.demo.moviecatalogservice.resources;

import com.microservices.demo.moviecatalogservice.models.CatalogItem;
import com.microservices.demo.moviecatalogservice.models.Movie;
import com.microservices.demo.moviecatalogservice.models.Rating;
import com.microservices.demo.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    private RestTemplate restTemplate;

//    @Qualifier("webclientbuilder")
//    @Autowired
//    private WebClient.Builder webClientBuilder;


    @GetMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {
        
        // Get list of rated movie Ids
        UserRating userRating = restTemplate.getForObject("http://localhost:8083/ratingsdata/users" + userId, UserRating.class);

        return userRating.getRatings().stream().map(rating -> {
            // get movie info for each movie Id from movie info service
            Movie movie = restTemplate.getForObject("http://localhost:8082/movies/" + rating.getMovieId(), Movie.class);

//            Movie movie = webClientBuilder.build()
//                    .get()
//                    .uri("http://localhost:8082/movies/" + rating.getMovieId())
//                    .retrieve()
//                    .bodyToMono(Movie.class)
//                    .block();

            // put them all together
            return CatalogItem.builder().name(movie.getName()).desc("Desc").rating(rating.getRating()).build();
        }).collect(Collectors.toList());
    }

}
