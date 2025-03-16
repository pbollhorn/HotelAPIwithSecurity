package app.controllers;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes
{

    public static EndpointGroup getRoutes()
    {
        return () -> {
            path("hotel", () -> {

                get("/", ctx -> {
                    HotelController.getAll(ctx);
                });

                get("/{id}", ctx -> {
                    HotelController.get(ctx);
                });

                // New route for getting rooms of a specific hotel by ID
                get("/{id}/room", ctx -> {
                    RoomController.getAll(ctx);
                });

                post("/", ctx -> {
                    HotelController.create(ctx);
                });

                put("/{id}", ctx -> {
                    HotelController.update(ctx);
                });

                delete("/{id}", ctx -> {
                    HotelController.delete(ctx);
                });


            });
        };

    }
}