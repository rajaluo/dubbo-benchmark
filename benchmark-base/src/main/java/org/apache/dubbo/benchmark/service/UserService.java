package org.apache.dubbo.benchmark.service;

import org.apache.dubbo.benchmark.bean.Page;
import org.apache.dubbo.benchmark.bean.User;

import javax.ws.rs.*;

@Path("/users")
public interface UserService {
    @GET
    @Path("/exist/{email}")
    boolean existUser(@PathParam("email") String email);

    @POST
    @Path("/create")
    boolean createUser(User user);

    @GET
    @Path("/get/{id}")
    User getUser(@PathParam("id") long id);

    @GET
    @Path("/list/{pageNo}")
    Page<User> listUser(@PathParam("pageNo") int pageNo);
}

