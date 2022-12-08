package csdev;

import java.io.Serializable;

/**
 * <p>
 * MessageLetterResult class
 * 
 * @author Sergey Gutnikov
 * @version 1.0
 */
public class MessageSendResult extends MessageResult implements Serializable {

	private static final long serialVersionUID = 1L;

	public MessageSendResult(String errorMessage) { // Error

		super(Protocol.CMD_SEND, errorMessage);
	}

	public MessageSendResult() { // No errors

		super(Protocol.CMD_SEND);
	}
}