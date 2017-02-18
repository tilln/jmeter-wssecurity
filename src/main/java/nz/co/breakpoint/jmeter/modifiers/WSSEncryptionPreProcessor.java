package nz.co.breakpoint.jmeter.modifiers;

import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.message.WSSecBase;
import org.apache.wss4j.dom.message.WSSecEncrypt;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.w3c.dom.Document;

public class WSSEncryptionPreProcessor extends AbstractWSSecurityPreProcessor { 

	private static final long serialVersionUID = 1L;

	transient private WSSecEncrypt secBuilder = new WSSecEncrypt();

	static {
		keyIdentifiers.put("Binary Security Token",         WSConstants.BST_DIRECT_REFERENCE);
		keyIdentifiers.put("Issuer Name and Serial Number", WSConstants.ISSUER_SERIAL);
		keyIdentifiers.put("X509 Certificate",              WSConstants.X509_KEY_IDENTIFIER);
		keyIdentifiers.put("Subject Key Identifier",        WSConstants.SKI_KEY_IDENTIFIER);
		keyIdentifiers.put("Thumbprint SHA1 Identifier",    WSConstants.THUMBPRINT_IDENTIFIER);
		keyIdentifiers.put("EncryptedKeySHA1",              WSConstants.ENCRYPTED_KEY_SHA1_IDENTIFIER);
	}
	
	static final String[] keyEncryptionAlgorithms = new String[]{
		WSConstants.KEYTRANSPORT_RSAOEP,
		WSConstants.KEYTRANSPORT_RSA15,
	};

	static final String[] symmetricEncryptionAlgorithms = new String[]{
		WSConstants.AES_128, 
		WSConstants.AES_192, 
		WSConstants.AES_256, 
		WSConstants.TRIPLE_DES,
	};

	public WSSEncryptionPreProcessor() throws ParserConfigurationException {
		super();
	}

	@Override
	protected WSSecBase getSecBuilder() {
		return secBuilder;
	}

	@Override
	protected Document build(Document document, Crypto crypto, WSSecHeader secHeader) throws WSSecurityException {
		document = secBuilder.build(document, crypto, secHeader);
		secBuilder = new WSSecEncrypt(); // discard old instance and create a fresh one for next iteration, during which the bean properties will be reapplied before processing the sampler
		return document;
	}
	
	// Accessors
	public String getSymmetricEncryptionAlgorithm() {
		return secBuilder.getSymmetricEncAlgorithm();
	}

	public void setSymmetricEncryptionAlgorithm(String algorithm) {
		secBuilder.setSymmetricEncAlgorithm(algorithm);
	}

	public String getKeyEncryptionAlgorithm() {
		return secBuilder.getKeyEncAlgo();
	}

	public void setKeyEncryptionAlgorithm(String algorithm) {
		secBuilder.setKeyEncAlgo(algorithm);
	}
	
	public boolean getCreateEncryptedKey() {
		return secBuilder.isEncryptSymmKey();
	}

	public void setCreateEncryptedKey(boolean createEncryptedKey) {
		secBuilder.setEncryptSymmKey(createEncryptedKey);
	}
}
