package cn.songm.file.web;

import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.google.gson.GsonBuilder;

import cn.songm.common.utils.DateTypeAdapter;
import cn.songm.common.utils.JsonUtils;

public class InitServlet extends HttpServlet {

    private static final long serialVersionUID = 2393419970664601285L;

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new DateTypeAdapter());
        JsonUtils.init(builder);
    }

}
