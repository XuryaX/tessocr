# tessocr
A wrapper for Tesseract OCR , providing formatting for resulting data as well , saving in a doc for txt
I have also uploaded the required dependencies. 
The Tesseract DLLs need to be in one of the resource folders , linked in classpath. The Tessdata folder and english.txt in the linked lib folder.

The library has been used in the view package inside src.

Design

There is one main module which allows to convert the text to formatted text, which is helped by its sub modules.
Main Module -> Uses ImageToDocument class, which accepts a buffered image as an input. The interface of the submodules can be inheritted to make use of other implementations.
1. OCR -> The OCR Engine used. By Default Tesseract Engine is used.

2. SpellCheck -> A Spellchecking engine is used to correct the inaccurate detections by the OCR Engine.

3. Writers -> A Document Writing engine, which dictates on how the document would be written and in what format. Currently TextWriter and DocWriter(uses Apache POI) are present.


The image is segmented into different textual line segments(based on how distinct they are).  The textual data is converted using the OCR Engine and the Formatting is deduced using relative size and the area covered by the text.

Line Segmentation Algorithms & Image Processing Techniques have been used to build this API. 
