package app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.daos.HotelDao;
import app.dtos.HotelDto;

public class HotelControllerTest
{
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    private static final HotelDao hotelDao = HotelDao.getInstance(emf);
    private static HotelDto h1;
    private static HotelDto h2;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setupAll()
    {
        ApplicationConfig
                .getInstance()
                .initiateServer()
                .setRoute(Routes.getRoutes())
                .handleException()
                .autoShutdown()
                .startServer(7777);

        RestAssured.baseURI = "http://localhost:7777/api";
    }

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
    void getAll()
    {
        given()
                .when()
                .get("/hotel")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2));
    }

    @Test
    void get()
    {
        given()
                .when()
                .get("/hotel/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("Hotel 1"))
                .body("address", equalTo("Address 1"));

        // Negative test: Test with id that does not exist
        given()
                .when()
                .get("/hotel/0")
                .then()
                .statusCode(404);

        // Negative test: Test with id that is not a number
        given()
                .when()
                .get("/hotel/notnumber")
                .then()
                .statusCode(400);
    }


    @Test
    void create() throws Exception
    {
        HotelDto h = new HotelDto("New Hotel", "New Address");
        String json = objectMapper.writeValueAsString(h);
        given().when()
                .body(json)
                .post("/hotel")
                .then()
                .statusCode(200)
                .body("id", equalTo(3))
                .body("name", equalTo(h.name()))
                .body("address", equalTo(h.address()));

        // Negative test: Request body is just empty json
        given().when()
                .body("{}")
                .post("/hotel")
                .then()
                .statusCode(400)
                .body("code", equalTo(400));

        // Negative test: Request body is only hotel name
        given().when()
                .body("{\"name\": \"New Hotel\"}")
                .post("/hotel")
                .then()
                .statusCode(400)
                .body("code", equalTo(400));

        // Negative test: Request body is only hotel address
        given().when()
                .body("{\"address\": \"New Address\"}")
                .post("/hotel")
                .then()
                .statusCode(400)
                .body("code", equalTo(400));

        // Negative test: Hotel name is too long
        String tooLongString = "s".repeat(256);
        h = new HotelDto(tooLongString, "New Address");
        json = objectMapper.writeValueAsString(h);
        given().when()
                .body(json)
                .post("/hotel")
                .then()
                .statusCode(400)
                .body("code", equalTo(400));

        // Negative test: Hotel address is too long
        h = new HotelDto("New Hotel", tooLongString);
        json = objectMapper.writeValueAsString(h);
        given().when()
                .body(json)
                .post("/hotel")
                .then()
                .statusCode(400)
                .body("code", equalTo(400));

        // Negative test: Request body is just empty string
        // TODO: The code should be fixed so return code will be 400
        given().when()
                .body("")
                .post("/hotel")
                .then()
                .statusCode(400);
//                .body("code", equalTo("500"));

    }

    @Test
    void update() throws Exception
    {
        HotelDto h = new HotelDto("Updated Hotel", "Updated Hotel");
        String json = objectMapper.writeValueAsString(h);
        given().when()
                .body(json)
                .put("/hotel/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo(h.name()))
                .body("address", equalTo(h.address()));

        // Negative test: Test with id that does not exist
        given().when()
                .body(json)
                .put("/hotel/0")
                .then()
                .statusCode(404)
                .body("code", equalTo(404));

        // Negative test: Test with id that is not a number
        given()
                .when()
                .delete("/hotel/notnumber")
                .then()
                .statusCode(400)
                .body("code", equalTo(400));
    }

    @Test
    void delete()
    {
        given()
                .when()
                .delete("/hotel/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("Hotel 1"))
                .body("address", equalTo("Address 1"));

        // Negative test: Try to delete hotel 1 again
        given()
                .when()
                .delete("/hotel/1")
                .then()
                .statusCode(404)
                .body("code", equalTo(404));

        // Negative tests: Try to delete some hotels that have never existed
        given()
                .when()
                .delete("/hotel/0")
                .then()
                .statusCode(404)
                .body("code", equalTo(404));
        given()
                .when()
                .delete("/hotel/-1")
                .then()
                .statusCode(404)
                .body("code", equalTo(404));
        given()
                .when()
                .delete("/hotel/17")
                .then()
                .statusCode(404)
                .body("code", equalTo(404));

        // Negative test: Try to delete hotel with id that is not a number
        given()
                .when()
                .delete("/hotel/notnumber")
                .then()
                .statusCode(400)
                .body("code", equalTo(400));
    }
}
