package nz.co.breakpoint.jmeter.modifiers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.engine.WSSecurityEngine;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.dom.handler.WSHandlerResult;
import org.w3c.dom.Document;

public class WSSDecryptionPostProcessor extends CryptoWSSecurityPostProcessor {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    public WSSDecryptionPostProcessor() throws ParserConfigurationException {
        super();
    }

    @Override
    protected Document process(Document document) throws WSSecurityException {
        WSSecurityEngine secEngine = new WSSecurityEngine();

        RequestData requestData = new RequestData();
        requestData.setSigVerCrypto(getCrypto());
        requestData.setDecCrypto(getCrypto());
        requestData.setActor(getActor());
        updateAttachmentCallbackHandler();
        requestData.setAttachmentCallbackHandler(getAttachmentCallbackHandler());
        requestData.setExpandXopInclude(true);
        requestData.setAllowRSA15KeyTransportAlgorithm(true);
        requestData.setCallbackHandler(new CallbackHandler() {
            public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                for (Callback callback : callbacks) {
                    if (callback instanceof WSPasswordCallback) {
                        ((WSPasswordCallback)callback).setPassword(getCertPassword());
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
    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
}
