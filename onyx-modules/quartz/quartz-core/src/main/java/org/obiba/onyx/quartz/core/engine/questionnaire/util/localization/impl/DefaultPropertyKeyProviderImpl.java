package org.obiba.onyx.quartz.core.engine.questionnaire.util.localization.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.obiba.onyx.quartz.core.engine.questionnaire.ILocalizable;
import org.obiba.onyx.quartz.core.engine.questionnaire.IVisitor;
import org.obiba.onyx.quartz.core.engine.questionnaire.question.Category;
import org.obiba.onyx.quartz.core.engine.questionnaire.question.OpenAnswerDefinition;
import org.obiba.onyx.quartz.core.engine.questionnaire.question.Page;
import org.obiba.onyx.quartz.core.engine.questionnaire.question.Question;
import org.obiba.onyx.quartz.core.engine.questionnaire.question.QuestionCategory;
import org.obiba.onyx.quartz.core.engine.questionnaire.question.Questionnaire;
import org.obiba.onyx.quartz.core.engine.questionnaire.question.Section;
import org.obiba.onyx.quartz.core.engine.questionnaire.util.localization.IPropertyKeyProvider;
import org.obiba.onyx.util.data.Data;

public class DefaultPropertyKeyProviderImpl implements IPropertyKeyProvider, IVisitor {

  private String property;

  private List<String> properties;
  
  public List<String> getProperties(ILocalizable localizable) {
    this.property = null;
    localizable.accept(this);
    return properties;
  }

  public String getPropertyKey(ILocalizable localizable, String property) {
    this.property = property;
    localizable.accept(this);
    return localizable.getClass().getSimpleName() + "." + localizable.getName() + "." + property;
  }

  protected List<String> getQuestionnaireProperties() {
    return Arrays.asList("label", "description", "labelNext", "imageNext", "labelPrevious", "imagePrevious", "labelStart", "labelFinish", "labelInterrupt", "labelResume", "labelCancel");
  }
  
  protected List<String> getSectionProperties() {
    return Arrays.asList("label");
  }
  
  protected List<String> getPageProperties() {
    return Arrays.asList("label");
  }
  
  protected List<String> getQuestionProperties() {
    return Arrays.asList("label", "instructions", "caption", "help", "image" );
  }
  
  protected List<String> getCategoryProperties() {
    return Arrays.asList("label", "image" );
  }
  
  protected List<String> getOpenAnswerDefinitionProperties() {
    return new ArrayList<String>(Arrays.asList("label", "unitLabel" ));
  }
  
  //
  // visitor methods
  //
  public void visit(Questionnaire questionnaire) {
    properties = getQuestionnaireProperties();

    if(property != null && !properties.contains(property)) {
      throw invalidPropertyException(questionnaire);
    }
  }

  public void visit(Section section) {
    properties = getSectionProperties();

    if(property != null && !properties.contains(property)) {
      throw invalidPropertyException(section);
    }
  }

  public void visit(Page page) {
    properties = getPageProperties();

    if(property != null && !properties.contains(property)) {
      throw invalidPropertyException(page);
    }
  }

  public void visit(Question question) {
    properties = getQuestionProperties();

    if(property != null && !properties.contains(property)) {
      throw invalidPropertyException(question);
    }
  }

  public void visit(QuestionCategory questionCategory) {
    visit(questionCategory.getCategory());
  }

  public void visit(Category category) {
    properties = getCategoryProperties();

    if(property != null && !properties.contains(property)) {
      throw invalidPropertyException(category);
    }
  }

  public void visit(OpenAnswerDefinition openAnswerDefinition) {
    properties = getOpenAnswerDefinitionProperties();
    for (Data value : openAnswerDefinition.getDefaultValues()) {
      properties.add(value.getValueAsString());
    }
    
    if(property != null && !properties.contains(property)) {
      throw invalidPropertyException(openAnswerDefinition);
    }
  }

  /**
   * Exception if a requested property is not part of questionnaire element allowed properties. 
   * @param localizable
   * @return
   */
  private IllegalArgumentException invalidPropertyException(ILocalizable localizable) {
    return new IllegalArgumentException("Invalid property for class " + localizable.getClass().getName() + ": " + property);
  }
  
}
