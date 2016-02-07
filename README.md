[![License](https://img.shields.io/github/license/Torchmind/Mango.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.txt)
[![Latest Tag](https://img.shields.io/github/tag/Torchmind/Mango.svg?style=flat-square&label=Latest Tag)](https://github.com/Torchmind/Mango/tags)
[![Latest Release](https://img.shields.io/github/release/Torchmind/Mango.svg?style=flat-square&label=Latest Release)](https://github.com/Torchmind/Mango/releases)

Base Defense 2
==============

Table of Contents
-----------------
* [About](#about)
* [Contacts](#contacts)
* [Usage](#usage)
* [License](#license)
* [Downloads](#downloads)
* [Issues](#issues)
* [Building](#building)
* [Contributing](#contributing)

About
-----

A collection of utility interfaces and implementations for the use within Java libraries and applications.

Contacts
--------

* [Website](https://www.torchmind.com/open-source)
* [IRC #Torchmind on Freenode](http://webchat.freenode.net/?channels=%23Torchmind)
* [GitHub](https://github.com/Torchmind/Mango)

Usage
-----

You may need either one of the following repositories (either the stable or snapshot repository) depending on the
library version you chose to use:

```xml
<!-- Stable Releases -->
<repository>
        <id>torchmind</id>
        <name>Torchmind Public Repository</name>
        <url>https://maven.torchmind.com/release</url>
</repository>

<!-- Testing (Unstable) Releases -->
<repository>
        <id>torchmind</id>
        <name>Torchmind Public Repository</name>
        <url>https://maven.torchmind.com/snapshot</url>
        
        <snapshots>
                <enabled>true</enabled>
        </snapshots>
</repository>
```

In addition you may either choose to depend on the entire framework (and thus all of its components) or any of its
modules as shown below:

```xml
<!-- All Components -->
<dependency>
        <groupId>com.torchmind.mango</groupId>
        <artifactId>mango</artifactId>
        <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- Single Component -->
<dependency>
        <groupId>com.torchmind.mango</groupId>
        <artifactId>$moduleName</artifactId>
        <version>1.0.0-SNAPSHOT</version>
</dependency>
```

where ```$moduleName``` may be one of the following:

* __observable__ - for the the observable properties component

License
-------

* Mango Observables
  - Copyright (C) 2016 Johannes "Akkarin" Donath
  and other copyright owners as documented in the project's IP log.
  - [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.txt)

Downloads
---------

Released versions of the library can be found on [GitHub](https://github.com/Torchmind/Mango/releases).

Issues
------

You encountered problems with the library or have a suggestion? Create an issue!

1. Make sure your issue has not been fixed in a newer version (check the list of [closed issues](https://github.com/Torchmind/Mango/issues?q=is%3Aissue+is%3Aclosed)
1. Create [a new issue](https://github.com/Torchmind/Mango/issues/new) from the [issues page](https://github.com/Torchmind/Mango/issues)
1. Enter your issue's title (something that summarizes your issue) and create a detailed description containing:
   - What is the expected result?
   - What problem occurs?
   - How to reproduce the problem?
   - Crash Log (Please use a [Pastebin](http://www.pastebin.com) service)
1. Click "Submit" and wait for further instructions

Building
--------

1. Clone this repository via ```git clone https://github.com/Torchmind/Mango.git``` or download a [zip](https://github.com/Torchmind/Mango/archive/master.zip)
1. Build the modification by running ```mvn clean install```
1. The resulting jars can be found in their respective ```target``` directories as well as your local maven repository

Contributing
------------

Before you add any major changes to the library you may want to discuss them with us (see [Contact](#contact)) as
we may choose to reject your changes for various reasons. All contributions are applied via [Pull-Requests](https://help.github.com/articles/creating-a-pull-request).
Patches will not be accepted. Also be aware that all of your contributions are made available under the terms of the
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt). Please read the [Contribution Guidelines](CONTRIBUTING.md)
for more information.
