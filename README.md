# Arquillian In-container URL Extension [![Build Status](https://travis-ci.org/handcraftedbits/arquillian-incontainerurl-extension.svg?branch=release/1.0.1)](https://travis-ci.org/handcraftedbits/arquillian-incontainerurl-extension)

An extension for [Arquillian](http://arquillian.org) that allows you to reference the base URL of your test deployment
from within the container.  This makes it easy to e.g. test a REST service and check its effect on backend components in
the same test case.

# Usage

Add the extension in your `pom.xml` file:

```xml
<dependency>
  <groupId>com.handcraftedbits.arquillian</groupId>
  <artifactId>arquillian-incontainerurl-extension</artifactId>
  <version>1.0.1</version>
  <scope>test</scope>
</dependency>
```

Then add the `@InContainerResource` annotation to a `URL` field or test method parameter:

```java
@InContainerResource
private URL url;
```

or

```java
@Test
public void testService (@InContainerResource URL url) {
     ...
}
```

That's it!  All your container-based tests will now have access to the URL where your test archive has been deployed.
For client-based tests (i.e., those annotated with `@RunAsClient` or where the deployment has been marked
`testable = false`), this field or method parameter will be `null`; you should use `@ArquillianResource` instead.

# Notes

* This extension will only work for WAR archives supporting Servlet 3.0 or an EAR archive containing a WAR archive that
supports Servlet 3.0.
* This extension will only work on real Java EE application server containers (e.g. [Wildfly](http://wildfly.org),
[Glassfish](https://glassfish.java.net), etc.).  It will not work with the [Weld](http://weld.cdi-spec.org) container.
* Though it may be tempting, don't use `@ArquillianResource` and `@InContainerResource` on the same field or test method
parameter; your tests will fail.
