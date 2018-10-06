package cn.lanyj.keeper.auth;

public class AuthenticationFailedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4796300923761461824L;
	
	private String msg;
	
	public AuthenticationFailedException(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String getMessage() {
		return msg;
	}
	
}
