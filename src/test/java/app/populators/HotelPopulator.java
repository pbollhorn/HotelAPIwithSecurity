package app.populators;

import app.daos.HotelDao;
import app.dtos.HotelDto;
import app.entities.Hotel;
import app.entities.Room;
import app.exceptions.DaoException;

public class HotelPopulator
{
    public static HotelDto[] populate(HotelDao hotelDao) throws DaoException
    {
        Hotel hotel1 = new Hotel(new HotelDto("Hotel 1", "Address 1"));
        hotel1.addRoom(new Room("101", 1000.0));
        hotel1.addRoom(new Room("102", 1000.0));
        hotel1.addRoom(new Room("103", 1000.0));
        hotel1.addRoom(new Room("201", 1500.0));
        hotel1.addRoom(new Room("202", 1500.0));
        HotelDto h1 = hotelDao.createFromEntity(hotel1);

        Hotel hotel2 = new Hotel(new HotelDto("Hotel 2", "Address 2"));
        hotel2.addRoom(new Room("1A", 600.0));
        hotel2.addRoom(new Room("1B", 600.0));
        hotel2.addRoom(new Room("1C", 600.0));
        hotel2.addRoom(new Room("2A", 1800.0));
        HotelDto h2 = hotelDao.createFromEntity(hotel2);

        return new HotelDto[]{h1, h2};
    }

}