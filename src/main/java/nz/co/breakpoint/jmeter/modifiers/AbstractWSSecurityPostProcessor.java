package nz.co.breakpoint.jmeter.modifiers;

import java.io.IOException;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.IOUtils;
import org.apache.jmeter.assertions.AssertionResult;
import org.apache.jmeter.processor.PostProcessor;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.w3c.dom.Document;

/**
 * Abstract base class for any postprocessor that validates/decrypts a SOAP WSS header in the sampler response.
 * Subclasses need to provide an actual wss4j WSSecBase instance and implement a build wrapper method.
 */
public abstract class AbstractWSSecurityPostProcessor extends AbstractWSSecurityTestElement implements PostProcessor, TestBean {

    private static final Logger log = LoggingManager.getLoggerForClass();

    static final String FAIL_ON_WSS_EXCEPTION = "jmeter.wssecurity.failSamplerOnWSSException";
    static final String SAMPLE_LABEL_REGEX = "jmeter.wssecurity.findAttachmentsBySampleLabel";

    public AbstractWSSecurityPostProcessor() throws ParserConfigurationException {
        super();
    }

    /* Subclasses are to implement the actual header processing.
     */
    protected abstract Document process(Document document) throws WSSecurityException;

    /* The main method that is called after the sampler.
     * This will parse, process (validate or decrypt) and then replace
     * the sampler's response.
     */
    @Override
    public void process() {
        SampleResult prev = getResult();
        if (prev == null) return;

        String xml = prev.getResponseDataAsString();
        if (xml == null) return;

        try {
            log.debug("Parsing xml response");
            Document doc = stringToDocument(xml);

            log.debug("Processing WSS header");
            doc = this.process(doc); // Delegate in abstract method

            prev.setResponseData(documentToString(doc), prev.getDataEncodingWithDefault());

            retrieveProcessedAttachments(prev);
        }
        catch (Exception e) {
            log.error("Processing failed! ", e);
            if (e instanceof WSSecurityException && JMeterUtils.getPropDefault(FAIL_ON_WSS_EXCEPTION, true)) {
                AssertionResult assertionResult = new AssertionResult("WSSecurityException").setResultForFailure(e.getMessage());
                assertionResult.setError(true);
                assertionResult.setFailure(true);
                prev.addAssertionResult(assertionResult);
                prev.setSuccessful(false);
            }
        }
    }

    /* The attachmentCallbackHandler may contain response attachments that have been processed
     * (i.e. decrypted) by wss4j. This method updates the SampleResult with those attachments,
     * by either replacing a subresult that matches the attachment CID or by creating a new subresult.
     * Matching is based on the subresult's Content-ID header or its sample label together with a configurable regex.
     */
    protected void retrieveProcessedAttachments(SampleResult prev) {
        if (getAttachmentCallbackHandler() == null) {
            log.debug("AttachmentCallbackHandler undefined, skip retrieving attachments");
        } else if (prev == null) {
            log.debug("SampleResult undefined, cannot normally happen");
        } else if (getAttachmentCallbackHandler().getAttachments() == null || getAttachmentCallbackHandler().getAttachments().isEmpty()) {
            log.debug("No attachments received");
        } else {
            log.debug("Updating result attachments");
            for (org.apache.wss4j.common.ext.Attachment attachment : getAttachmentCallbackHandler().getAttachments()) {
                String cid = attachment.getId();

                SampleResult subresult = findAttachment(cid, prev);
                if (subresult != null) {
                    log.debug("Updating subresult "+subresult.getSampleLabel()+" with content from cid:"+cid);
                }
                else {
                    log.debug("No subresult found, creating new one for cid:"+cid);
                    subresult = new SampleResult(prev.getTimeStamp(), 0 /*elapsed not applicable*/);
                    prev.addRawSubResult(subresult);
                    subresult.setSampleLabel("Attachment cid:"+cid);
                    subresult.setSuccessful(true);
                }
                updateSampleResult(attachment, subresult);
            }
        }
    }

    /* Transfer any details from the received attachment to the subresult.
     * Here just only the data is being read (which could be from a decrypting stream).
     * Subclasses should refine this method to set additional subresult fields such as headers etc.
     */
    protected void updateSampleResult(org.apache.wss4j.common.ext.Attachment attachment, SampleResult subresult) {
        try {
            subresult.setResponseData(IOUtils.toByteArray(attachment.getSourceStream())); // could be a binary attachment, so don't just treat as String
        }
        catch (IOException e) {
            log.error("Failed to read source stream for attachment cid:"+attachment.getId(), e);
        }
    }

    // Recursively (depth-first) look for a SampleResult matching the decrypted attachment's cid
    protected SampleResult findAttachment(String cid, SampleResult result) {
        if (result == null) return null; // should not normally happen

        if (resultContainsCid(cid, result)) { return result; }

        for (SampleResult sub : result.getSubResults()) {
            SampleResult found = findAttachment(cid, sub);
            if (found != null) {
                return found;
            }
        }
        return null; // not found
    }

    // Check if SampleResult contains attachment with Content-ID cid, and search either for a label or a header match.
    protected boolean resultContainsCid(String cid, SampleResult result) {
        if (cid == null || result == null) return false;

        String labelRegex = JMeterUtils.getProperty(SAMPLE_LABEL_REGEX);
        if (labelRegex != null && !labelRegex.equals("")) {
            return cid.equals(result.getSampleLabel().replaceFirst(labelRegex, "$1"));
        }
        return Pattern.compile("(?m)^Content-ID\\s*:\\s*(<"+cid+">|"+cid+")\\s*$")
            .matcher(result.getResponseHeaders()).find();
    }
}
