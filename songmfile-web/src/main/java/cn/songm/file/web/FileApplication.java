package cn.songm.file.web;

import java.util.Date;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.google.gson.GsonBuilder;

import cn.songm.common.utils.DateTypeAdapter;
import cn.songm.common.utils.JsonUtils;

@Service
public class FileApplication implements ApplicationListener<ApplicationEvent>, InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent) {
			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeAdapter(Date.class, new DateTypeAdapter());
			JsonUtils.init(builder);
		}
	}

}
