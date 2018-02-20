/*
 * Copyright (c) 2017 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master Subscription
 * Agreement (or other master license agreement) separately entered into in writing between
 * you and MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.tools.migration.helper;

import com.mulesoft.tools.migration.task.step.MigrationStep;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class DocumentHelper {

  public static Document getDocument(String path) throws Exception {
    SAXBuilder saxBuilder = new SAXBuilder();
    File file = new File(path);
    Document document = saxBuilder.build(file);
    return document;
  }

  public static void restoreTestDocument(Document doc, String path) throws Exception {
    XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
    xmlOutputter.output(doc, new FileOutputStream(path));
  }

  public static List<Element> getElementsFromDocument(Document doc, String xPathExpression) {
    XPathExpression<Element> xpath =
        XPathFactory.instance().compile(xPathExpression, Filters.element(), null, doc.getRootElement().getAdditionalNamespaces());
    List<Element> nodes = xpath.evaluate(doc);
    return nodes;
  }

  public static void getNodesFromFile(String Xpath, MigrationStep step, String filePath) throws Exception {
    Document document = getDocument(filePath);
    List<Element> nodes = getElementsFromDocument(document, Xpath);
    step.setDocument(document);
    step.setNodes(nodes);
  }
}
