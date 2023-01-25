package cn.lioyan.autoconfigure.web.swagger;

import springfox.documentation.RequestHandler;

import java.util.function.Predicate;


/**
 * @author cn.lioyan
 * @since 2022/4/21 14:00
 */
public interface RequestHandlerPredicate {
    Predicate<RequestHandler> get();
}
