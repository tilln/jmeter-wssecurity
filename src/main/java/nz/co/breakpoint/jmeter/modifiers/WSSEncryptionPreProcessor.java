package nz.co.breakpoint.jmeter.modifiers;

import java.util.List;
import javax.crypto.SecretKey;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.util.KeyUtils;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.message.WSSecEncrypt;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.w3c.dom.Document;

public class WSSEncryptionPreProcessor extends CryptoWSSecurityPreProcessor {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    private String symmetricEncryptionAlgorithm, keyEncryptionAlgorithm;
    private boolean createEncryptedKey;

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
        WSConstants.KEYTRANSPORT_RSAOAEP,
        WSConstants.KEYTRANSPORT_RSAOAEP_XENC11,
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
    protected Document build(Document document, WSSecHeader secHeader) throws WSSecurityException {
        log.debug("Initializing WSSecEncrypt");
        WSSecEncrypt secBuilder = new WSSecEncrypt(secHeader);
        prepareBuilder(secBuilder);
        secBuilder.setSymmetricEncAlgorithm(getSymmetricEncryptionAlgorithm());
        secBuilder.setKeyEncAlgo(getKeyEncryptionAlgorithm());
        secBuilder.setEncryptSymmKey(isCreateEncryptedKey());
        log.debug("Generating symmetric key");
        SecretKey symmetricKey = KeyUtils.getKeyGenerator(getSymmetricEncryptionAlgorithm()).generateKey();
        log.debug("Building WSSecEncrypt");
        return secBuilder.build(getCrypto(), symmetricKey);
    }

    // Accessors
    public String getSymmetricEncryptionAlgorithm() {
        return symmetricEncryptionAlgorithm;
    }

    public void setSymmetricEncryptionAlgorithm(String symmetricEncryptionAlgorithm) {
        this.symmetricEncryptionAlgorithm = symmetricEncryptionAlgorithm;
    }

    public String getKeyEncryptionAlgorithm() {
        return keyEncryptionAlgorithm;
    }

    public void setKeyEncryptionAlgorithm(String keyEncryptionAlgorithm) {
        this.keyEncryptionAlgorithm = keyEncryptionAlgorithm;
    }

    public boolean isCreateEncryptedKey() {
        return createEncryptedKey;
    }

    public void setCreateEncryptedKey(boolean createEncryptedKey) {
        this.createEncryptedKey = createEncryptedKey;
    }

    public String getKeyIdentifier() {
        return keyIdentifier;
    }

    public void setKeyIdentifier(String keyIdentifier) {
        this.keyIdentifier = keyIdentifier;
    }

    public List<SecurityPart> getPartsToSecure() {
        return partsToSecure;
    }

    public void setPartsToSecure(List<SecurityPart> partsToSecure) {
        this.partsToSecure = partsToSecure;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
}
