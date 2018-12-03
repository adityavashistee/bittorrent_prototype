package com.p2p.man.message;
public enum MessageTypeEnum{
		CHOKE ((byte) 0),
		UNCHOKE ((byte) 1),
		INTERESTED ((byte) 2),
		NOT_INTERESTED ((byte) 3),
		HAVE ((byte) 4),
		BIT_FIELD ((byte) 5),
		REQUEST ((byte) 6),
		PIECE ((byte) 7),
	    HANDSHAKE ((byte) 8);
		
		final byte messageTypeCode;
		MessageTypeEnum(Byte messageTypeCode){
			this.messageTypeCode = messageTypeCode;
		}
		
		public static MessageTypeEnum getMessageTypeFromCode(byte messageTypeCode) {
			MessageTypeEnum[] allMsgTypes = MessageTypeEnum.values();
			MessageTypeEnum t = null;
			for(int iter = 0; iter < allMsgTypes.length; iter++){
				MessageTypeEnum type = allMsgTypes[iter];
				if(type.messageTypeCode == messageTypeCode){
					t = type;
					break;
				}
			}
			return t;
		}
		
		public byte getMessageTypeCode() {
			return this.messageTypeCode;
		}
		
}

