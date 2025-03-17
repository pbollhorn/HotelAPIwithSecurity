package app.security.controllers;

import java.util.Set;

import io.javalin.http.Handler;
import io.javalin.security.RouteRole;
import dk.bugelhartmann.UserDTO;

/**
 * Purpose: To handle security in the API
 * Author: Thomas Hartmann
 */
public interface ISecurityController
{
    Handler login(); // to get a token

    Handler register(); // to get a user

    Handler authenticate(); // to verify roles inside token

    boolean authorize(UserDTO userDTO, Set<RouteRole> allowedRoles); // to verify user roles

    String createToken(UserDTO user) throws Exception;

    UserDTO verifyToken(String token) throws Exception;
}
