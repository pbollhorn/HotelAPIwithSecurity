package app.daos;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;

import app.config.HibernateConfig;
import app.dtos.HotelDto;
import app.exceptions.DaoException;
import app.exceptions.IdNotFoundException;

class HotelDaoTest
{
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private static final HotelDao hotelDao = HotelDao.getInstance(emf);
    private static final RoomDao roomDao = RoomDao.getInstance(emf);
    private static HotelDto h1;
    private static HotelDto h2;

    @BeforeEach
    void setUp()
    {
        try (EntityManager em = emf.createEntityManager())
        {

            // Delete everything from tables and reset id's to start with 1
            em.getTransaction().begin();
            em.createNativeQuery("DELETE FROM Room").executeUpdate();
            em.createNativeQuery("DELETE FROM Hotel").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE room_id_seq RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE hotel_id_seq RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();

            // Populate database with the two test hotels, including their rooms
            HotelDto[] hotelDtos = app.populators.HotelPopulator.populate(hotelDao);
            h1 = hotelDtos[0];
            h2 = hotelDtos[1];
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void tearDown()
    {
        if (emf != null && emf.isOpen())
        {
            emf.close();
            System.out.println("EntityManagerFactory closed.");
        }
    }

    @Test
    void getAll() throws Exception
    {
        List<HotelDto> hotelDtos = hotelDao.getAll();
        assertEquals(2, hotelDtos.size());
        assertThat(hotelDtos.get(0), samePropertyValuesAs(h1));
        assertThat(hotelDtos.get(1), samePropertyValuesAs(h2));
    }

    @Test
    void get() throws Exception
    {
        HotelDto h = hotelDao.get(1);
        assertEquals(1, h.id());
        assertEquals("Hotel 1", h.name());
        assertEquals("Address 1", h.address());

        // Another way of doing the above
        assertNotNull(h);
        assertThat(h, samePropertyValuesAs(h1));

        // Negative tests
        assertThrows(IdNotFoundException.class, () -> hotelDao.get(0));
        assertThrows(IdNotFoundException.class, () -> hotelDao.get(-1));
        assertThrows(IdNotFoundException.class, () -> hotelDao.get(17));

    }

    @Test
    void create() throws Exception
    {
        HotelDto h = hotelDao.create(new HotelDto("Hotel 3", "Address 3"));
        assertEquals(3, h.id());
        assertEquals("Hotel 3", h.name());
        assertEquals("Address 3", h.address());

        // Negative tests
        assertThrows(DaoException.class, () -> hotelDao.create(null));
        assertThrows(DaoException.class, () -> hotelDao.create(new HotelDto(null, "Address 4")));
        assertThrows(DaoException.class, () -> hotelDao.create(new HotelDto("Hotel 4", null)));
    }


    @Test
    void update() throws Exception
    {
        HotelDto newH1 = hotelDao.update(1, new HotelDto("Updated Hotel", "Updated Address"));
        assertEquals(1, newH1.id());
        assertEquals("Updated Hotel", newH1.name());
        assertEquals("Updated Address", newH1.address());

        // Check that the hotel is still associated with the same number of rooms
        assertEquals(5, roomDao.getAllByHotelId(1).size());

        // Negative tests: Bad id
        assertThrows(IdNotFoundException.class, () -> hotelDao.update(0, new HotelDto("Updated Hotel", "Updated Address")));
        assertThrows(IdNotFoundException.class, () -> hotelDao.update(0, null));

        // Negative tests: Good id, bad hotelDto
        assertThrows(DaoException.class, () -> hotelDao.update(1, null));
        assertThrows(DaoException.class, () -> hotelDao.update(1, new HotelDto(null, "Updated Address")));
        assertThrows(DaoException.class, () -> hotelDao.update(1, new HotelDto("Updated Hotel", null)));
    }

    @Test
    void delete() throws Exception
    {
        HotelDto h = hotelDao.delete(1);
        assertThat(h, samePropertyValuesAs(h1));
        assertEquals(1, hotelDao.getAll().size());

        // Negative test
        assertThrows(IdNotFoundException.class, () -> hotelDao.delete(0));

    }

}