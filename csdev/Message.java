package csdev;

import java.io.Serializable;

/**
 * <p>
 * Message base class
 * 
 * @author Sergey Gutnikov
 * @version 1.0
 */
public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	private byte id;

	public byte getID() {
		return id;
	}

	protected Message() {
		assert (false);
	}

	protected Message(byte id) {

		assert (Protocol.validID(id) == true);
		this.id = id;
	}
}
