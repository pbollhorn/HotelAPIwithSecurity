package app;

import app.dtos.HotelDto;
import jakarta.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.controllers.Routes;
import app.daos.HotelDao;
import app.daos.RoomDao;
import app.entities.Hotel;
import app.entities.Room;
import app.exceptions.DaoException;

public class Main
{

    public static void main(String[] args)
    {
        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.info("""
                \n\n
                ----------------------------------------------------------
                (\\___/)                                            (\\___/)
                (='.'=)               SERVER STARTED               (='.'=)
                (")_(")                                            (")_(")
                ----------------------------------------------------------\n""");

        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        HotelDao hotelDao = HotelDao.getInstance(emf);
        RoomDao roomDao = RoomDao.getInstance(emf);

        // Close EntityManagerFactory when program shuts down
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (emf != null && emf.isOpen())
            {
                emf.close();
                System.out.println("EntityManagerFactory closed.");
            }
        }));


        try
        {
            Hotel h1 = new Hotel(new HotelDto("Mågevejens Hotel", "Mågevej 1, 2400 København NV"));
            h1.addRoom(new Room("101", 1000.0));
            h1.addRoom(new Room("102", 1000.0));
            h1.addRoom(new Room("103", 1000.0));
            h1.addRoom(new Room("201", 1500.0));
            h1.addRoom(new Room("202", 1500.0));
            hotelDao.createFromEntity(h1);

            Hotel h2 = new Hotel(new HotelDto("Byens Hotel", "Bjergbygade 1, 4200 Slagelse"));
            h2.addRoom(new Room("1A", 600.0));
            h2.addRoom(new Room("1B", 600.0));
            h2.addRoom(new Room("1C", 600.0));
            h2.addRoom(new Room("2A", 1800.0));
            hotelDao.createFromEntity(h2);
        }
        catch (DaoException e)
        {
            System.out.println("ERROR POPULATING DATABASE");
            return;
        }

        ApplicationConfig
                .getInstance()
                .initiateServer()
                .setRoute(Routes.getRoutes())
                .handleException()
                .autoShutdown()
                .startServer(7070);


    }

}