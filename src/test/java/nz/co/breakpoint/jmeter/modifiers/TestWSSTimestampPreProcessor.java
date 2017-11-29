package nz.co.breakpoint.jmeter.modifiers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.junit.Before;
import org.junit.Test;

public class TestWSSTimestampPreProcessor extends TestWSSSecurityPreProcessorBase {
    private WSSTimestampPreProcessor mod = null;
    private final static int TIME_TO_LIVE = 300;
    private JMeterContext context = null;
    
    @Before
    public void setUp() throws Exception {
        context = JMeterContextService.getContext();
        mod = new WSSTimestampPreProcessor();
        mod.setThreadContext(context);
        mod.setTimeToLive(TIME_TO_LIVE);
    }

    @Test
    public void testTimestamp() throws Exception {
        HTTPSamplerBase sampler = createHTTPSampler();
        context.setCurrentSampler(sampler);
        mod.process();
        String content = SamplerPayloadAccessor.getPayload(sampler);
        assertThat(content, containsString(":Timestamp"));
    }
}
