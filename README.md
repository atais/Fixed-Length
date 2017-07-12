# fixed-length [![Build Status](https://travis-ci.org/atais/Fixed-Length.svg?branch=master)](https://travis-ci.org/atais/Fixed-Length) [![codecov](https://codecov.io/gh/atais/Fixed-Length/branch/master/graph/badge.svg)](https://codecov.io/gh/atais/Fixed-Length) [![license](https://img.shields.io/github/license/mashape/apistatus.svg?style=flat)](https://github.com/atais/Fixed-Length/blob/master/LICENSE)

A simple Scala library for parsing fixed-length format.

A graceful playground for Shapeless and Cats libraries.

## Installation

You can find the `fixed-length` library on [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.atais%22%20AND%20%22fixed-length%22)

Add the library to your dependencies:

```
libraryDependencies += "com.github.atais" %% "fixed-length" % "0.3"
```

Library is cross-compiled for `Scala 2.10`, `2.11` and `2.12`.

## Examples

Please find example test scenarios describing simple use cases:

* [EncoderTest](https://github.com/atais/Fixed-Length/blob/master/src/test/scala/com/github/atais/fixedlength/simple/EncoderTest.scala)
* [DecoderTest](https://github.com/atais/Fixed-Length/blob/master/src/test/scala/com/github/atais/fixedlength/simple/DecoderTest.scala)
* [CodecTest](https://github.com/atais/Fixed-Length/blob/master/src/test/scala/com/github/atais/fixedlength/simple/CodecTest.scala)

## Usage

Describe your class with an `Encoder`, `Decoder` or `Codec` typeclass and `Parse` away!

## Parameters

### start: Int
A fixed position in line, where the field begins
 
### end: Int
A fixed position in line, where the field ends

### align: Alignment 
*(default value is `Alignment.Left`)*

Alignment of the field's value in the given area of size `end - start`
 
Available options:

* `Alignment.Left`
* `Alignment.Right`
 
### padding: Char
*(default value is `' '`)*

Fills the remaining space in the given area with the padding Char.

**Warning!** 
Wrong selection of padding char may cause decoding issues! 


## [License](https://github.com/atais/Fixed-Length/blob/master/LICENSE)
