package nz.co.breakpoint.jmeter.modifiers;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Properties;
import javax.crypto.SecretKey;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.CryptoFactory;
import org.apache.wss4j.common.crypto.Merlin;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.util.KeyUtils;
import org.apache.xml.security.utils.XMLUtils;

import static org.apache.wss4j.common.crypto.Merlin.*;

/** Provides common keystore settings to Crypto Pre/Postprocessors.
 */
public class CryptoTestElement extends AbstractTestElement {

    private static final Logger log = LoggingManager.getLoggerForClass();

    static final String KEYSTORE_TYPE_PROPERTY = "jmeter.wssecurity.keystoreType";

    private final Properties cryptoProps = new Properties(); // Holds configured attributes for crypto instance

    public CryptoTestElement() {
        cryptoProps.setProperty(PREFIX+KEYSTORE_TYPE, JMeterUtils.getPropDefault(KEYSTORE_TYPE_PROPERTY, "JCEKS"));
    }

    public Crypto getInstance() throws WSSecurityException {
        // A new crypto instance needs to be created for every iteration as the config could contain variables which may change.
        return CryptoFactory.getInstance(cryptoProps);
    }

    /** Extract secret key with the given alias and password from the keystore.
     */
    public byte[] getSecretKey(String alias, String password) {
        try {
            // Crypto interface has no method to get secret key or keystore, so cast to Merlin (which we created ourselves):
            SecretKey key = (SecretKey)((Merlin) getInstance()).getKeyStore()
                    .getKey(alias, password.toCharArray());
            if (key == null) {
                log.error("Key "+alias+" not found in keystore");
                return null;
            }
            log.debug("Found secret key "+alias+", algorithm "+key.getAlgorithm()+" format "+key.getFormat());
            return key.getEncoded();
        } catch (WSSecurityException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            log.error("Error getting secret key: ", e);
        }
        return null;
    }

    public static String getSecretKeyDigest(byte[] keyBytes) {
        try {
            byte[] encodedBytes = KeyUtils.generateDigest(keyBytes);
            return XMLUtils.encodeToString(encodedBytes);
        } catch (WSSecurityException e) {
            log.error("Failed to get secret key digest ", e);
        }
        return null;
    }

    public String getKeystoreFile() { return cryptoProps.getProperty(PREFIX+KEYSTORE_FILE); }

    public void setKeystoreFile(String keystoreFile) { cryptoProps.setProperty(PREFIX+KEYSTORE_FILE, keystoreFile); }

    public String getKeystorePassword() { return cryptoProps.getProperty(PREFIX+KEYSTORE_PASSWORD); }

    public void setKeystorePassword(String keystorePassword) { cryptoProps.setProperty(PREFIX+KEYSTORE_PASSWORD, keystorePassword); }
}
