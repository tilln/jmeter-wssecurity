package nz.co.breakpoint.jmeter.modifiers;

import java.beans.PropertyDescriptor;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerFactory;
import org.apache.jmeter.protocol.jms.sampler.JMSSampler;
import org.apache.jmeter.util.JMeterUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
