package nz.co.breakpoint.jmeter.modifiers;

import javax.xml.parsers.ParserConfigurationException;
import org.apache.jmeter.protocol.jms.sampler.JMSSampler;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.message.WSSecBase;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

public class TestAbstractWSSecurityPreProcessor extends TestWSSSecurityPreProcessorBase {
    private DummyWSSecurityPreProcessor instance = null;

    class DummyWSSecurityPreProcessor extends AbstractWSSecurityPreProcessor {
        public DummyWSSecurityPreProcessor() throws ParserConfigurationException {
            super();
        }

        @Override
        protected Document build(Document document, WSSecHeader secHeader) throws WSSecurityException {
            // no-op implementation for testing
            return document;
        }
    }

    @Before
    public void setUp() throws Exception {
        context = JMeterContextService.getContext();
        instance = new DummyWSSecurityPreProcessor();
        instance.setThreadContext(context);
    }

    @Test
    public void testGetPayloadOfOtherSampler() throws Exception {
        JMSSampler sampler = new JMSSampler();
        context.setCurrentSampler(sampler);

        sampler.setContent(SAMPLE_SOAP_MSG);
        String payload = instance.getSamplerPayload();
        assertEquals(SAMPLE_SOAP_MSG, payload);
    }

    @Test
    public void testSetPayloadOfOtherSampler() throws Exception {
        JMSSampler sampler = new JMSSampler();
        context.setCurrentSampler(sampler);

        instance.setSamplerPayload(SAMPLE_SOAP_MSG);
        String payload = sampler.getContent();
        assertEquals(SAMPLE_SOAP_MSG, payload);
    }

    @Test
    public void testProcess() throws Exception {
        JMSSampler sampler = new JMSSampler();
        context.setCurrentSampler(sampler);
        final String xml = "<x>✓</x>";
        sampler.setContent(xml);

        instance.process();
        String payload = sampler.getContent();
        assertThat(payload, containsString(">✓<"));
        assertThat(payload, containsString(":Security"));
    }
}
