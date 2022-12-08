package csdev;

import java.io.Serializable;

/**
 * <p>
 * MessageCheckMailResult class
 * 
 * @author Sergey Gutnikov
 * @version 1.0
 */
public class MessageCheckChatResult extends MessageResult implements
		Serializable {

	private static final long serialVersionUID = 1L;

	public String[] letters = null;

	public MessageCheckChatResult(String errorMessage) { // Error
		super(Protocol.CMD_CHECK_CHAT, errorMessage);
	}

	public MessageCheckChatResult(String[] letters) { // No errors
		super(Protocol.CMD_CHECK_CHAT);
		this.letters = letters;
	}

}
