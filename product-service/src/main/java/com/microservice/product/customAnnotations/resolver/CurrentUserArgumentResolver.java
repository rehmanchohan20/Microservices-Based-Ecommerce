package com.microservice.product.customAnnotations.resolver;

import com.microservice.product.customAnnotations.CurrentUser;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUser.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        CurrentUser annotation = parameter.getParameterAnnotation(CurrentUser.class);
        String type = annotation.value(); // "id" or "name"

        if ("id".equalsIgnoreCase(type)) {
            Object principal = auth.getPrincipal();
            if (principal instanceof Long) {
                return principal; // ✅ Long userId
            } else if (principal instanceof String) {
                return Long.valueOf((String) principal); // fallback
            }
        } else if ("name".equalsIgnoreCase(type)) {
            Object credentials = auth.getCredentials();
            if (credentials != null) return credentials.toString(); // ✅ username
        }

        return null;
    }
}