package com.campus.postgres.user.routes;

public class UserRoutes {
    public final static String ROOT = "/api/v1/user";
    public final static String CREATE = ROOT;
    public final static String BY_ID = ROOT + "/{id}";
    public final static String SEARCH = ROOT;
    public final static String EDIT = BY_ID;
    public final static String DELETE = BY_ID;

}
