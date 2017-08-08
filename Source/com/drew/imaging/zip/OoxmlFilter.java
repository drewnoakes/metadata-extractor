package com.drew.imaging.zip;

import com.drew.imaging.FileType;
import com.sun.deploy.xml.XMLAttribute;
import com.sun.deploy.xml.XMLNode;
import com.sun.deploy.xml.XMLParser;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Payton Garland
 */
public class OoxmlFilter implements ZipFilter
{
    private boolean containsRels;
    private boolean isDocument;
    private boolean isWorkbook;
    private boolean isPresentation;

    public OoxmlFilter()
    {
        containsRels = false;
        isDocument = false;
        isWorkbook = false;
        isPresentation = false;
    }

    @Override
    public FileType getFileType()
    {
        if (containsRels) {
            if (isDocument) {
                return FileType.Docx;
            } else if (isWorkbook) {
                return FileType.Xlsx;
            } else if (isPresentation) {
                return FileType.Pptx;
            }
        }
        return FileType.Zip;
    }

    @Override
    public void filterEntry(ZipEntry entry, ZipInputStream inputStream)
    {
        if (entry.getName().equals("_rels/.rels")) {
            containsRels = true;
            detectOoxmlType(inputStream);
        }
    }

    private void detectOoxmlType(ZipInputStream inputStream)
    {
       StringBuilder relsContent = new StringBuilder();
       try {
           for (int i = inputStream.read(); i != -1; i = inputStream.read()) {
                relsContent.append((char)i);
           }

           XMLParser xmlParser = new XMLParser(relsContent.toString());
           XMLNode node = xmlParser.parse();
           if (node.getName().equals("Relationships")) {
               node = node.getNested();
               while (node != null) {
                   XMLAttribute attribute = node.getAttributes();
                   while (attribute != null) {
                       if (attribute.getValue().equals("http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument")) {
                           attribute = attribute.getNext();
                           if (attribute.getValue().equals("word/document.xml")) {
                               isDocument = true;
                           } else if (attribute.getValue().equals("xl/workbook.xml")) {
                               isWorkbook = true;
                           } else if (attribute.getValue().equals("ppt/presentation.xml")) {
                               isPresentation = true;
                           }
                       }
                       attribute = attribute.getNext();
                   }
                   node = node.getNext();
               }
           }
       } catch (IOException ignored) {

       } catch (SAXException ignored) {

       }
    }
}
