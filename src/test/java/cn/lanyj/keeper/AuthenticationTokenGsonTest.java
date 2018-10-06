package cn.lanyj.keeper;

import java.util.UUID;

import com.google.gson.Gson;

import cn.lanyj.keeper.api.controller.ResponseModel;
import cn.lanyj.keeper.auth.AuthenticationService;
import cn.lanyj.keeper.auth.AuthenticationToken;
import cn.lanyj.keeper.models.User;

public class AuthenticationTokenGsonTest {
	
	public static void main(String[] args) {
		Gson gson = new Gson();
		AuthenticationToken token = new AuthenticationToken("Hello world!");
		token.put("A", "a").put("B", "b").put("C", "c");
		System.out.println(gson.toJson(token));
		token = gson.fromJson(gson.toJson(token), AuthenticationToken.class);
		
		ResponseModel model = new ResponseModel();
		model.success();
		model.put("x", new Integer(45));
		model.put("y", token);
		System.out.println(gson.toJson(model));
		
		User user = new User();
		user.setId(UUID.randomUUID().toString());
		user.setEmail("lanyj@mail.ustc.edu.cn");
		AuthenticationToken authenticationToken = AuthenticationService.generateAuthenticationToken(user);
		String stoken = AuthenticationService.authenticationToString(authenticationToken);
		System.out.println(stoken);
		System.out.println(AuthenticationService.isAuthentication(stoken));
	}
	
}
