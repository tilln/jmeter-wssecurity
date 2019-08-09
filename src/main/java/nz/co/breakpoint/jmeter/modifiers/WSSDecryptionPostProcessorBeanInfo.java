package nz.co.breakpoint.jmeter.modifiers;

import java.beans.PropertyDescriptor;
import org.apache.jmeter.testbeans.gui.TableEditor;

public class WSSDecryptionPostProcessorBeanInfo extends CryptoWSSecurityPostProcessorBeanInfo {

    public WSSDecryptionPostProcessorBeanInfo() {
        super(WSSDecryptionPostProcessor.class);

        property("certAlias").setHidden(true); // not needed for decryption, key reference is in response

        createPropertyGroup("Validation", new String[] { "failOnWSSException", "credentials" });

        PropertyDescriptor p = property("failOnWSSException");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, Boolean.TRUE);
        p.setValue(NOT_OTHER, Boolean.TRUE);

        p = property("credentials");
        p.setPropertyEditorClass(TableEditor.class);
        p.setValue(TableEditor.CLASSNAME, Credential.class.getName());
        p.setValue(TableEditor.HEADERS, getTableHeadersWithDefaults("credentials.tableHeaders",
                new String[]{"Identifier", "Password"}));
        p.setValue(TableEditor.OBJECT_PROPERTIES, new String[]{"name", "password"});

        createPropertyGroup("Decryption", new String[] { "attachments" });

        p = property("attachments");
        p.setPropertyEditorClass(TableEditor.class);
        p.setValue(TableEditor.CLASSNAME, Attachment.class.getName());
        p.setValue(TableEditor.HEADERS, getTableHeadersWithDefaults("attachments.tableHeaders",
            new String[]{"Content-ID", "Bytes (base64)"}));
        p.setValue(TableEditor.OBJECT_PROPERTIES, new String[]{"name", "content"});
    }
}
