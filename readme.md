html-to-pdf-converter
---------------------------------

A library for converting HTML documents to PDF documents.

<br/>

## How it works

Uses Apache Exec and a synchronization-block to ensure a thread-safe, non-locking interaction between library and the command line
tool [wkhtmltopdf](https://wkhtmltopdf.org/), which converts HTML documents to PDF documents.

This library includes the binaries for Windows, Mac & Linux and will decide which to use based on the Operating System
it is running on.  You can instead provide your own wkhtmltopdf binary by setting the `WKHTMLTOPDF_BINARY` environment variable.

<br/>

#### Limitations

This library aims at simplifying the usage of [wkhtmltopdf](https://wkhtmltopdf.org/) in Java, which means this library is limited to only
what that tool can do and also shares any bugs that tool might contain.

<br/>

## Things to know

* The `HtmlToPdfFileConverter` class does the converting and has two methods of interest:
  * `tryToConvert(html)` which writes the generated PDF to a file in the temporary directory so you can then write it wherever you'd like
  * `tryToConvert(html, outputFile)` which writes the generated PDF to the specified output file
* When `HtmlToPdfFileConverter` is instantiated for the first time using the no-arg constructor a wkhtmltopdf binary will be extracted from the JAR to a temporary folder
* The `PdfFile` class that is returned from `HtmlToPdfFileConverter` has a `close()` method which will delete the file from disk
* When the HTML -> PDF conversion process fails, the exception `HtmlToPdfConversionException` is thrown which will contain the output
text from the command line for easier troubleshooting.

<br/>

## Usage Examples:

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