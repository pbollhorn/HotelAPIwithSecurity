package app.daos;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import app.dtos.RoomDto;
import app.entities.Room;
import app.exceptions.DaoUnexpectedException;

public class RoomDao
{

    private static RoomDao instance;
    private static EntityManagerFactory emf;

    private RoomDao(EntityManagerFactory emf)
    {
        this.emf = emf;
    }

    public static RoomDao getInstance(EntityManagerFactory emf)
    {
        if (instance == null)
        {
            instance = new RoomDao(emf);
        }
        return instance;
    }

    public static RoomDao getInstance()
    {
        return instance;
    }


    public List<RoomDto> getAllByHotelId(int hotelId) throws DaoUnexpectedException
    {

        try (EntityManager em = emf.createEntityManager())
        {

            String jpql = "SELECT r FROM Room r WHERE r.hotel.id=:hotelId ORDER BY id";
            TypedQuery<Room> query = em.createQuery(jpql, Room.class);
            query.setParameter("hotelId", hotelId);
            List<Room> rooms = query.getResultList();

            List<RoomDto> roomDtos = new LinkedList<>();
            for (Room r : rooms)
            {
                roomDtos.add(new RoomDto(r));
            }
            return roomDtos;

        }
        catch (RuntimeException e)
        {
            throw new DaoUnexpectedException("Error reading all rooms for hotel");
        }
    }

}
