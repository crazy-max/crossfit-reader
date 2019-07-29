<p align="center"><a href="https://github.com/crazy-max/crossfit-reader" target="_blank"><img width="256" src="https://github.com/crazy-max/crossfit-reader/blob/master/res/logo.png"></a></p>

<p align="center">
  <a href="https://github.com/crazy-max/crosffit-reader/releases/latest"><img src="https://img.shields.io/github/release/crazy-max/crossfit-reader.svg?style=flat-square" alt="GitHub release"></a>
  <a href="https://github.com/crazy-max/crosffit-reader/releases/latest"><img src="https://img.shields.io/github/downloads/crazy-max/crossfit-reader/total.svg?style=flat-square" alt="Total downloads"></a>
  <a href="https://ci.appveyor.com/project/crazy-max/crossfit-reader"><img src="https://img.shields.io/appveyor/ci/crazy-max/crossfit-reader.svg?style=flat-square" alt="AppVeyor"></a>
  <a href="https://www.codacy.com/app/crazy-max/crossfit-reader"><img src="https://img.shields.io/codacy/grade/3daf2e6395764406ab0c8fd2c2202581.svg?style=flat-square" alt="Code Quality"></a>
  <br /><a href="https://www.patreon.com/crazymax"><img src="https://img.shields.io/badge/donate-patreon-fb664e.svg?style=flat-square" alt="Support me on Patreon"></a>
  <a href="https://www.paypal.me/crazyws"><img src="https://img.shields.io/badge/donate-paypal-7057ff.svg?style=flat-square" alt="Donate Paypal"></a>
</p>

## About

💳 Crossfit Reader is a card reader application for ACR122U device affiliate to the [CrossFit Nancy](http://www.crossfit-nancy.fr/) booking application.

## Features

* ACR122U compliant
* Supported tags: Mifare Classic 1K (only)
* Java 1.8
* Windows 7 operating system or later 
* Read card UID
* Use remote webservices to associate / remove card UID for a crossfit member

## Build

`gradlew.bat antClean getDeps antRelease`

Libraries used to build the project :
* [Gradle](https://gradle.org/)
* [Apache Ant](http://ant.apache.org/)
* [Launch4j](http://launch4j.sourceforge.net/)
* [InnoSetup](http://www.jrsoftware.org/isinfo.php) are used to build the project.

## About the ACR122U device

![Yosoo ACR122U](res/acr122u.jpg?raw=true)

The [ACR122U](http://www.acs.com.hk/en/products/3/acr122u-usb-nfc-reader/) is made by [Advanced Card Systems Ltd](http://www.acs.com.hk/) (Hong Kong, China).

### Device features

* PC-linked contactless smart card ([NFC](http://en.wikipedia.org/wiki/Near_field_communication)) reader/writer
* Contactless operating frequency: 13.56 MHz
* Supports: [ISO14443](http://en.wikipedia.org/wiki/ISO/IEC_14443) Type A & B, [MIFARE®](http://en.wikipedia.org/wiki/MIFARE), FeliCa, 4 types of NFC (ISO/IEC18092) tags
* Interface: USB
* Operating Distance: Up to 50 mm (depends on the tag type)
* Operating Voltage: DC 5.0V
* Operating Frequency: 13.56 MHz
* Compliance/Certifications: ISO 14443, PC/SC, CCID
* Size: 98 mm x 65 mm x 12.8 mm
* Weight: 70 g

## How can i help ?

All kinds of contributions are welcomed :raised_hands:!<br />
The most basic way to show your support is to star :star2: the project, or to raise issues :speech_balloon:<br />
But we're not gonna lie to each other, I'd rather you buy me a beer or two :beers:!

[![Support me on Patreon](res/patreon.png)](https://www.patreon.com/crazymax) 
[![Paypal](res/paypal-donate.png)](https://www.paypal.me/crazyws)

## License

MIT. See `LICENSE` for more details.<br />
Icons credit to [Recep Kütük](http://recepkutuk.com/).
