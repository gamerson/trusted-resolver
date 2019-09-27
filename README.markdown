# Trusted Resolver

## What is it?

This repo shows the capabilities of an OSGi Resolver Hook.

## Examples

To test the examples clone this repo and run
```
$ ./mvnw clean install
```

Now you can execute one of the samples with the commands below

### Signed jar with good license provider

This example has only one jar that is both signed by Liferay and requires a valid license.  Also in this app is a valid license provider bundle.  The resolver hook ensures that only a Liferay signed license provider is available and also ensures that any jar that requires a license is signed.

```
$ java -jar trusted.resolve.app/target/good-signature.jar
```

### Good license provider but test jar is not signed

This example has one jar that requires a license. But this time the jar that requires signature has not been signed.  Therefore it will not be able to resolve the license.

```
$ java -jar trusted.resolve.app/target/no-signature.jar
```

### Good license provider but test jar has signed with another certificate

This example has one jar that requires a license. But this time the jar that requires signature has been signed with another certificate, so it will be prevented from running.

```
$ java -jar trusted.resolve.app/target/bad-signature.jar
```

### Fake Provider

This example has one jar that requires a license but then instead of a good license provider there is a fake license provider.  It will not work since it isn't signed with a Liferay signature but instead an "ACME" signature, so it can not provide a valid license.

```
$ java -jar trusted.resolve.app/target/fake-provider.jar
```