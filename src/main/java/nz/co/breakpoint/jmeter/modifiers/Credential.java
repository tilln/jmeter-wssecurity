package nz.co.breakpoint.jmeter.modifiers;

import org.apache.jmeter.testelement.AbstractTestElement;

public class Credential extends AbstractTestElement {
    private static final String PASSWORD = "Credential.Password";

    public Credential() {}

    // For unit tests
    public Credential(String name, String password) {
        setName(name);
        setPassword(password);
    }

    public String getPassword() { return getPropertyAsString(PASSWORD); }

    public void setPassword(String password) { setProperty(PASSWORD, password); }

    @Override
    public String toString() {
        return getName()+":"+getPassword();
    }
}
