package app.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.config.JavalinConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.javalin.apibuilder.ApiBuilder.path;

import app.exceptions.ApiException;

public class ApplicationConfig
{
    private static ApplicationConfig applicationConfig;
    private static Javalin app;
    private static JavalinConfig javalinConfig;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    private ApplicationConfig()
    {
    }

    public static ApplicationConfig getInstance()
    {
        if (applicationConfig == null)
        {
            applicationConfig = new ApplicationConfig();
        }
        return applicationConfig;
    }

    public ApplicationConfig initiateServer()
    {
        app = Javalin.create(config -> {
            javalinConfig = config;
            config.http.defaultContentType = "application/json";
            config.router.contextPath = "/api";
            config.bundledPlugins.enableRouteOverview("/routes");
            config.bundledPlugins.enableDevLogging();
        });
        return applicationConfig;
    }

    public ApplicationConfig setRoute(EndpointGroup routes)
    {
        javalinConfig.router.apiBuilder(() -> {
            path("/", routes);
        });
        return applicationConfig;
    }

    public ApplicationConfig startServer(int port)
    {
        app.start(port);
        return applicationConfig;
    }

    public ApplicationConfig handleException()
    {
        //StreamReadException
        //UnrecognizedPropertyException
        //JsonParseException

        app.exception(JacksonException.class, (e, ctx) -> {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("code", 400);
            node.put("msg", "Bad request");
            ctx.status(400);
            ctx.json(node);
            logger.warn("400 - Bad request");
        });
        app.exception(ApiException.class, (e, ctx) -> {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("code", e.getCode());
            node.put("msg", e.getMessage());
            ctx.status(e.getCode());
            ctx.json(node);
            logger.warn("{} - {}", e.getCode(), e.getMessage());
        });
        app.exception(Exception.class, (e, ctx) -> {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("code", "500");
            node.put("msg", "Internal server error");
            ctx.status(500);
            ctx.json(node);
            logger.error("500 - Internal server error");
        });
        return applicationConfig;
    }


    // My own method for auto shutdown, based on code from Javalin documentation:
    // https://javalin.io/documentation#server-setup
    public ApplicationConfig autoShutdown()
    {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            app.stop();
        }));

        return applicationConfig;
    }

}