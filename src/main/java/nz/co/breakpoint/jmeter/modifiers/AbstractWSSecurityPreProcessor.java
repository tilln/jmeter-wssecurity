package nz.co.breakpoint.jmeter.modifiers;

import org.apache.jmeter.processor.PreProcessor;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.message.WSSecBase;
import org.w3c.dom.Document;

/**
 * Abstract base class for any preprocessor that creates/modifies a SOAP WSS header in the sampler payload.
 * Subclasses need to implement a build method that creates the actual header element.
 */
public abstract class AbstractWSSecurityPreProcessor extends AbstractWSSecurityTestElement implements PreProcessor, TestBean {

    private static final Logger log = LoggingManager.getLoggerForClass();

    private String actor;
    private boolean mustUnderstand;

    public AbstractWSSecurityPreProcessor() throws ParserConfigurationException {
        super();
    }

    protected String getSamplerPayload() {
        log.debug("Getting sampler payload");
        return SamplerPayloadAccessor.getPayload(getSampler());
    }

    protected void setSamplerPayload(String payload) {
        log.debug("Setting sampler payload");
        SamplerPayloadAccessor.setPayload(getSampler(), payload);
    }

    /* Subclasses are to implement the actual creation of the signature or encryption.
     */
    protected abstract Document build(Document document, WSSecHeader secHeader)
        throws WSSecurityException;

    /* The main method that is called before the sampler.
     * This will get, parse, secure (sign or encrypt) and then replace
     * the sampler's payload.
     */
    @Override
    public void process() {
        String xml = getSamplerPayload();
        if (xml == null) return;

        try {
            log.debug("Parsing xml payload");
            Document doc = stringToDocument(xml);

            log.debug("Initializing WSS header");
            WSSecHeader secHeader = new WSSecHeader(getActor(), isMustUnderstand(), doc);
            secHeader.insertSecurityHeader(); // Create header unless one exists

            log.debug("Building WSS header");
            doc = this.build(doc, secHeader); // Delegate in abstract method

            setSamplerPayload(documentToString(doc));
        }
        catch (Exception e) {
            log.error("Processing failed! ", e);
        }
    }

    // Accessors
    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public boolean isMustUnderstand() {
        return mustUnderstand;
    }

    public void setMustUnderstand(boolean mustUnderstand) {
        this.mustUnderstand = mustUnderstand;
    }
}
