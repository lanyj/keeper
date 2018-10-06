package cn.lanyj.keeper.execption;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lanyj.keeper.api.controller.ResponseModel;
import cn.lanyj.keeper.auth.AuthenticationFailedException;

@ControllerAdvice
public class ExecptionHandler {
	
	@ResponseBody
	@ExceptionHandler(value=AuthenticationFailedException.class)
	public ResponseModel handleAuthenticationFailedException(AuthenticationFailedException afe) {
		ResponseModel model = new ResponseModel();
		return model.error(afe.getMessage());
	}
	
}
