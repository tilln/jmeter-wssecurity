package nz.co.breakpoint.jmeter.modifiers;

import javax.xml.parsers.ParserConfigurationException;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.ext.WSSecurityException;

/**
 * Abstract parent class of any postprocessors that perform crypto operations (e.g. signature or encryption).
 */
public abstract class CryptoWSSecurityPostProcessor extends AbstractWSSecurityPostProcessor {

    protected CryptoTestElement crypto = new CryptoTestElement();

    public CryptoWSSecurityPostProcessor() throws ParserConfigurationException {
        super();
    }

    protected Crypto getCrypto() throws WSSecurityException {
        return crypto.getInstance();
    }

    // Accessors
    public String getKeystoreFile() { return crypto.getKeystoreFile(); }

    public void setKeystoreFile(String keystoreFile) { crypto.setKeystoreFile(keystoreFile); }

    public String getKeystorePassword() { return crypto.getKeystorePassword(); }

    public void setKeystorePassword(String keystorePassword) { crypto.setKeystorePassword(keystorePassword); }
}
