package csdev;

import java.io.Serializable;

/**
 * <p>
 * MessageUserResult class
 * 
 * @author Sergey Gutnikov
 * @version 1.0
 */
public class MessageUserResult extends MessageResult implements Serializable {

	private static final long serialVersionUID = 1L;

	public String[] userNickNames = null;

	public MessageUserResult(String errorMessage) { // Error
		super(Protocol.CMD_USER, errorMessage);
	}

	public MessageUserResult(String[] userNickNames) { // No errors
		super(Protocol.CMD_USER);
		this.userNickNames = userNickNames;
	}
}