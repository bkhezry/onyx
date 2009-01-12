/*******************************************************************************
 * Copyright 2008(c) The OBiBa Consortium. All rights reserved.
 * 
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.obiba.onyx.engine.variable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.obiba.onyx.util.data.Data;
import org.obiba.onyx.util.data.DataType;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * 
 */
@XStreamAlias("variableData")
public class VariableData implements Serializable {

  private static final long serialVersionUID = 1L;

  // public static final String ENCODING = "ISO-8859-1";
  //
  // public static final String QUERY = "/data?";
  //
  // public static final String QUERY_KEY_VALUE_SEPARATOR = "=";
  //
  // public static final String QUERY_STATEMENT_SEPARATOR = "&";

  @XStreamAsAttribute
  private String path;

  @XStreamAsAttribute
  private DataType type;

  @XStreamImplicit
  private List<Serializable> values;

  @XStreamImplicit
  private List<VariableData> variableDatas;

  public VariableData(String variablePath) {
    super();
    this.path = variablePath;
  }

  public VariableData(String variablePath, Data data) {
    super();
    this.path = variablePath;
    addData(data);
  }

  // public String getPath() {
  // String path = "";
  // if(variablePath != null) {
  // path = variablePath;
  // }
  // if(data != null) {
  // if(path.length() > 0) {
  // path += QUERY + "value" + QUERY_KEY_VALUE_SEPARATOR;
  // }
  // try {
  // path += URLEncoder.encode(data.getValueAsString(), ENCODING);
  // } catch(UnsupportedEncodingException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // }
  // return path;
  // }

  public String getVariablePath() {
    return path;
  }

  public void setVariablePath(String variablePath) {
    this.path = variablePath;
  }

  public List<Serializable> getValues() {
    return values != null ? values : (values = new ArrayList<Serializable>());
  }

  public List<Data> getDatas() {
    List<Data> datas = new ArrayList<Data>();

    for(Serializable value : getValues()) {
      datas.add(new Data(type, value));
    }

    return datas;
  }

  public VariableData addData(Data child) {
    if(child != null) {
      getDatas().add(child);
      Serializable value = child.getValue();
      getValues().add(value);
      if(type != null && !type.equals(child.getType())) {
        throw new IllegalArgumentException("Cannot mix values from different data types: " + type + " is current, " + child.getType() + " is added.");
      }
      type = child.getType();
    }
    return this;
  }

  public List<VariableData> getVariableDatas() {
    return variableDatas != null ? variableDatas : (variableDatas = new ArrayList<VariableData>());
  }

  /**
   * Cross referring variable data.
   * @param child
   * @return this for chaining
   */
  public VariableData addVariableData(VariableData child) {
    if(child != null) {
      getVariableDatas().add(child);
    }
    return this;
  }

  public VariableData addVariableDatas(VariableData... variableDatas) {
    if(variableDatas != null) {
      for(VariableData ref : variableDatas) {
        addVariableData(ref);
      }
    }
    return this;
  }

}
