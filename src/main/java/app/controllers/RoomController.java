package app.controllers;

import java.util.List;

import io.javalin.http.Context;

import app.daos.RoomDao;
import app.dtos.RoomDto;
import app.exceptions.ApiException;

public class RoomController
{

    private static RoomDao roomDao = RoomDao.getInstance();

    public static void getAll(Context ctx) throws Exception
    {
        int hotelId;

        try
        {
            hotelId = Integer.parseInt(ctx.pathParam("id"));
        }
        catch (RuntimeException e)
        {
            throw new ApiException(400, "Bad Request: Malformed id");
        }

        List<RoomDto> roomDtos = roomDao.getAllByHotelId(hotelId);
        ctx.json(roomDtos);


    }

//    private static void getById(Context ctx) {
//
//    }
//
//    private static void create(Context ctx) {
//
//    }
//
//
//    private static void update(Context ctx) {
//
//    }
//
//    private static void delete(Context ctx) {
//
//    }


}
