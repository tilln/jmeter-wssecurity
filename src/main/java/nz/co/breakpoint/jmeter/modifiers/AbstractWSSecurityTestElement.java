package nz.co.breakpoint.jmeter.modifiers;

import javax.xml.parsers.ParserConfigurationException;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.wss4j.dom.engine.WSSConfig;

/**
 * Abstract base class for any test element that deals with WSSecurity.
 * Initialises the wss4j configuration when the class is loaded.
 */
public abstract class AbstractWSSecurityTestElement extends AbstractXMLTestElement implements TestBean {

    static { WSSConfig.init(); }

    private String actor;

    public AbstractWSSecurityTestElement() throws ParserConfigurationException {
        super();
    }

    protected Sampler getSampler() {
        return getThreadContext().getCurrentSampler();
    }

    protected SampleResult getResult() {
        return getThreadContext().getPreviousResult();
    }

    // Accessors
    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }
}
