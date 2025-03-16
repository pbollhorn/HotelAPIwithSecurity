package app.dtos;

import app.entities.Room;

public record RoomDto(Integer id, Integer hotelId, String roomNumber, Double pricePerNightDkk)
{

    public RoomDto(Room room)
    {
        this(room.getId(), room.getHotel().getId(), room.getRoomNumber(), room.getPricePerNightDkk());
    }

}