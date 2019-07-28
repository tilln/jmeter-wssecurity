package nz.co.breakpoint.jmeter.modifiers;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Properties;
import javax.crypto.SecretKey;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.CryptoFactory;
import org.apache.wss4j.common.crypto.Merlin;
import org.apache.wss4j.common.ext.WSSecurityException;
import static org.apache.wss4j.common.crypto.Merlin.*;

/** Provides common keystore settings to Crypto Pre/Postprocessors.
 */
public class CryptoTestElement extends AbstractTestElement {

    private static final Logger log = LoggingManager.getLoggerForClass();

    private final Properties cryptoProps = new Properties(); // Holds configured attributes for crypto instance
    protected String certAlias, certPassword;

    public CryptoTestElement() {
        cryptoProps.setProperty(PREFIX+KEYSTORE_TYPE, "JCEKS"); // compatible with JKS and PKCS12/PFX
    }

    public Crypto getInstance() throws WSSecurityException {
        // A new crypto instance needs to be created for every iteration as the config could contain variables which may change.
        return CryptoFactory.getInstance(cryptoProps);
    }

    /** Extract secret key with the current alias and password from the keystore.
     */
    public byte[] getSecretKey() {
        try {
            // Crypto interface has no method to get secret key or keystore, so cast to Merlin (which we created ourselves):
            SecretKey key = (SecretKey)((Merlin) getInstance()).getKeyStore()
                    .getKey(getCertAlias(), getCertPassword().toCharArray());
            if (key == null) {
                log.error("Key "+getCertAlias()+" not found in keystore");
                return null;
            }
            log.debug("Symmetric signature with secret key algorithm "+key.getAlgorithm()+" format "+key.getFormat());
            return key.getEncoded();
        } catch (WSSecurityException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            log.error("Error getting secret key: ", e);
        }
        return null;
    }

    public String getCertAlias() { return certAlias; }

    public void setCertAlias(String certAlias) { this.certAlias = certAlias; }

    public String getCertPassword() { return certPassword; }

    public void setCertPassword(String certPassword) { this.certPassword = certPassword; }

    public String getKeystoreFile() { return cryptoProps.getProperty(PREFIX+KEYSTORE_FILE); }

    public void setKeystoreFile(String keystoreFile) { cryptoProps.setProperty(PREFIX+KEYSTORE_FILE, keystoreFile); }

    public String getKeystorePassword() { return cryptoProps.getProperty(PREFIX+KEYSTORE_PASSWORD); }

    public void setKeystorePassword(String keystorePassword) { cryptoProps.setProperty(PREFIX+KEYSTORE_PASSWORD, keystorePassword); }
}
