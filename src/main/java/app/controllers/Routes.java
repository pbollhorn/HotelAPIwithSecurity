package app.controllers;

import app.security.enums.Role;
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
                }, Role.USER);

                get("/{id}", ctx -> {
                    HotelController.get(ctx);
                }, Role.USER);

                // New route for getting rooms of a specific hotel by ID
                get("/{id}/room", ctx -> {
                    RoomController.getAll(ctx);
                }, Role.USER);

                post("/", ctx -> {
                    HotelController.create(ctx);
                }, Role.USER);

                put("/{id}", ctx -> {
                    HotelController.update(ctx);
                }, Role.USER);

                delete("/{id}", ctx -> {
                    HotelController.delete(ctx);
                }, Role.USER);


            });
        };

    }
}