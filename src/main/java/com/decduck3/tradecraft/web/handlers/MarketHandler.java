package com.decduck3.tradecraft.web.handlers;

import com.decduck3.tradecraft.TradeCraft;
import com.decduck3.tradecraft.db.models.Listing;
import com.decduck3.tradecraft.web.data.WebSerializer;
import com.google.common.primitives.Ints;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.search.SearchOperator;
import com.mongodb.client.model.search.SearchPath;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import org.bson.conversions.Bson;

import javax.swing.text.html.Option;
import java.util.*;

import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.mql.MqlValues.*;
import static com.mongodb.client.model.Sorts.ascending;

public class MarketHandler implements HttpHandler {
    public static int GLOBAL_ITEM_LIMIT = 8;

    private enum SortingOption {
        Date("recent"),
        PriceLowHigh("pricelowhigh"),
        PriceHighLow("pricehighlow"),
        Random("random");

        public final String key;

        SortingOption(String label) {
            this.key = label;
        }
    }

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        Handlers.routing()
                // Fetch all listings (with pagination and stuff)
                .add("GET", "/l/all", exchange -> {
                    List<Listing> listings = new ArrayList<>();
                    Map<String, Deque<String>> query = exchange.getQueryParameters();
                    List<Bson> aggregationParams = new ArrayList<>();

                    SortingOption sortingOption = SortingOption.Date;
                    if(query.containsKey("sort")){
                        String requestedSortingAlgorithm = query.get("sort").getFirst();
                        Optional<SortingOption> foundRequestedSortingAlgorithm = Arrays.stream(SortingOption.values()).filter(e -> e.key.equals(requestedSortingAlgorithm)).findFirst();
                        if(foundRequestedSortingAlgorithm.isPresent()){
                            sortingOption = foundRequestedSortingAlgorithm.get();
                        }else{
                            exchange.setStatusCode(400);
                            exchange.getResponseSender().send("Invalid sorting algorithm");
                            exchange.getResponseSender().close();
                            return;
                        }
                    }

                    switch (sortingOption){
                        case Date -> aggregationParams.add(Aggregates.sort(descending("createdAt")));
                        case PriceLowHigh -> {
                            aggregationParams.add(Aggregates.addFields(new Field<>("finalPrice", current().getInteger("centsPerUnit").multiply(current().getNumber("saleMultipler")))));
                            aggregationParams.add(Aggregates.sort(ascending("finalPrice")));
                        }
                        case PriceHighLow -> {
                            aggregationParams.add(Aggregates.addFields(new Field<>("finalPrice", current().getInteger("centsPerUnit").multiply(current().getNumber("saleMultipler")))));
                            aggregationParams.add(Aggregates.sort(descending("finalPrice")));
                        }
                        case Random -> aggregationParams.add(Aggregates.sample(GLOBAL_ITEM_LIMIT));
                    }

                    int skip = 0;
                    if(query.containsKey("skip")){
                        skip = Optional.ofNullable(Ints.tryParse(query.get("skip").getFirst())).orElse(0);
                    }
                    aggregationParams.add(Aggregates.skip(skip));

                    aggregationParams.add(Aggregates.limit(GLOBAL_ITEM_LIMIT));

                    AggregateIterable<Listing> aggregateIterable = TradeCraft.database().getListings().aggregate(aggregationParams);
                    try (MongoCursor<Listing> cursor = aggregateIterable.cursor()) {
                        cursor.forEachRemaining(listings::add);
                    }

                    exchange.getResponseHeaders().add(HttpString.tryFromString("Content-Type"), "application/json");
                    exchange.getResponseSender().send(WebSerializer.singleton.toJson(listings));
                    exchange.getResponseSender().close();
                })
                // Fetch specific listing, with more information
                .add("GET", "/l/fetch", exchange -> {
                    Map<String, Deque<String>> query = exchange.getQueryParameters();
                    if(!query.containsKey("id")){
                        exchange.setStatusCode(400);
                        exchange.getResponseSender().send("Specify the id in the query params");
                        exchange.getResponseSender().close();
                        return;
                    }

                    String id = query.get("id").getFirst();
                    Listing listing = Listing.findListing(id);

                    if(listing == null){
                        exchange.setStatusCode(404);
                        exchange.getResponseSender().close();
                        return;
                    }

                    exchange.getResponseHeaders().add(HttpString.tryFromString("Content-Type"), "application/json");
                    exchange.getResponseSender().send(WebSerializer.singleton.toJson(listing));
                    exchange.getResponseSender().close();
                })
                // Fetch all vendors (with pagination again)
                .add("GET", "/v/all", exchange -> {

                })
                // Fetch specific vendor
                .add("GET", "/v/fetch", exchange -> {

                })
                // 404 handler
                .setFallbackHandler(exchange -> {
                    exchange.setStatusCode(404);
                    exchange.getResponseSender().close();
                })
                .handleRequest(httpServerExchange);
    }
}
