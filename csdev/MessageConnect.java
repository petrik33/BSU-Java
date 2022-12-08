package csdev;

import java.io.Serializable;

/**
 * <p>
 * MessageConnect class
 * 
 * @author Sergey Gutnikov
 * @version 1.0
 */
public class MessageConnect extends Message implements Serializable {

	private static final long serialVersionUID = 1L;

	public String userNickName;
	public String userFullName;

	public MessageConnect(String userNickName, String userFullName) {
		super(Protocol.CMD_CONNECT);
		this.userNickName = userNickName;
		this.userFullName = userFullName;
	}

}