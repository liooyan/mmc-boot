package cn.lioyan.autoconfigure.web.response;

import cn.lioyan.autoconfigure.file.FileInfo;
import cn.lioyan.autoconfigure.file.util.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBodyReturnValueHandler;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

/**
 * {@link FileInfoResponseBodyReturnValueHandler}
 * <p>
 * {@link  StreamingResponseBodyReturnValueHandler}
 *
 * @author cn.lioyan
 * @since 2022/4/28 15:48
 */
public class FileInfoResponseBodyReturnValueHandler implements HandlerMethodReturnValueHandler {
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        if (FileInfo.class.isAssignableFrom(returnType.getParameterType())) {
            return true;
        }
        return false;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }

        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        Assert.state(response != null, "No HttpServletResponse");
        ServerHttpResponse outputMessage = new ServletServerHttpResponse(response);
        InputStream inputStream = null;
        if (returnValue instanceof FileInfo) {
            FileInfo fileInfo = (FileInfo) returnValue;
            response.setStatus(HttpServletResponse.SC_OK);
            outputMessage.getHeaders().add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileInfo.getName() + "\"");
            outputMessage.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
            inputStream = ((FileInfo) returnValue).getInputStream();
        }

        ServletRequest request = webRequest.getNativeRequest(ServletRequest.class);
        Assert.state(request != null, "No ServletRequest");
        ShallowEtagHeaderFilter.disableContentCaching(request);

        Assert.isInstanceOf(FileInfo.class, returnValue, "FileInfo expected");

        Callable<Void> callable = new StreamingResponseBodyTask(outputMessage.getBody(), inputStream);
        WebAsyncUtils.getAsyncManager(webRequest).startCallableProcessing(callable, mavContainer);
    }

    private static class StreamingResponseBodyTask implements Callable<Void> {

        private final OutputStream outputStream;

        private final InputStream inputStream;

        public StreamingResponseBodyTask(OutputStream outputStream, InputStream inputStream) {
            this.outputStream = outputStream;
            this.inputStream = inputStream;
        }

        @Override
        public Void call() throws Exception {
            IOUtils.copyBytes(inputStream,outputStream,1024);
            this.outputStream.flush();
            return null;
        }
    }


}
