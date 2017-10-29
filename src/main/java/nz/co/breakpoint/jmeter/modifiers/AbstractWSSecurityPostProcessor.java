package nz.co.breakpoint.jmeter.modifiers;

import org.apache.jmeter.processor.PostProcessor;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import java.io.StringReader;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.util.XMLUtils;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Abstract base class for any postprocessor that validates/decrypts a SOAP WSS header in the sampler response.
 * Subclasses need to provide an actual wss4j WSSecBase instance and implement a build wrapper method.
 */
public abstract class AbstractWSSecurityPostProcessor extends AbstractXMLTestElement implements PostProcessor, TestBean { 

    private static final Logger log = LoggingManager.getLoggerForClass();

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
        SampleResult prev = getThreadContext().getPreviousResult();
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
        }
    }
}
