package csdev;

import java.io.Serializable;

public class MessageCheckChat extends Message implements Serializable {

	private static final long serialVersionUID = 1L;

	public MessageCheckChat() {
		super(Protocol.CMD_CHECK_CHAT);
	}
}
