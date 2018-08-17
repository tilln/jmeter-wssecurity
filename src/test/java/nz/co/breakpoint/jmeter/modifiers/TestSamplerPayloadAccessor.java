package nz.co.breakpoint.jmeter.modifiers;

import org.apache.jmeter.protocol.jms.sampler.JMSSampler;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.util.JMeterUtils;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestSamplerPayloadAccessor {
    @ClassRule
    public static final JMeterPropertiesResource props = new JMeterPropertiesResource();
    @BeforeClass
    public static void setUpClass() throws Exception {
        JMeterUtils.setProperty(SamplerPayloadAccessor.ACCESSORS_PROPERTY_NAME, DummySampler.class.getName()+".it");
        SamplerPayloadAccessor.parseProperties();
    }

    class DummySampler extends AbstractSampler {
        private String payload;
        public String getIt() { return payload; }
        public void setIt(String payload) { this.payload = payload; }
        public SampleResult sample(Entry e) { return null; }
    }

    DummySampler sampler = new DummySampler();

    @Test
    public void testGetPayloadOfKnownSampler() throws Exception {
        JMSSampler sampler = new JMSSampler();
        sampler.setContent(TestWSSSecurityPreProcessorBase.SAMPLE_SOAP_MSG);
        String payload = SamplerPayloadAccessor.getPayload(sampler); 
        assertEquals(TestWSSSecurityPreProcessorBase.SAMPLE_SOAP_MSG, payload);
    }

    @Test
    public void testSetPayloadOfKnownSampler() throws Exception {
        JMSSampler sampler = new JMSSampler();
        SamplerPayloadAccessor.setPayload(sampler, TestWSSSecurityPreProcessorBase.SAMPLE_SOAP_MSG);
        String payload = sampler.getContent();
        assertEquals(TestWSSSecurityPreProcessorBase.SAMPLE_SOAP_MSG, payload);
    }

    @Test
    public void testGetPayloadOf3rdPartySampler() throws Exception {
        sampler.setIt(TestWSSSecurityPreProcessorBase.SAMPLE_SOAP_MSG);
        String payload = SamplerPayloadAccessor.getPayload(sampler);
        assertEquals(TestWSSSecurityPreProcessorBase.SAMPLE_SOAP_MSG, payload);
    }

    @Test
    public void testSetPayloadOf3rdPartySampler() throws Exception {
        SamplerPayloadAccessor.setPayload(sampler, TestWSSSecurityPreProcessorBase.SAMPLE_SOAP_MSG);
        String payload = sampler.getIt();
        assertEquals(TestWSSSecurityPreProcessorBase.SAMPLE_SOAP_MSG, payload);
    }
}
