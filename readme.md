# TODO: Fill in contents here
Update this now that both Chrome AND wkhtmltopdf options exist.


html-to-pdf-converter
---------------------------------

A library for converting HTML documents to PDF documents.

<br/>

## How it works

### Chrome


### wkhtmltopdf 
TODO: Fill in contents here

<br/>


This library uses a binary of [wkhtmltopdf](https://wkhtmltopdf.org/), Apache Exec, and synchronization for a robust, thread-safe HTML to PDF conversion process.

It includes the binaries for Windows, Mac & Linux and will decide which to use based on the current operating system.  
Optionally you can provide your own [wkhtmltopdf](https://wkhtmltopdf.org/) binary by setting the `WKHTMLTOPDF_BINARY` environment variable.

#### Limitations

This library aims at simplifying the usage of [wkhtmltopdf](https://wkhtmltopdf.org/) in Java, which means this library is limited to only
what that tool can do and also shares any bugs that tool might contain.

This library is written and compiled with Java 8.  Compile from source if you need to target a lower Java version.

<br/>

## Adding this to your project

In your `build.gradle` file:
* Under `repositories`
    * Add `maven { url "https://jitpack.io" }`, making sure it's the _last_ repo declared
* Under `dependencies`
    * Add `compile 'com.github.todd-elvers:html-to-pdf-converter:2.0.0'`
    
<br/>

## Examples:

In the following example we write the resulting PDF to a temporary directory, then write it to some stream, and then the try-with-resources block
cleans up the PDF from disk:

```java
HtmlToPdfFileConverter htmlToPdfFileConverter = new HtmlToPdfFileConverter();

try (PdfFile pdfFile = htmlToPdfFileConverter.tryToConvert(html)) {
    pdfFile.writeToOutputStream(...);
}
```

<br/>

If you want to avoid the intermediate step of writing to a temporary directory and instead write directly to the destination:

```java
HtmlToPdfFileConverter htmlToPdfFileConverter = new HtmlToPdfFileConverter();
File outputFile = ...

try {
    htmlToPdfFileConverter.tryToConvert(html, outputFile);
} catch(HtmlToPdfConversionException ex) {
    ...
}
```

<br/>

## Things to know

#### PDF Generation

* `HtmlToPdfFileConverter`
    * Instantiating this class causes a wkhtmltopdf binary to be extracted from the JAR to the system's temp. directory.
        * To disable this behavior and use your own `wkhtmltopdf` binary simply create the environment variable `WKHTMLTOPDF_BINARY` and point it to your binary 
    * `tryToConvert(html)` should be used if the PDF being generated has a short-lived purpose (e.g. delivering dynamic PDFs from a webservice)
        * Generates a PDF file and writes it to the system's temp. directory which can then be streamed to a destination
    * `tryToConvert(html, outputFile)` should be used for all other PDF generation
        * Generates a PDF file and writes it to `outputFile`
    * If either of the two methods above fail, a `HtmlToPdfConversionException` is thrown which will contain the output text from the command line for easier troubleshooting. 
