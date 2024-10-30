package com.workshop.route.application.controller;

import com.workshop.route.application.response.service.RouteResponseService;
import com.workshop.route.application.services.RouteCommandService;
import com.workshop.route.domain.model.aggregates.Route;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/routes")
public class RouteCommandController {

    private static final Logger logger = LoggerFactory.getLogger(RouteCommandController.class);
    private final RouteCommandService routeCommandService;
    private final RouteResponseService routeResponseService;

    public RouteCommandController(RouteCommandService routeCommandService, RouteResponseService routeResponseService) {
        this.routeCommandService = routeCommandService;
        this.routeResponseService = routeResponseService;
    }

    @PostMapping
    public Mono<ResponseEntity<Route>> createRoute(@RequestBody Route route) {
        logger.info("Attempting to create a new route with name: {}", route.getRouteName());
        return routeCommandService.createRoute(route)
                .flatMap(routeResponseService::buildCreatedResponse)
                .doOnSuccess(response -> logger.info("Successfully created route with ID: {}", route.getRouteId()));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Route>> updateRoute(@PathVariable ObjectId id, @RequestBody Route route) {
        logger.info("Attempting to update route with ID: {}", id);
        return routeCommandService.updateRoute(id, route)
                .flatMap(routeResponseService::buildOkResponse)
                .doOnSuccess(response -> logger.info("Successfully updated route with ID: {}", id))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRoute(@PathVariable ObjectId id) {
        logger.info("Attempting to delete route with ID: {}", id);
        return routeCommandService.deleteRoute(id)
                .then(routeResponseService.buildNoContentResponse())
                .doOnSuccess(response -> logger.info("Successfully deleted route with ID: {}", id));
    }
}