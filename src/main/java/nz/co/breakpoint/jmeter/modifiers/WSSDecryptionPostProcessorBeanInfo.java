package nz.co.breakpoint.jmeter.modifiers;

import java.beans.PropertyDescriptor;
import org.apache.wss4j.dom.WSConstants;

public class WSSDecryptionPostProcessorBeanInfo extends CryptoWSSecurityPostProcessorBeanInfo {

    public WSSDecryptionPostProcessorBeanInfo() {
        super(WSSDecryptionPostProcessor.class);
    }
}
