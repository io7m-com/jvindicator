jvindicator
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.jvindicator/com.io7m.jvindicator.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.jvindicator%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/com.io7m.jvindicator/com.io7m.jvindicator.svg?style=flat-square)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/io7m/jvindicator/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m/jvindicator.svg?style=flat-square)](https://codecov.io/gh/io7m/jvindicator)

![jvindicator](./src/site/resources/jvindicator.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m/jvindicator/main.linux.temurin.current.yml)](https://github.com/io7m/jvindicator/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m/jvindicator/main.linux.temurin.lts.yml)](https://github.com/io7m/jvindicator/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m/jvindicator/main.windows.temurin.current.yml)](https://github.com/io7m/jvindicator/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m/jvindicator/main.windows.temurin.lts.yml)](https://github.com/io7m/jvindicator/actions?query=workflow%3Amain.windows.temurin.lts)|


## Usage

Use the `Vindicator` class to build a validator, and then call `check()`. After
calling `check()`, parameters can be inspected in a type-safe manner:

```
final var v =
  Vindication.start();
final var p0 =
  v.addRequiredParameter("u0", Vindication.integerUnsigned());
final var p1 =
  v.addRequiredParameter("u1", Vindication.integerUnsignedLong());
final var p2 =
  v.addRequiredParameter("s0", Vindication.integerSigned());
final var p3 =
  v.addRequiredParameter("s1", Vindication.integerSignedLong());
final var p4 =
  v.addRequiredParameter("i0", Vindication.integerBig());

v.check(Map.ofEntries(
  Map.entry("u0", new String[]{"23"}),
  Map.entry("u1", new String[]{"24"}),
  Map.entry("s0", new String[]{"-23"}),
  Map.entry("s1", new String[]{"-24"}),
  Map.entry("i0", new String[]{"-4703919738795935661825"})
));

assertEquals(23, p0.get());
assertEquals(24L, p1.get());
assertEquals(-23, p2.get());
assertEquals(-24L, p3.get());
assertEquals(new BigInteger("-4703919738795935661825"), p4.get());
```

