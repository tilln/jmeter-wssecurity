package nz.co.breakpoint.jmeter.modifiers;

import org.apache.jmeter.testbeans.gui.FileEditor;
import org.apache.jmeter.testbeans.gui.TextAreaEditor;

import java.beans.PropertyDescriptor;

public class WSSSamlPreProcessorBeanInfo extends AbstractWSSecurityPreProcessorBeanInfo{
    public WSSSamlPreProcessorBeanInfo() {
        super(WSSSamlPreProcessor.class);

        createPropertyGroup("Assertion", new String[]{
                "saml"
        });
        PropertyDescriptor p;

        p = property("saml");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        p.setPropertyEditorClass(TextAreaEditor.class);
    }
}
