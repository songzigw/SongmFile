package cn.songm.file.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import cn.songm.common.web.CookieUtils;
import cn.songm.sso.entity.Session;

public class Browser {

	public static final String HEADER_SESSIONID_KEY = "Songmsso-Sessionid";
	
    /**
     * 获取客户端唯一Id
     * 
     * @return
     */
    public static String getSessionId(HttpServletRequest request) {
        Cookie c = CookieUtils.getCookieByName(request,
                    Session.USER_SESSION_KEY);
        String sessionId = null;
        if (c != null) {
            sessionId = c.getValue();
        }
        if (sessionId == null) {
            sessionId = request.getParameter(Session.USER_SESSION_KEY);
        }
        if (sessionId == null) {
        	sessionId = request.getHeader(HEADER_SESSIONID_KEY);
        }
        return sessionId;
    }
}
