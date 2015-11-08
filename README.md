![fonz icon](https://raw.githubusercontent.com/decarbonization/android-fonz/master/app/src/main/res/drawable-mdpi/ic_launcher.png)

# Fonz

[![Circle CI](https://circleci.com/gh/decarbonization/android-fonz/tree/master.svg?style=svg&circle-token=f4212f192ae698963c57dc6dde54be7ad4d9bce6)](https://circleci.com/gh/decarbonization/android-fonz/tree/master)

A clone of the iconfactory's excellent [Freznic](http://frenzic.com/) game for Android.

# Signing

The Fonz project pulls all signing information out of a properties file named `signing.properties`
placed in the root directory. The properties file specifies the location of your keystore files,
what they're called, and the information necessary to load them.

```properties
# An example `signing.properties`

keyStoreHome=path/to/your/keystores

debugKeyStoreName=debug.keystore
debugKeyStoreAlias=debug
debugKeyStorePassword=supersecret

releaseKeyStoreName=release.keystore
releaseKeyStoreAlias=release
releaseKeyStorePassword=supersecret
```

The matching keystore directory layout for the example `signing.properties` is as follows:

- `keystores/`
    + `debug.keystore`
    + `release.keystore`

# Contributing

If you find a bug, or see something that could be improved, don't hesitate to create a new issue on the `android-fonz` GitHub project.

If you'd like to contribute code to Fonz, fork the project on GitHub, and submit a pull request with your changes. Be sure to include unit tests where possible, and try to follow the code style of the project as closely as you can.

# License

    Copyright (c) 2015, Peter 'Kevin' MacWhinnie
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

    1. Redistributions may not be sold, nor may they be used in a commercial
       product or activity.
    2. Redistributions of source code must retain the above copyright notice, this
       list of conditions and the following disclaimer.
    3. Redistributions in binary form must reproduce the above copyright notice,
       this list of conditions and the following disclaimer in the documentation
       and/or other materials provided with the distribution.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
    ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
    WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
    DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
    ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
    (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
    LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
    ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

(Derived from new the BSD License combined with the non-commercial clause from the MAME license.)
