package nz.co.breakpoint.jmeter.modifiers;

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

	transient private WSSecEncrypt secBuilder;

	/* Currently supported attributes are listed below.
	 * The first value for each will be displayed in the GUI as default.
	 */
	static final String[] keyIdentifiers = new String[]{
		getKeyIdentifierLabelForType(WSConstants.BST_DIRECT_REFERENCE),
		getKeyIdentifierLabelForType(WSConstants.ISSUER_SERIAL),
		getKeyIdentifierLabelForType(WSConstants.X509_KEY_IDENTIFIER),
		getKeyIdentifierLabelForType(WSConstants.SKI_KEY_IDENTIFIER),
		getKeyIdentifierLabelForType(WSConstants.THUMBPRINT_IDENTIFIER),
		getKeyIdentifierLabelForType(WSConstants.ENCRYPTED_KEY_SHA1_IDENTIFIER),
	};

	static final String[] keyEncryptionAlgorithms = new String[]{
		WSConstants.KEYTRANSPORT_RSAOEP,
		WSConstants.KEYTRANSPORT_RSA15,
		WSConstants.KEYTRANSPORT_RSAOAEP_XENC11,
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
	protected void initSecBuilder() {
		secBuilder = new WSSecEncrypt();
	}

	@Override
	protected WSSecBase getSecBuilder() {
		return secBuilder;
	}

	@Override
	protected Document build(Document document, Crypto crypto, WSSecHeader secHeader) throws WSSecurityException {
		return secBuilder.build(document, crypto, secHeader);
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

	/* This getter/setter pair seems to be required for the bean introspector when building the GUI,
	 * otherwise the parent class property will be overwritten when building child class GUIs.
	 */
	public String getKeyIdentifier() {
		return super.getKeyIdentifier();
	}

	public void setKeyIdentifier(String keyIdentifier) {
		super.setKeyIdentifier(keyIdentifier);
	}
}
