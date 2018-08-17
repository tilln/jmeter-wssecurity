package nz.co.breakpoint.jmeter.modifiers;

import javax.xml.parsers.ParserConfigurationException;
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
}
