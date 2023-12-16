package com.campus.postgres.user.routes;

import com.campus.postgres.base.routes.BaseRoutes;

public class UserRoutes {
    public final static String ROOT = BaseRoutes.API + "/user";
    public final static String REGISTRATION = BaseRoutes.NOT_SECURED + "/api/v1/registration";
    public final static String BY_ID = ROOT + "/{id}";
    public final static String SEARCH = ROOT;
    public final static String EDIT = ROOT;
    public final static String DELETE = BY_ID;
    public final static String TEST = BaseRoutes.NOT_SECURED + "/test";
    public final static String INIT = BaseRoutes.NOT_SECURED + "/init";

}
