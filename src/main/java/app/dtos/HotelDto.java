package app.dtos;

import app.entities.Hotel;

public record HotelDto(Integer id, String name, String address)
{

    public HotelDto(String name, String address)
    {
        this(null, name, address);
    }

    public HotelDto(Hotel hotel)
    {
        this(hotel.getId(), hotel.getName(), hotel.getAddress());
    }

}