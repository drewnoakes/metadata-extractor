package com.drew.imaging.zip;

import com.drew.imaging.FileType;
import com.sun.deploy.xml.XMLAttribute;
import com.sun.deploy.xml.XMLNode;
import com.sun.deploy.xml.XMLParser;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Payton Garland
 */
public class OoxmlFilter extends ZipFilter
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
    public void filterEntry(ZipEntry entry, ZipInputStream inputStream)
    {
        if (entry.getName().equals("_rels/.rels")) {
            containsRels = true;
            detectOoxmlType(inputStream);
        }
    }

    @Override
    HashMap<List<Boolean>, FileType> addConditions()
    {
        HashMap<List<Boolean>, FileType> conditionsMap = new HashMap<List<Boolean>, FileType>();

        List<Boolean> docx = Arrays.asList(containsRels, isDocument);
        conditionsMap.put(docx, FileType.Docx);
        List<Boolean> pptx = Arrays.asList(containsRels, isPresentation);
        conditionsMap.put(pptx, FileType.Pptx);
        List<Boolean> xlsx = Arrays.asList(containsRels, isWorkbook);
        conditionsMap.put(xlsx, FileType.Xlsx);

        return conditionsMap;
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
