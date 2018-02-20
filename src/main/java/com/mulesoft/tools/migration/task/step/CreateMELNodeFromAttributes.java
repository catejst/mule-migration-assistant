/*
 * Copyright (c) 2017 MuleSoft, Inc. This software is protected under international
 * copyright law. All use of this software is subject to MuleSoft's Master Subscription
 * Agreement (or other master license agreement) separately entered into in writing between
 * you and MuleSoft. If such an agreement is not in place, you may not use the software.
 */
package com.mulesoft.tools.migration.task.step;

import com.mulesoft.tools.migration.exception.MigrationStepException;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.util.*;

import static com.mulesoft.tools.migration.tools.mel.MELUtils.getMELExpressionFromMap;
import static com.mulesoft.tools.migration.report.ReportCategory.RULE_APPLIED;

/**
 * Transform an attribute in a mel child node
 * 
 * @author Mulesoft Inc.
 * @since 1.0.0
 */
public class CreateMELNodeFromAttributes extends MigrationStep {

  private String originalNode;

  private String newNodeName;

  private String newNodeNamespace;

  private String newNodeNamespaceUri;

  private String attributeKeyName;

  private String attributeValueName;

  public CreateMELNodeFromAttributes(String originalNode, String newNodeName, String newNodeNamespace,
                                     String newNodeNamespaceUri, String attributeKeyName, String attributeValueName) {
    setOriginalNode(originalNode);
    setNewNodeName(newNodeName);
    setNewNodeNamespace(newNodeNamespace);
    setNewNodeNamespaceUri(newNodeNamespaceUri);
    setAttributeKeyName(attributeKeyName);
    setAttributeValueName(attributeValueName);
  }

  public CreateMELNodeFromAttributes() {}

  public void execute() throws Exception {
    try {
      for (Element node : getNodes()) {
        Map<String, String> attributesMap = new HashMap<>();
        List<Element> elementsToRemove = new ArrayList<>();

        for (Element childNode : node.getChildren()) {
          if (childNode.getName().equals(getOriginalNode())) {
            Attribute keyAttribute = childNode.getAttribute(getAttributeKeyName());
            Attribute valueAttribute = childNode.getAttribute(getAttributeValueName());
            if (null != keyAttribute && null != valueAttribute) {
              elementsToRemove.add(childNode);
              attributesMap.put(keyAttribute.getValue(), valueAttribute.getValue());
            }
          }
        }

        if (attributesMap.size() > 0) {
          for (Element elementToRemove : elementsToRemove) {
            node.removeContent(elementToRemove);
          }
          Namespace newNodeNamespace = Namespace.getNamespace(getNewNodeNamespace(), getNewNodeNamespaceUri());
          Element child = new Element(getNewNodeName(), newNodeNamespace);
          child.setText(getMELExpressionFromMap(attributesMap));
          node.addContent(child);

          getReportingStrategy().log("MEL node <" + child.getQualifiedName() + "> was created, attributes-map:" + attributesMap,
                                     RULE_APPLIED, this.getDocument().getBaseURI(), null, this);
        }
      }
    } catch (Exception ex) {
      throw new MigrationStepException("Create mel node from attributes exception. " + ex.getMessage());
    }
  }

  public String getOriginalNode() {
    return originalNode;
  }

  public void setOriginalNode(String originalNode) {
    this.originalNode = originalNode;
  }

  public String getNewNodeName() {
    return newNodeName;
  }

  public void setNewNodeName(String newNodeName) {
    this.newNodeName = newNodeName;
  }

  public String getNewNodeNamespace() {
    return newNodeNamespace;
  }

  public void setNewNodeNamespace(String newNodeNamespace) {
    this.newNodeNamespace = newNodeNamespace;
  }

  public String getNewNodeNamespaceUri() {
    return newNodeNamespaceUri;
  }

  public void setNewNodeNamespaceUri(String newNodeNamespaceUri) {
    this.newNodeNamespaceUri = newNodeNamespaceUri;
  }

  public String getAttributeKeyName() {
    return attributeKeyName;
  }

  public void setAttributeKeyName(String attributeKeyName) {
    this.attributeKeyName = attributeKeyName;
  }

  public String getAttributeValueName() {
    return attributeValueName;
  }

  public void setAttributeValueName(String attributeValueName) {
    this.attributeValueName = attributeValueName;
  }
}
