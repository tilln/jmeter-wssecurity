package nz.co.breakpoint.jmeter.modifiers;

import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.message.WSSecEncrypt;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.w3c.dom.Document;

public class WSSEncryptionPreProcessor extends AbstractWSSecurityPreProcessor { 

	private static final long serialVersionUID = 1L;

	static {
		keyIdentifiers.put("Binary Security Token",   		WSConstants.BST_DIRECT_REFERENCE);
		keyIdentifiers.put("Issuer Name and Serial Number",	WSConstants.ISSUER_SERIAL);
		keyIdentifiers.put("X509 Certificate",				WSConstants.X509_KEY_IDENTIFIER);
		keyIdentifiers.put("Subject Key Identifier",		WSConstants.SKI_KEY_IDENTIFIER);
		keyIdentifiers.put("Thumbprint SHA1 Identifier",	WSConstants.THUMBPRINT_IDENTIFIER);
		keyIdentifiers.put("EncryptedKeySHA1",  			WSConstants.ENCRYPTED_KEY_SHA1_IDENTIFIER);
	}
	
	static final String[] keyEncryptionAlgorithms = new String[]{
		WSConstants.KEYTRANSPORT_RSA15,
		WSConstants.KEYTRANSPORT_RSAOEP,
	};

	static final String[] symmetricEncryptionAlgorithms = new String[]{
		WSConstants.TRIPLE_DES,
		WSConstants.AES_128, 
		WSConstants.AES_192, 
		WSConstants.AES_256, 
	};
	
	public WSSEncryptionPreProcessor() throws ParserConfigurationException {
		super();
		secBuilder = new WSSecEncrypt();
	}

	@Override
	protected Document build(Document document, Crypto crypto, WSSecHeader secHeader) throws WSSecurityException {
		return ((WSSecEncrypt)secBuilder).build(document, crypto, secHeader);
	}
	
	// Accessors
	public String getSymmetricEncryptionAlgorithm() {
		return ((WSSecEncrypt)secBuilder).getSymmetricEncAlgorithm();
	}

	public void setSymmetricEncryptionAlgorithm(String algorithm) {
		((WSSecEncrypt)secBuilder).setSymmetricEncAlgorithm(algorithm);
	}

	public String getKeyEncryptionAlgorithm() {
		return ((WSSecEncrypt)secBuilder).getKeyEncAlgo();
	}

	public void setKeyEncryptionAlgorithm(String algorithm) {
		((WSSecEncrypt)secBuilder).setKeyEncAlgo(algorithm);
	}
	
	public boolean getCreateEncryptedKey() {
		return ((WSSecEncrypt)secBuilder).isEncryptSymmKey();
	}

	public void setCreateEncryptedKey(boolean createEncryptedKey) {
		((WSSecEncrypt)secBuilder).setEncryptSymmKey(createEncryptedKey);
	}
}