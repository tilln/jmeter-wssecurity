# jmeter-wssecurity [![travis][travis-image]][travis-url]

[travis-image]: https://travis-ci.org/tilln/jmeter-wssecurity.svg?branch=master
[travis-url]: https://travis-ci.org/tilln/jmeter-wssecurity

Overview
------------

Apache JMeter plugin for signing and encrypting SOAP messages (WS-Security).

Installation
------------

1. Copy the [jmeter-wssecurity jar file](https://github.com/tilln/jmeter-wssecurity/releases/download/1.0/jmeter-wssecurity-1.0.jar) into JMeter's lib/ext directory.
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
that can be configured for signing and encrypting the payloads of an HTTP or JMS request.
Users familiar with SoapUI will find similarities to the [outgoing WS-Security configuration](https://www.soapui.org/soapui-projects/ws-security.html#3-Outgoing-WS-Security-configurations).

### SOAP Message Signer

![SOAP Message Signer](https://raw.githubusercontent.com/tilln/jmeter-wssecurity/master/docs/signature.png)

### SOAP Message Encrypter

![SOAP Message Encrypter](https://raw.githubusercontent.com/tilln/jmeter-wssecurity/master/docs/encryption.png)


Dependencies
------------

Maven retrieves the following dependencies:

* org.apache.wss4j / wss4j-ws-security-dom
* org.apache.wss4j / wss4j-ws-security-common
* org.apache.santuario / xmlsec
