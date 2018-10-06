package cn.lanyj.keeper.api.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cn.lanyj.keeper.auth.AuthenticationService;
import cn.lanyj.keeper.auth.AuthenticationToken;
import cn.lanyj.keeper.models.Email;
import cn.lanyj.keeper.models.User;
import cn.lanyj.keeper.repositories.UserRepository;
import cn.lanyj.keeper.services.CryptoService;
import cn.lanyj.keeper.services.EmailService;

@RestController
@RequestMapping("/user")
@PropertySource({"classpath:app.properties"})
public class UserController {
	
	@Autowired
	UserRepository repository;
	
	@Autowired
	CryptoService cryptoService;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	Environment env;
	
	@ResponseBody
	@RequestMapping("/login")
	public ResponseModel login(@RequestParam String email, @RequestParam String password) {
		ResponseModel model = new ResponseModel();
		User user = repository.findUserByEmail(email);
		if(user == null || !user.getPassword().equals(cryptoService.toHash256(password))) {
			return model.error("email or password invalidated.");
		}
		AuthenticationToken authenticationToken = AuthenticationService.generateAuthenticationToken(user);
		return model.success().put(AuthenticationService.AUTHENTICATION_PART, AuthenticationService.authenticationToString(authenticationToken))
				.put("email", email);
	}
	
	@ResponseBody
	@RequestMapping("/refresh")
	public ResponseModel refresh(AuthenticationToken token) {
		ResponseModel model = new ResponseModel();
		token.refresh();
		return model.success().put(AuthenticationService.AUTHENTICATION_PART, AuthenticationService.authenticationToString(token))
				.put("email", repository.findUserById(token.getUserId()).getEmail());
	}
	
	@ResponseBody
	@RequestMapping("/register")
	public ResponseModel register(@RequestParam String email, @RequestParam String password, @RequestParam String redirectUrl, HttpServletRequest request) throws UnsupportedEncodingException {
		email = email.trim();
		password = password.trim();
		ResponseModel model = new ResponseModel();
		if(!EmailValidator.getInstance(true).isValid(email)) {
			return model.error("mail is not validated.");
		}
		if(password.length() < 6) {
			return model.error("password length cannot less than 6 letters");
		}
		User user = repository.findUserByEmail(email);
		if(user != null) {
			return model.error("mail is alerdy in use.");
		}
		user = new User();
		Email mail = new Email();
		mail.setFrom(env.getProperty("mail.sender"));
		mail.setTo(email);
		mail.setSubject("注册用户");
		mail.setContent(getRegisterMailContent(user, email, cryptoService.toHash256(password), URLDecoder.decode(redirectUrl, "utf-8"), request));
		emailService.postSend(mail);
		return model.success();
	}
	
	@RequestMapping("/register2")
	public void register2(AuthenticationToken token, HttpServletResponse response) throws IOException {
		if(!token.containsKey("register_user")) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "permission denied.");
			return;
		}
		// check user has been registed.
		if(repository.findUserByEmail(token.get("email").toString()) == null) {
			User user = new User();
			user.setEmail(token.get("email").toString());
			user.setPassword(token.get("password").toString());
			user.setRole(User.ROLE_USER);
			repository.save(user);
		}
		response.sendRedirect(token.get("redirectUrl").toString());
	}
	
	@ResponseBody
	@RequestMapping("/forget")
	public ResponseModel forget(@RequestParam String email, @RequestParam String password, @RequestParam String redirectUrl, HttpServletRequest request) throws UnsupportedEncodingException {
		email = email.trim();
		password = password.trim();
		ResponseModel model = new ResponseModel();
		if(!EmailValidator.getInstance(true).isValid(email)) {
			return model.error("mail is not validated.");
		}
		if(password.length() < 6) {
			return model.error("password length cannot less than 6 letters");
		}
		User user = repository.findUserByEmail(email);
		if(user == null) {
			return model.error("mail is not in use.");
		}
		Email mail = new Email();
		mail.setFrom(env.getProperty("mail.sender"));
		mail.setTo(email);
		mail.setSubject("找回密码");
		mail.setContent(getForgetMailContent(user, cryptoService.toHash256(password), URLDecoder.decode(redirectUrl, "utf-8"), request));
		emailService.postSend(mail);
		return model.success();
	}
	
	@RequestMapping("/forget2")
	public void forget2(AuthenticationToken token, HttpServletResponse response) throws IOException {
		if(!token.containsKey("change_password")) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "permission denied.");
			return;
		}
		User user = repository.findUserById(token.getUserId());
		if(user == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "user not found.");
			return;
		}
		user.setPassword(token.get("password").toString());
		repository.flush();
		response.sendRedirect(token.get("redirectUrl").toString());
	}
	
	@ResponseBody
	@RequestMapping("/update")
	public ResponseModel update(AuthenticationToken token, @RequestParam String email, @RequestParam String password, @RequestParam String redirectUrl, HttpServletRequest request) throws UnsupportedEncodingException {
		email = email.trim();
		password = password.trim();
		ResponseModel model = new ResponseModel();
		if(!EmailValidator.getInstance(true).isValid(email)) {
			return model.error("mail is not validated.");
		}
		if(password.length() < 6) {
			return model.error("password length cannot less than 6 letters");
		}
		User user = repository.findUserById(token.getUserId());
		if(user == null) {
			return model.error("user not found.");
		}
		Email mail = new Email();
		mail.setFrom(env.getProperty("mail.sender"));
		mail.setTo(email);
		mail.setSubject("用户更新");
		mail.setContent(getUpdateMailContent(user, email, cryptoService.toHash256(password), URLDecoder.decode(redirectUrl, "utf-8"), request));
		emailService.postSend(mail);
		return model.success();
	}
	
	@ResponseStatus(value=HttpStatus.OK)
	@RequestMapping("/update2")
	public void update2(AuthenticationToken token, HttpServletResponse response) throws IOException {
		if(!token.containsKey("update_user")) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "premission denied.");
			return;
		}
		User user = repository.findUserById(token.getUserId());
		if(user == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "user not found.");
			return;
		}
		if(!token.containsKey("email") && !token.containsKey("password")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "email or password illegal.");
			return;
		}
		user.setEmail(token.get("email").toString());
		user.setPassword(token.get("password").toString());
		repository.flush();
		response.sendRedirect(token.get("redirectUrl").toString());
	}
	
	private String getRegisterMailContent(User user, String email, String password, String redirectUrl, HttpServletRequest request) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
		StringBuilder sb = new StringBuilder();
		sb.append("敬爱的keeper用户，您好！\n");
		sb.append("  您于");
		sb.append(sdf.format(new Date()));
		sb.append("进行注册。请在十五分钟内点击下面链接：\n");
		sb.append(request.getScheme());
		sb.append("://");
		sb.append(request.getServerName());
		sb.append(':');
		sb.append(request.getServerPort());
		sb.append("/ukeeper/");
		// http://keeper.lanyj.cn/
		sb.append("user/register2?");
		sb.append(AuthenticationService.AUTHENTICATION_PART);
		sb.append('=');
		AuthenticationToken token = AuthenticationService.generateAuthenticationToken(user);
		token.put("register_user", true);
		token.put("email", email);
		token.put("password", password);
		token.put("redirectUrl", redirectUrl);
		sb.append(AuthenticationService.authenticationToString(token));
		return sb.toString();
	}
	
	private String getUpdateMailContent(User user, String email, String password, String redirectUrl, HttpServletRequest request) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
		StringBuilder sb = new StringBuilder();
		sb.append("敬爱的keeper用户，您好！\n");
		sb.append("  您于");
		sb.append(sdf.format(new Date()));
		sb.append("进行用户更新操作。请在十五分钟内点击下面链接：\n");
		sb.append(request.getScheme());
		sb.append("://");
		sb.append(request.getServerName());
		sb.append(':');
		sb.append(request.getServerPort());
		sb.append("/ukeeper/");
		// http://keeper.lanyj.cn/
		sb.append("user/update2?");
		sb.append(AuthenticationService.AUTHENTICATION_PART);
		sb.append('=');
		AuthenticationToken token = AuthenticationService.generateAuthenticationToken(user);
		token.put("update_user", true);
		token.put("email", email);
		token.put("password", password);
		token.put("redirectUrl", redirectUrl);
		sb.append(AuthenticationService.authenticationToString(token));
		return sb.toString();
	}
	
	private String getForgetMailContent(User user, String password, String redirectUrl, HttpServletRequest request) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
		StringBuilder sb = new StringBuilder();
		sb.append("敬爱的keeper用户，您好！\n");
		sb.append("  您于");
		sb.append(sdf.format(new Date()));
		sb.append("进行找回密码操作。请在十五分钟内点击下面链接：\n");
		sb.append(request.getScheme());
		sb.append("://");
		sb.append(request.getServerName());
		sb.append(':');
		sb.append(request.getServerPort());
		sb.append("/ukeeper/");
		// http://keeper.lanyj.cn/
		sb.append("user/forget2?");
		sb.append(AuthenticationService.AUTHENTICATION_PART);
		sb.append('=');
		AuthenticationToken token = AuthenticationService.generateAuthenticationToken(user);
		token.put("change_password", true);
		token.put("password", password);
		token.put("redirectUrl", redirectUrl);
		sb.append(AuthenticationService.authenticationToString(token));
		return sb.toString();
	}
}
