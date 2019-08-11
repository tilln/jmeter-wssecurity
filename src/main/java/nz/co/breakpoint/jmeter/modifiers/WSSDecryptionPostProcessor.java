package nz.co.breakpoint.jmeter.modifiers;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.security.auth.callback.Callback;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.Merlin;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.engine.WSSecurityEngine;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.dom.handler.WSHandlerResult;
import org.w3c.dom.Document;
import static org.apache.wss4j.common.ext.WSPasswordCallback.*;

public class WSSDecryptionPostProcessor extends CryptoWSSecurityPostProcessor {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    protected List<Credential> credentials = new ArrayList<>();

    public WSSDecryptionPostProcessor() throws ParserConfigurationException {
        super();
    }

    protected String getPasswordForAlias(String alias) {
        for (Credential cred : credentials) {
            if (alias.equals(cred.getName())) {
                return cred.getPassword();
            }
        }
        return null;
    }

    @Override
    protected Document process(Document document) throws WSSecurityException {
        WSSecurityEngine secEngine = new WSSecurityEngine();
        Crypto crypto = getCrypto();

        RequestData requestData = new RequestData();
        requestData.setSigVerCrypto(crypto);
        requestData.setDecCrypto(crypto);
        requestData.setActor(getActor());
        updateAttachmentCallbackHandler();
        requestData.setAttachmentCallbackHandler(getAttachmentCallbackHandler());
        requestData.setExpandXopInclude(true);
        requestData.setAllowRSA15KeyTransportAlgorithm(true);
        requestData.setCallbackHandler(callbacks -> {
            for (Callback callback : callbacks) {
                if (callback instanceof WSPasswordCallback) {
                    // https://ws.apache.org/wss4j/topics.html#wspasswordcallback_identifiers
                    WSPasswordCallback pwcb = (WSPasswordCallback)callback;
                    switch (pwcb.getUsage()) {
                        case DECRYPT:
                            log.debug("Providing callback with private key password for "+pwcb.getIdentifier());
                            pwcb.setPassword(getPasswordForAlias(pwcb.getIdentifier()));
                            break;
                        case USERNAME_TOKEN:
                            log.debug("Providing callback with password for username "+pwcb.getIdentifier());
                            pwcb.setPassword(getPasswordForAlias(pwcb.getIdentifier()));
                            break;
                        case SIGNATURE:
                            log.debug("Not providing callback with anything");
                            break;
                        case SECRET_KEY:
                            log.debug("Providing callback with secret key for digest "+pwcb.getIdentifier());
                            try {
                                KeyStore keystore = ((Merlin) crypto).getKeyStore();
                                Enumeration<String> aliases = keystore.aliases();
                                // Iterate through all keystore entries to find a secret key
                                // with matching digest and known password:
                                for (String alias; aliases.hasMoreElements(); ) {
                                    alias = aliases.nextElement();
                                    if (!keystore.entryInstanceOf(alias, KeyStore.SecretKeyEntry.class)) continue;
                                    byte[] keyBytes = this.crypto.getSecretKey(alias, getPasswordForAlias(alias));
                                    if (pwcb.getIdentifier().equals(CryptoTestElement.getSecretKeyDigest(keyBytes))) {
                                        pwcb.setKey(keyBytes);
                                        break;
                                    }
                                }
                            } catch (KeyStoreException e) {
                                log.error("Failed to find secret key entry", e);
                            }
                            break;
                        default:
                            log.warn("Ignoring unsupported password callback usage "+pwcb.getUsage());
                    }
                }
            }
        });
        WSHandlerResult results = secEngine.processSecurityHeader(document, requestData);
        return document;
    }

    // Handle decrypted headers and MIME type
    @Override
    protected void updateSampleResult(org.apache.wss4j.common.ext.Attachment attachment, SampleResult subresult) {
        super.updateSampleResult(attachment, subresult);

        Map<String, String> responseHeaders = Attachment.toHeadersMap(subresult.getResponseHeaders());
        Map<String, String> decryptedHeaders = attachment.getHeaders();

        // Any decrypted headers of the attachment should replace response headers (though those shouldn't actually be present):
        if (decryptedHeaders != null && !decryptedHeaders.isEmpty()) {
            log.debug("Adding subresult headers: " + decryptedHeaders);
            responseHeaders.putAll(decryptedHeaders);
        }
        String mimeType = attachment.getMimeType(); // From <xenc:EncryptedData> MimeType attribute, otherwise undefined from GUI
        if (mimeType != null && mimeType.length() != 0) {
            log.debug("Setting subresult MimeType: " + mimeType);
            subresult.setContentType(mimeType);
            subresult.setEncodingAndType(mimeType);
            responseHeaders.replace("Content-Type", mimeType); // replace only if header is present
        }
        subresult.setResponseHeaders(Attachment.fromHeadersMap(responseHeaders));
    }

    // Accessors
    public List<Credential> getCredentials() { return credentials; }

    public void setCredentials(List<Credential> credentials) { this.credentials = credentials; }

    public List<Attachment> getAttachments() { return attachments; }

    public void setAttachments(List<Attachment> attachments) { this.attachments = attachments; }
}
