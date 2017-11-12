package nz.co.breakpoint.jmeter.modifiers;

import org.apache.jmeter.protocol.jms.sampler.JMSSampler;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestSamplerPayloadAccessor {
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
}
