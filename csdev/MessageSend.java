package csdev;

import java.io.Serializable;

/**
 * <p>
 * MessageLetter class
 * 
 * @author Sergey Gutnikov
 * @version 1.0
 */
public class MessageSend extends Message implements Serializable {

	private static final long serialVersionUID = 1L;

	public String txt;

	public MessageSend(String txt) {
		super(Protocol.CMD_SEND);
		this.txt = txt;
	}

}