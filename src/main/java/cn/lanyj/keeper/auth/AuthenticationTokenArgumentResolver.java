package cn.lanyj.keeper.auth;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthenticationTokenArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		if (parameter.isOptional() || parameter.getParameterType().isAssignableFrom(AuthenticationToken.class)) {
			return true;
		}
		return false;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		AuthenticationToken token = (AuthenticationToken) webRequest.getAttribute(AuthenticationService.AUTHENTICATION_PART,
				RequestAttributes.SCOPE_REQUEST);
		return token;
	}

}
