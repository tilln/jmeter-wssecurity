package nz.co.breakpoint.jmeter.modifiers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.junit.Before;
import org.junit.Test;

public class WSSTimestampPreProcessorPreProcessor extends TestWSSSecurityPreProcessorBase {
    private WSSUsernameTokenPreProcessor mod = null;
    private WSSTimestampPreProcessor timeStampMod = null;
    private final static String USERNAME = "BREAKPOINT";
    private final static String PASSWORD = "JMETERROCKS";
    private final static int TIME_TO_LIVE =	300;
    private JMeterContext context = null;
    
    @Before
    public void setUp() throws Exception {
        context = JMeterContextService.getContext();
    }

    @Test
    public void testAllUsernameTokenCombinations() throws Exception {
    		timeStampMod = new WSSTimestampPreProcessor();
    		timeStampMod.setTimeToLive(TIME_TO_LIVE);
        for (String pt : WSSUsernameTokenPreProcessor.passwordTypes) {
            for (boolean an : new boolean[]{true, false}) {
                for (boolean ac : new boolean[]{true, false}) {
                    mod = new WSSUsernameTokenPreProcessor();
                    mod.setThreadContext(context);
                    mod.setUsername(USERNAME);
                    mod.setPassword(PASSWORD);
                    mod.setPasswordType(pt);
                    mod.setAddNonce(an);
                    mod.setAddCreated(ac);
                    HTTPSamplerBase sampler = createHTTPSampler();
                    context.setCurrentSampler(sampler);
                    mod.process();
                    
                    timeStampMod.setThreadContext(context);
                    timeStampMod.process();
                    
                    String content = SamplerPayloadAccessor.getPayload(sampler);
                    assertThat(content, containsString(":Timestamp"));
                }
            }
        }
    }
}
