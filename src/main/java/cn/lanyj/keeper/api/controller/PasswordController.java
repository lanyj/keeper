package cn.lanyj.keeper.api.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.lanyj.keeper.auth.AuthenticationFailedException;
import cn.lanyj.keeper.auth.AuthenticationToken;
import cn.lanyj.keeper.models.Password;
import cn.lanyj.keeper.models.User;
import cn.lanyj.keeper.repositories.PasswordRepository;
import cn.lanyj.keeper.repositories.UserRepository;
import cn.lanyj.keeper.services.CryptoService;
import cn.lanyj.keeper.util.AESCrypto;

@RestController
@RequestMapping("/password")
public class PasswordController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PasswordRepository passwordRepository;
	
	@Autowired
	CryptoService cryptoService;
	
	@ResponseBody
	@RequestMapping("/list/{id}")
	public ResponseModel getPassword(AuthenticationToken token, @PathVariable String id) {
		ResponseModel responseModel = new ResponseModel();
		Password password = passwordRepository.findById(id).get();
		return responseModel.success().put("value", decryptPassword(password.getOwner(), password, true));
	}
	
	@ResponseBody
	@RequestMapping("/list")
	public ResponseModel listPassword(AuthenticationToken token, 
			@RequestParam(name="pageNo", required=false, defaultValue="0") int pageNo,
			@RequestParam(name="pageSize", required=false, defaultValue=Integer.MAX_VALUE + "") int pageSize) {
		ResponseModel responseModel = new ResponseModel();
		User user = userRepository.findUserById(token.getUserId());

		List<Password> tmp = passwordRepository.findPasswordByOwner(userRepository.findUserById(token.getUserId()), PageRequest.of(pageNo, pageSize));
		List<Password> passwords = new ArrayList<>(tmp.size());
		for(Password password : tmp) {
			passwords.add(decryptPassword(user, password, true));
		}
		return responseModel.success().put("value", passwords);
	}
	
	@ResponseBody
	@RequestMapping("/like")
	public ResponseModel listPasswordByUrlLike(AuthenticationToken token, 
			@RequestParam(name="pageNo", required=false, defaultValue="0") int pageNo,
			@RequestParam(name="pageSize", required=false, defaultValue=Integer.MAX_VALUE + "") int pageSize,
			@RequestParam String url) {
		ResponseModel responseModel = new ResponseModel();
		User user = userRepository.findUserById(token.getUserId());
		List<Password> tmp = passwordRepository.findPasswordByOwnerAndUrlLike(user, "%" + url + "%", PageRequest.of(pageNo, pageSize));
		List<Password> passwords = new ArrayList<>(tmp.size());
		for(Password password : tmp) {
			passwords.add(decryptPassword(user, password, true));
		}
		return responseModel.success().put("value", passwords);
	}
	
	@ResponseBody
	@RequestMapping("/update")
	public ResponseModel updatePassword(AuthenticationToken token, 
			@RequestParam String id,
			@RequestParam String url,
			@RequestParam String content,
			@RequestParam String username,
			@RequestParam String password) {
		ResponseModel model = new ResponseModel();
		Password nt = passwordRepository.findById(id).get();
		if(nt == null || !nt.getOwner().getId().equals(token.getUserId())) {
			return model.error("Password not found.");
		}
		nt.setContent(content);
		nt.setUsername(username);
		nt.setPassword(password);
		nt.setUrl(url);
		encryptPassword(nt.getOwner(), nt, false);
		passwordRepository.flush();
		return model.success();
	}
	
	@ResponseBody
	@RequestMapping("/save")
	public ResponseModel savePassword(AuthenticationToken token, 
			@RequestParam String url,
			@RequestParam String content,
			@RequestParam String username,
			@RequestParam String password) {
		ResponseModel model = new ResponseModel();
		User user = userRepository.findUserById(token.getUserId());
		if(user == null) {
			throw new AuthenticationFailedException("user not found.");
		}
		System.out.println("Content = " + content);
		Password pwd = new Password();
		pwd.setContent(content);
		pwd.setUsername(username);
		pwd.setPassword(password);
		pwd.setUrl(url);
		pwd.setOwner(user);
		passwordRepository.save(encryptPassword(user, pwd, false));
		return model.success();
	}
	
	@ResponseBody
	@RequestMapping("/delete/{id}")
	public ResponseModel deletePassword(AuthenticationToken token, @PathVariable String id) {
		ResponseModel model = new ResponseModel();
		Password password = passwordRepository.findPasswordById(id);
		if(password == null) {
			return model.error("password not found.");
		}
		if(!password.getOwner().getId().equals(token.getUserId())) {
			throw new AuthenticationFailedException("Authentication failed.");
		}
		passwordRepository.delete(password);
		return model.success();
	}
	
	private Password encryptPassword(User user, Password pwd, boolean alloc) {
		Password password = pwd;
		if(alloc) {
			password = new Password();
			try {
				BeanUtils.copyProperties(password, pwd);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		String id = user.getId();
		AESCrypto crypto = new AESCrypto(id);
		password.setUsername(crypto.encrypt(pwd.getUsername()));
		password.setPassword(crypto.encrypt(pwd.getPassword()));
		return password;
	}
	
	private Password decryptPassword(User user, Password pwd, boolean alloc) {
		Password password = pwd;
		if(alloc) {
			password = new Password();
			try {
				BeanUtils.copyProperties(password, pwd);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		String id = user.getId();
		AESCrypto crypto = new AESCrypto(id);
		password.setUsername(crypto.decrypt(pwd.getUsername()));
		password.setPassword(crypto.decrypt(pwd.getPassword()));
		return password;
	}	
	
}
