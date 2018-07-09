package nz.co.breakpoint.jmeter.modifiers;

import java.beans.PropertyDescriptor;

public class WSSEncryptionPreProcessorBeanInfo extends CryptoWSSecurityPreProcessorBeanInfo {

    public WSSEncryptionPreProcessorBeanInfo() {
        super(WSSEncryptionPreProcessor.class);

        createPropertyGroup("Encryption", new String[]{ 
            "keyIdentifier", "symmetricEncryptionAlgorithm", "keyEncryptionAlgorithm", "createEncryptedKey",
            createPartsToSecureProperty().getName()
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
