# jmeter-wssecurity [![travis][travis-image]][travis-url]

[travis-image]: https://travis-ci.org/tilln/jmeter-wssecurity.svg?branch=master
[travis-url]: https://travis-ci.org/tilln/jmeter-wssecurity

Overview
------------

Apache JMeter plugin for signing and encrypting SOAP messages (WS-Security).

Installation
------------

1. Copy the [jmeter-wssecurity jar file](https://github.com/tilln/jmeter-wssecurity/releases/download/1.1/jmeter-wssecurity-1.1.jar) into JMeter's lib/ext directory.
2. Copy the following dependencies into JMeter's lib directory:
	* [org.apache.wss4j / wss4j-ws-security-dom](http://central.maven.org/maven2/org/apache/wss4j/wss4j-ws-security-dom/2.1.8/wss4j-ws-security-dom-2.1.8.jar)
	* [org.apache.wss4j / wss4j-ws-security-common](http://central.maven.org/maven2/org/apache/wss4j/wss4j-ws-security-common/2.1.8/wss4j-ws-security-common-2.1.8.jar)
	* [org.apache.santuario / xmlsec](http://central.maven.org/maven2/org/apache/santuario/xmlsec/2.0.8/xmlsec-2.0.8.jar)
3. When starting JMeter there will be the following two Preprocessors:
	* SOAP Message Signer
	* SOAP Message Encrypter

Usage
------------

The plugin provides two [Preprocessors](http://jmeter.apache.org/usermanual/component_reference.html#preprocessors) 
that can be configured for signing and encrypting the payloads of an [HTTP request](http://jmeter.apache.org/usermanual/component_reference.html#HTTP_Request) 
or [JMS Publisher](http://jmeter.apache.org/usermanual/component_reference.html#JMS_Publisher)/[Point-to-Point sampler](http://jmeter.apache.org/usermanual/component_reference.html#JMS_Point-to-Point).
Users familiar with SoapUI will find similarities to the [outgoing WS-Security configuration](https://www.soapui.org/soapui-projects/ws-security.html#3-Outgoing-WS-Security-configurations).

### SOAP Message Signer

![SOAP Message Signer](https://raw.githubusercontent.com/tilln/jmeter-wssecurity/master/docs/signature.png)

### SOAP Message Encrypter

![SOAP Message Encrypter](https://raw.githubusercontent.com/tilln/jmeter-wssecurity/master/docs/encryption.png)

### Configuration

The dropdown fields are initialized with WSS default values, and allow the customization of most signature and encryption settings, 
depending on what the endpoint's WSDL defines.

The "Parts to Sign"/"Parts to Encrypt" are empty by default, however, that results in the SOAP Body content to be signed or encrypted.

Suppose the Timestamp element was to be included in the signature or encryption in addition to the Body element, both would have to be listed as follows: 

|Name|Namespace|Encode|
|----|---------|------|
|Body|http://schemas.xmlsoap.org/soap/envelope/| |
|Timestamp|http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd| |

Note that the Preprocessor does not generate the Timestamp element but it needs to be present in the payload.

Encode is only relevant for encryption and can be one of the following:
* "Element" (default): The entire XML element is encrypted.
* "Content": Only child nodes of the XML element are encrypted (i.e. the element name and its attributes will remain in clear text).
* "Header": Encloses the XML element in an EncryptedHeader element ("http://docs.oasis-open.org/wss/oasis-wss-wssecurity-secext-1.1.xsd"), 
but only if it is an immediate child node of the SOAP Header.

### Support for 3rd party samplers

Samplers that are not JMeter core functionality, such as [JMeter-Plugins](http://jmeter-plugins.org), can also be used
if they provide a getter/setter pair to access a String property that contains the sampler's payload that is to be signed or encrypted.

In that case, the JMeter property `jmeter.wssecurity.samplerPayloadAccessors` can be used to specify the class and member name (without the get/set prefix) 
which the Preprocessor will access at run time via Reflection.

Suppose a sampler like the following:
```java
package some.package;
public class SomeSampler extends AbstractSampler {
	public String getPayload() 
	// ...
	public void setPayload(String content)
	// ...
}
```

Then the JMeter property should be set like so: `jmeter.wssecurity.samplerPayloadAccessors=some.package.SomeSampler.payload`

More than one of these can be comma separated (if really required).
