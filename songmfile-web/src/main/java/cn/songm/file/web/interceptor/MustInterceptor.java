package cn.songm.file.web.interceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.songm.common.web.CookieUtils;
import cn.songm.file.web.Browser;
import cn.songm.sso.entity.Session;
import cn.songm.sso.service.SongmSSOService;

public class MustInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(MustInterceptor.class);

    @Resource(name = "songmSsoService")
    private SongmSSOService songmSsoService;

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        LOG.info("URI: {}", request.getRequestURI());

        Session session = songmSsoService.report(Browser.getSessionId(request));
        CookieUtils.addCookie(response, Session.USER_SESSION_KEY, session.getSesId(), 0);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {

    }

}
