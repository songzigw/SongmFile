package cn.songm.file.web;

import javax.annotation.Resource;

import cn.songm.acc.entity.User;
import cn.songm.common.utils.JsonUtils;
import cn.songm.common.web.BaseController;
import cn.songm.common.web.Browser;
import cn.songm.sso.service.SongmSSOService;

public class FileBaseController extends BaseController {

    @Resource(name = "songmSsoService")
    protected SongmSSOService songmSsoService;
    //@Resource(name = "userService")
    //protected UserService userService;
    
    protected String getSessionId() {
    	return Browser.getSessionId(this.getRequest());
    }
    
    protected User getSessionUser() {
        String sessionId = Browser.getSessionId(this.getRequest());
        String userJson = songmSsoService.getUserInfo(sessionId);
        if (userJson == null) return null;
        return JsonUtils.getInstance().fromJson(userJson, User.class);
    }
    
}
