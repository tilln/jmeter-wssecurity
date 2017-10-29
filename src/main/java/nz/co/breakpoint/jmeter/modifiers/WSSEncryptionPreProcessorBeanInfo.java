package nz.co.breakpoint.jmeter.modifiers;

import java.beans.PropertyDescriptor;
import org.apache.wss4j.dom.WSConstants;

public class WSSEncryptionPreProcessorBeanInfo extends CryptoWSSecurityPreProcessorBeanInfo {

    public WSSEncryptionPreProcessorBeanInfo() {
        super(WSSEncryptionPreProcessor.class);

        createPropertyGroup("Encryption", new String[]{ 
            "keyIdentifier", "symmetricEncryptionAlgorithm", "keyEncryptionAlgorithm", "createEncryptedKey",
            PARTSTOSECURE // this property is created in the parent class but added here so it is rendered at the bottom
        });
        PropertyDescriptor p;

        p = property("keyIdentifier");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, WSSEncryptionPreProcessor.keyIdentifiers[0]);
        p.setValue(NOT_OTHER, Boolean.TRUE);
        p.setValue(TAGS, WSSEncryptionPreProcessor.keyIdentifiers);

        p = property("symmetricEncryptionAlgorithm");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, WSSEncryptionPreProcessor.symmetricEncryptionAlgorithms[0]);
        p.setValue(NOT_OTHER, Boolean.TRUE);
        p.setValue(TAGS, WSSEncryptionPreProcessor.symmetricEncryptionAlgorithms);

        p = property("keyEncryptionAlgorithm");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, WSSEncryptionPreProcessor.keyEncryptionAlgorithms[0]);
        p.setValue(NOT_OTHER, Boolean.TRUE);
        p.setValue(TAGS, WSSEncryptionPreProcessor.keyEncryptionAlgorithms);

        p = property("createEncryptedKey");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, Boolean.TRUE);
    }
}
