package cn.lanyj.keeper.auth;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthenticationInterceptor implements HandlerInterceptor {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "*");
		response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
		response.addHeader("Access-Control-Max-Age", "3600");
		response.addHeader("Content-type", "application/json");
		
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();
		Class<?>[] clazz = method.getParameterTypes();
		for(Class<?> c : clazz) {
			if(c == AuthenticationToken.class) {
				AuthenticationToken authenticationToken = getAuthenticationToken(request);
				if(authenticationToken == null) {
					throw new AuthenticationFailedException("Token invalidated.");
				} else {
					request.setAttribute(AuthenticationService.AUTHENTICATION_PART, getAuthenticationToken(request));
				}
				break;
			}
		}
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
	
	private AuthenticationToken getAuthenticationToken(HttpServletRequest request) {
		String token = request.getParameter(AuthenticationService.AUTHENTICATION_PART);
		AuthenticationToken authenticationToken = AuthenticationService.getAuthenticationToken(token);
		return authenticationToken;
	}
	
}