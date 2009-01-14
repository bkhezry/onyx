/*******************************************************************************
 * Copyright 2008(c) The OBiBa Consortium. All rights reserved.
 * 
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.obiba.onyx.engine.variable.util;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.obiba.onyx.engine.variable.IVariablePathNamingStrategy;
import org.obiba.onyx.engine.variable.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 */
public class VariableFinder {

  private static final Logger log = LoggerFactory.getLogger(VariableFinder.class);

  public static final String ALL_VARIABLES_XPATH = "//variable[@dataType]";

  private Variable parent;

  private IVariablePathNamingStrategy variablePathNamingStrategy;

  public static VariableFinder getInstance(Variable parent, IVariablePathNamingStrategy variablePathNamingStrategy) {
    VariableFinder finder = new VariableFinder();
    finder.parent = parent;
    finder.variablePathNamingStrategy = variablePathNamingStrategy;

    return finder;
  }

  public Variable findVariable(String path) {
    if(path == null) return null;

    Variable root = getVariableRoot();

    List<String> normalizedNames = variablePathNamingStrategy.getNormalizedNames(path);

    if(normalizedNames.size() == 0) return null;
    if(!variablePathNamingStrategy.normalizeName(root.getName()).equals(normalizedNames.get(0))) return null;

    Variable current = root;
    for(int i = 1; i < normalizedNames.size(); i++) {
      String entityName = normalizedNames.get(i);
      boolean found = false;
      for(Variable child : current.getVariables()) {
        if(variablePathNamingStrategy.normalizeName(child.getName()).equals(entityName)) {
          current = child;
          found = true;
          break;
        }
      }
      if(!found) {
        current = null;
        break;
      }
    }

    return current;
  }

  public List<Variable> filter(String xpathQuery) {
    List<Variable> variables = new ArrayList<Variable>();

    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setNamespaceAware(true); // never forget this!
    try {
      DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
      Document doc = builder.parse(new ByteArrayInputStream(VariableStreamer.toXML(getVariableRoot()).getBytes()));

      XPathFactory factory = XPathFactory.newInstance();
      XPath xpath = factory.newXPath();
      XPathExpression expr = xpath.compile(xpathQuery);
      Object result = expr.evaluate(doc, XPathConstants.NODESET);
      NodeList nodes = (NodeList) result;
      for(int i = 0; i < nodes.getLength(); i++) {
        Node node = nodes.item(i);
        String path = variablePathNamingStrategy.getPath(node);
        log.info("filter.path={}", path);

        Variable variable = findVariable(path);
        if(variable != null) {
          variables.add(variable);
        }
      }

    } catch(Exception e) {
      e.printStackTrace();
    }

    log.info("filter.variables={}", variables);

    return variables;
  }

  public Variable getVariableRoot() {
    Variable root = parent;

    while(root.getParent() != null) {
      root = root.getParent();
    }

    return root;
  }

}
