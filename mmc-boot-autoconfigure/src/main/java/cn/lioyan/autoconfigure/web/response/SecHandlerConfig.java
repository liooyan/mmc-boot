package cn.lioyan.autoconfigure.web.response;

import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link SecHandlerConfig}
 *
 * @author cn.lioyan
 * @since 2022/4/28 15:52
 */
public class SecHandlerConfig extends WebMvcConfigurationSupport {

    @Override
    protected RequestMappingHandlerAdapter createRequestMappingHandlerAdapter() {
        RequestMappingHandlerAdapter adapter = super.createRequestMappingHandlerAdapter();
        List<HandlerMethodReturnValueHandler> myHandlerList = new ArrayList<>();
        //处理FileInfo
        myHandlerList.add(new FileInfoResponseBodyReturnValueHandler());
        adapter.setReturnValueHandlers(myHandlerList);
        return adapter;
    }
}