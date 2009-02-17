/*******************************************************************************
 * Copyright 2008(c) The OBiBa Consortium. All rights reserved.
 * 
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.obiba.onyx.quartz.core.engine.questionnaire.util.builder;

import org.obiba.onyx.core.domain.participant.Gender;
import org.obiba.onyx.quartz.core.engine.questionnaire.answer.DataSource;
import org.obiba.onyx.quartz.core.engine.questionnaire.condition.AnswerCondition;
import org.obiba.onyx.quartz.core.engine.questionnaire.condition.Condition;
import org.obiba.onyx.quartz.core.engine.questionnaire.condition.ConditionOperator;
import org.obiba.onyx.quartz.core.engine.questionnaire.condition.DataCondition;
import org.obiba.onyx.quartz.core.engine.questionnaire.condition.ExternalAnswerCondition;
import org.obiba.onyx.quartz.core.engine.questionnaire.condition.MultipleCondition;
import org.obiba.onyx.quartz.core.engine.questionnaire.condition.NotCondition;
import org.obiba.onyx.quartz.core.engine.questionnaire.question.Category;
import org.obiba.onyx.quartz.core.engine.questionnaire.question.Question;
import org.obiba.onyx.quartz.core.engine.questionnaire.util.QuestionnaireBuilder;
import org.obiba.onyx.quartz.core.engine.questionnaire.util.QuestionnaireFinder;
import org.obiba.onyx.util.data.ComparisonOperator;
import org.obiba.onyx.util.data.Data;
import org.obiba.onyx.util.data.DataBuilder;

public class ConditionBuilder extends AbstractQuestionnaireElementBuilder<Condition> {

  /**
   * Constructor build around a pre-existing {@link Condition}.
   * @param parent
   * @param condition
   */
  private ConditionBuilder(QuestionnaireBuilder parent, Condition condition) {
    super(parent);
    this.element = condition;
  }

  /**
   * Constructor using {@link QuestionBuilder} to get the {@link Question} it is applied to.
   * @param parent
   * @param condition
   */
  private ConditionBuilder(QuestionBuilder parent, Condition condition) {
    super(parent);
    this.element = condition;
    this.element.addQuestion(parent.getElement());
  }

  /**
   * Constructor of an {@link AnswerCondition}.
   * @param parent
   * @param name
   * @param questionName
   * @param categoryName
   */
  private ConditionBuilder(QuestionBuilder parent, String name, String questionName, String categoryName) {
    super(parent);

    this.element = getValidAnswerCondition(name, questionName, categoryName);
    this.element.addQuestion(parent.getElement());
  }

  /**
   * Constructor of an {@link ExternalAnswerCondition}.
   * @param parent
   * @param name
   * @param questionnaireName
   * @param questionName
   * @param categoryName
   */
  private ConditionBuilder(QuestionBuilder parent, String name, String questionnaireName, String questionName, String categoryName) {
    super(parent);

    if(QuestionnaireFinder.getInstance(questionnaire).findCondition(name) != null) throw invalidNameUnicityException(Condition.class, name);

    element = new ExternalAnswerCondition(name, questionnaireName, questionName, categoryName);
    element.addQuestion(parent.getElement());
  }

  /**
   * Constructor with condition based on data from data source.
   * @param parent
   * @param name
   * @param dataSource1
   * @param comparisonOperator
   * @param dataSource2
   */
  private ConditionBuilder(QuestionBuilder parent, String name, DataSource dataSource1, ComparisonOperator comparisonOperator, DataSource dataSource2) {
    super(parent);

    element = new DataCondition(name, dataSource1, comparisonOperator, dataSource2);
    element.addQuestion(parent.getElement());
  }

  /**
   * Constructor of Multiple Condition
   * @param parent
   * @param name
   * @param operator
   */
  private ConditionBuilder(QuestionBuilder parent, String name, ConditionOperator operator) {
    super(parent);

    MultipleCondition multipleCondition = new MultipleCondition(name, operator);
    multipleCondition.addQuestion(parent.getElement());
    element = multipleCondition;
  }

  /**
   * Constructor of NoAnswerCondition
   * @param parent
   * @param name
   */
  private ConditionBuilder(QuestionBuilder parent, String name) {
    super(parent);

    NotCondition noAnswerCondition = new NotCondition(name);
    noAnswerCondition.addQuestion(parent.getElement());
    element = noAnswerCondition;
  }

  /**
   * Add a {@link AnswerCondition} to current {@link Question}.
   * @param name
   * @param question
   * @param category
   * @param openAnswerDefinition
   * @return
   */
  public static ConditionBuilder createQuestionCondition(QuestionBuilder questionBuilder, String name, String question, String category) {
    return new ConditionBuilder(questionBuilder, name, question, category);
  }

  /**
   * Add a {@link ExternalAnswerCondition} to current {@link Question}.
   * @param name
   * @param questionnaireName
   * @param question
   * @param category
   * @param openAnswerDefinition
   * @return
   */
  public static ConditionBuilder createQuestionCondition(QuestionBuilder questionBuilder, String name, String questionnaireName, String question, String category) {
    return new ConditionBuilder(questionBuilder, name, questionnaireName, question, category);
  }

  /**
   * Add a {@link DataCondition} to current {@link Question}.
   * @param questionBuilder
   * @param name
   * @param questionnaireName
   * @param questionName
   * @param categoryName
   * @param openAnswerDefinitionName
   * @param comparisonOperator
   * @param data
   * @return
   */
  public static ConditionBuilder createQuestionCondition(QuestionBuilder questionBuilder, String name, String questionnaireName, String questionName, String categoryName, String openAnswerDefinitionName, ComparisonOperator comparisonOperator, Data data) {
    return (new ConditionBuilder(questionBuilder, name, DataSourceBuilder.createExternalOpenAnswerSource(questionnaireName, questionName, categoryName, openAnswerDefinitionName).getDataSource(), comparisonOperator, DataSourceBuilder.createFixedSource(data).getDataSource()));
  }

  /**
   * Add a {@link DataCondition} to current {@link Question}.
   * @param questionBuilder
   * @param questionName
   * @param categoryName
   * @param dataType
   * @param comparisonOperator
   * @return
   */
  public static ConditionBuilder createQuestionCondition(QuestionBuilder questionBuilder, String name, String questionName, String categoryName, String openAnswerDefinitionName, ComparisonOperator comparisonOperator, Data data) {
    return (new ConditionBuilder(questionBuilder, name, DataSourceBuilder.createOpenAnswerSource(questionBuilder, questionName, categoryName, openAnswerDefinitionName).getDataSource(), comparisonOperator, DataSourceBuilder.createFixedSource(data).getDataSource()));
  }

  /**
   * @param questionBuilder
   * @param name
   * @param question
   * @param category
   * @param openAnswerDefinition
   * @param comparisonOperator
   * @param dataSource
   * @return
   */
  public static ConditionBuilder createQuestionCondition(QuestionBuilder questionBuilder, String name, String questionName, String categoryName, String openAnswerDefinitionName, ComparisonOperator comparisonOperator, DataSource dataSource) {
    return (new ConditionBuilder(questionBuilder, name, DataSourceBuilder.createOpenAnswerSource(questionBuilder, questionName, categoryName, openAnswerDefinitionName).getDataSource(), comparisonOperator, dataSource));
  }

  /**
   * Add a {@link DataCondition} to current {@link Question}.
   * @param questionBuilder
   * @param name
   * @param question
   * @param category
   * @param openAnswerDefinition
   * @param comparisonOperator
   * @param questionName
   * @param categoryName
   * @param openAnswerDefinitionName
   * @return
   */
  public static ConditionBuilder createQuestionCondition(QuestionBuilder questionBuilder, String name, String question, String category, String openAnswerDefinition, ComparisonOperator comparisonOperator, String questionName, String categoryName, String openAnswerDefinitionName) {
    return (new ConditionBuilder(questionBuilder, name, DataSourceBuilder.createOpenAnswerSource(questionBuilder, question, category, openAnswerDefinition).getDataSource(), comparisonOperator, DataSourceBuilder.createOpenAnswerSource(questionBuilder, questionName, categoryName, openAnswerDefinitionName).getElement()));
  }

  /**
   * Add a {@link DataCondition} to current {@link Question}.
   * @param questionBuilder
   * @param name
   * @param question
   * @param category
   * @param openAnswerDefinition
   * @param comparisonOperator
   * @param questionnaireName
   * @param questionName
   * @param categoryName
   * @param openAnswerDefinitionName
   * @return
   */
  public static ConditionBuilder createQuestionCondition(QuestionBuilder questionBuilder, String name, String question, String category, String openAnswerDefinition, ComparisonOperator comparisonOperator, String questionnaireName, String questionName, String categoryName, String openAnswerDefinitionName) {
    return (new ConditionBuilder(questionBuilder, name, DataSourceBuilder.createOpenAnswerSource(questionBuilder, question, category, openAnswerDefinition).getDataSource(), comparisonOperator, DataSourceBuilder.createExternalOpenAnswerSource(questionnaireName, questionName, categoryName, openAnswerDefinitionName).getElement()));
  }

  /**
   * @param questionBuilder
   * @param name
   * @param dataSource1
   * @param comparisonOperator
   * @param dataSource2
   * @return
   */
  public static ConditionBuilder createQuestionCondition(QuestionBuilder questionBuilder, String name, DataSource dataSource1, ComparisonOperator comparisonOperator, DataSource dataSource2) {
    return new ConditionBuilder(questionBuilder, name, dataSource1, comparisonOperator, dataSource2);
  }

  /**
   * Add a {@link MultipleCondition} to current {@link Question}.
   * @param questionBuilder
   * @param name
   * @param operator
   * @return
   */
  public static ConditionBuilder createQuestionMultipleCondition(QuestionBuilder questionBuilder, String name, ConditionOperator operator) {
    return (new ConditionBuilder(questionBuilder, name, operator));
  }

  /**
   * Add a participant property {@link DataCondition} to current {@link Question}.
   * @param questionBuilder
   * @param name
   * @param participantProperty
   * @param comparisonOperator
   * @param data
   * @return
   */
  public static ConditionBuilder createQuestionCondition(QuestionBuilder questionBuilder, String name, String participantProperty, ComparisonOperator comparisonOperator, Data data) {
    return new ConditionBuilder(questionBuilder, new DataCondition(name, DataSourceBuilder.createParticipantPropertySource(participantProperty).getDataSource(), comparisonOperator, DataSourceBuilder.createFixedSource(data).getDataSource()));
  }

  /**
   * Add a participant property {@link DataCondition} to current {@link Question}.
   * @param questionBuilder
   * @param name
   * @param participantProperty
   * @param comparisonOperator
   * @param question
   * @param category
   * @param openAnswerDefinition
   * @return
   */
  public static ConditionBuilder createQuestionCondition(QuestionBuilder questionBuilder, String name, String participantProperty, ComparisonOperator comparisonOperator, String question, String category, String openAnswerDefinition) {
    return new ConditionBuilder(questionBuilder, new DataCondition(name, DataSourceBuilder.createParticipantPropertySource(participantProperty).getDataSource(), comparisonOperator, DataSourceBuilder.createOpenAnswerSource(questionBuilder, question, category, openAnswerDefinition).getDataSource()));
  }

  /**
   * Add a participant 'gender' property {@link DataCondition} to current {@link Question}.
   * @param questionBuilder
   * @param name
   * @param comparisonOperator
   * @param gender
   * @return
   */
  public static ConditionBuilder createQuestionCondition(QuestionBuilder questionBuilder, String name, ComparisonOperator comparisonOperator, Gender gender) {
    return new ConditionBuilder(questionBuilder, new DataCondition(name, DataSourceBuilder.createParticipantPropertySource("gender").getDataSource(), comparisonOperator, DataSourceBuilder.createFixedSource(DataBuilder.buildText(gender.toString())).getDataSource()));
  }

  /**
   * Add a {@link MultipleCondition} to current {@link Question}.
   * @param questionBuilder
   * @param name
   * @param operator
   * @return
   */
  public static ConditionBuilder createQuestionNotCondition(QuestionBuilder questionBuilder, String name) {
    return (new ConditionBuilder(questionBuilder, name));
  }

  /**
   * Add a {@link AnswerCondition} on the current ConditionBuilder.
   * @param name
   * @param questionName
   * @return
   */
  public ConditionBuilder withAnswerCondition(String name, String questionName) {
    return withAnswerCondition(name, questionName, null);
  }

  /**
   * Add a {@link AnswerCondition} on the current ConditionBuilder.
   * @param name
   * @param questionName
   * @param categoryName
   * @param openAnswerDefinitionName
   * @param data
   * @param comparisonOperator
   * @param occurence
   * @return
   */
  public ConditionBuilder withExternalAnswerCondition(String name, String questionnaireName, String questionName, String categoryName) {
    if(QuestionnaireFinder.getInstance(questionnaire).findCondition(name) != null) {
      throw invalidNameUnicityException(Condition.class, name);
    }
    addToCurrentCondition(new ExternalAnswerCondition(name, questionnaireName, questionName, categoryName));
    return this;
  }

  /**
   * Add a {@link AnswerCondition} on the current ConditionBuilder.
   * @param name
   * @param questionName
   * @param categoryName
   * @param openAnswerDefinitionName
   * @param data
   * @param comparisonOperator
   * @param occurence
   * @return
   */
  public ConditionBuilder withAnswerCondition(String name, String questionName, String categoryName) {
    addToCurrentCondition(getValidAnswerCondition(name, questionName, categoryName));
    return this;
  }

  /**
   * Add a {@link DataCondition} between an external open answer and a fixed data.
   * @param name
   * @param questionnaireName
   * @param questionName
   * @param categoryName
   * @param openAnswerDefinitionName
   * @param comparisonOperator
   * @param data
   * @return
   */
  public ConditionBuilder withDataCondition(String name, String questionnaireName, String questionName, String categoryName, String openAnswerDefinitionName, ComparisonOperator comparisonOperator, Data data) {
    addToCurrentCondition(new DataCondition(name, DataSourceBuilder.createExternalOpenAnswerSource(questionnaireName, questionName, categoryName, openAnswerDefinitionName).getDataSource(), comparisonOperator, DataSourceBuilder.createFixedSource(data).getDataSource()));
    return this;
  }

  /**
   * Add a {@link DataCondition} between an open answer from the currently administered questionnaire and a fied data.
   * @param name
   * @param questionName
   * @param categoryName
   * @param openAnswerDefinitionName
   * @param comparisonOperator
   * @param data
   * @return
   */
  public ConditionBuilder withDataCondition(String name, String questionName, String categoryName, String openAnswerDefinitionName, ComparisonOperator comparisonOperator, Data data) {
    addToCurrentCondition(new DataCondition(name, DataSourceBuilder.createOpenAnswerSource(this, questionName, categoryName, openAnswerDefinitionName).getDataSource(), comparisonOperator, DataSourceBuilder.createFixedSource(data).getDataSource()));
    return this;
  }

  /**
   * Add a {@link DataCondition} between two open answer of the currently administered questionnaire.
   * @param name
   * @param questionName
   * @param categoryName
   * @param openAnswerDefinitionName
   * @param comparisonOperator
   * @param questionNameToCompare
   * @param categoryNameToCompare
   * @param openAnswerDefinitionNameToCompare
   * @return
   */
  public ConditionBuilder withDataCondition(String name, String questionName, String categoryName, String openAnswerDefinitionName, ComparisonOperator comparisonOperator, String questionNameToCompare, String categoryNameToCompare, String openAnswerDefinitionNameToCompare) {
    addToCurrentCondition(new DataCondition(name, DataSourceBuilder.createOpenAnswerSource(this, questionName, categoryName, openAnswerDefinitionName).getDataSource(), comparisonOperator, DataSourceBuilder.createOpenAnswerSource(this, questionNameToCompare, categoryNameToCompare, openAnswerDefinitionNameToCompare).getElement()));
    return this;
  }

  /**
   * Add a {@link DataCondition} between an open answer and a data source of the currently administered questionnaire.
   * @param name
   * @param questionName
   * @param categoryName
   * @param openAnswerDefinitionName
   * @param comparisonOperator
   * @param dataSource
   * @return
   */
  public ConditionBuilder withDataCondition(String name, String questionName, String categoryName, String openAnswerDefinitionName, ComparisonOperator comparisonOperator, DataSource dataSource) {
    addToCurrentCondition(new DataCondition(name, DataSourceBuilder.createOpenAnswerSource(this, questionName, categoryName, openAnswerDefinitionName).getDataSource(), comparisonOperator, dataSource));
    return this;
  }

  /**
   * Add a {@link DataCondition} on a participant property compared to a open answer of the currently administered
   * questionnaire.
   * @param name
   * @param participantProperty
   * @param comparisonOperator
   * @param questionName
   * @param categoryName
   * @param openAnswerDefinitionName
   * @return
   */
  public ConditionBuilder withDataCondition(String name, String participantProperty, ComparisonOperator comparisonOperator, String questionName, String categoryName, String openAnswerDefinitionName) {
    addToCurrentCondition(new DataCondition(name, DataSourceBuilder.createParticipantPropertySource(participantProperty).getDataSource(), comparisonOperator, DataSourceBuilder.createOpenAnswerSource(this, questionName, categoryName, openAnswerDefinitionName).getDataSource()));
    return this;
  }

  /**
   * Add a {@link DataCondition} on a participant property compared to a fixed data of the currently administered
   * questionnaire.
   * @param name
   * @param participantProperty
   * @param comparisonOperator
   * @param data
   * @return
   */
  public ConditionBuilder withDataCondition(String name, String participantProperty, ComparisonOperator comparisonOperator, Data data) {
    addToCurrentCondition(new DataCondition(name, DataSourceBuilder.createParticipantPropertySource(participantProperty).getDataSource(), comparisonOperator, DataSourceBuilder.createFixedSource(data).getDataSource()));
    return this;
  }

  /**
   * Add a {@link DataCondition} on a participant property compared to a fixed text value of the currently administered
   * questionnaire.
   * @param name
   * @param participantProperty
   * @param comparisonOperator
   * @param text
   * @return
   */
  public ConditionBuilder withDataCondition(String name, String participantProperty, ComparisonOperator comparisonOperator, String text) {
    addToCurrentCondition(new DataCondition(name, DataSourceBuilder.createParticipantPropertySource(participantProperty).getDataSource(), comparisonOperator, DataSourceBuilder.createFixedSource(DataBuilder.buildText(text)).getDataSource()));
    return this;
  }

  /**
   * Add a {@link DataCondition} on a participant 'gender' property compared to given gender in the currently
   * administered questionnaire.
   * @param name
   * @param comparisonOperator
   * @param gender
   * @return
   */
  public ConditionBuilder withDataCondition(String name, ComparisonOperator comparisonOperator, Gender gender) {
    addToCurrentCondition(new DataCondition(name, DataSourceBuilder.createParticipantPropertySource("gender").getDataSource(), comparisonOperator, DataSourceBuilder.createFixedSource(DataBuilder.buildText(gender.toString())).getDataSource()));
    return this;
  }

  /**
   * Add a {@link DataCondition} on a participant property compared to a data source of the currently administered
   * questionnaire.
   * @param name
   * @param participantProperty
   * @param comparisonOperator
   * @param dataSource
   * @return
   */
  public ConditionBuilder withDataCondition(String name, String participantProperty, ComparisonOperator comparisonOperator, DataSource dataSource) {
    addToCurrentCondition(new DataCondition(name, DataSourceBuilder.createParticipantPropertySource(participantProperty).getDataSource(), comparisonOperator, dataSource));
    return this;
  }

  /**
   * Add a generic {@link DataCondition} between two {@link DataSource} of the currently administered questionnaire.
   * @param name
   * @param dataSource1
   * @param comparisonOperator
   * @param dataSource2
   * @return
   */
  public ConditionBuilder withDataCondition(String name, DataSource dataSource1, ComparisonOperator comparisonOperator, DataSource dataSource2) {
    addToCurrentCondition(new DataCondition(name, dataSource1, comparisonOperator, dataSource2));
    return this;
  }

  /**
   * Add a {@link DataCondition} between an open answer of the currently administered questionnaire and an external open
   * answer.
   * @param name
   * @param questionName
   * @param categoryName
   * @param openAnswerDefinitionName
   * @param comparisonOperator
   * @param questionnaireNameToCompare
   * @param questionNameToCompare
   * @param categoryNameToCompare
   * @param openAnswerDefinitionNameToCompare
   * @return
   */
  public ConditionBuilder withDataCondition(String name, String questionName, String categoryName, String openAnswerDefinitionName, ComparisonOperator comparisonOperator, String questionnaireNameToCompare, String questionNameToCompare, String categoryNameToCompare, String openAnswerDefinitionNameToCompare) {
    addToCurrentCondition(new DataCondition(name, DataSourceBuilder.createOpenAnswerSource(this, questionName, categoryName, openAnswerDefinitionName).getDataSource(), comparisonOperator, DataSourceBuilder.createExternalOpenAnswerSource(questionnaireNameToCompare, questionNameToCompare, categoryNameToCompare, openAnswerDefinitionNameToCompare).getDataSource()));
    return this;
  }

  /**
   * Add a {@link NotCondition} on the current ConditionBuilder.
   * @param name
   * @return
   */
  public ConditionBuilder withNoAnswerCondition(String name) {
    NotCondition noAnswerCondition = new NotCondition(name);

    if(!(element instanceof MultipleCondition)) {
      throw new IllegalArgumentException("You cannot have a no answer condition on a condition of type: " + element.getClass().getName());
    }

    ((MultipleCondition) element).getConditions().add(noAnswerCondition);
    element = noAnswerCondition;
    return this;
  }

  /**
   * Add a {@link MultipleCondition} on the current ConditionBuilder.
   * @param name
   * @return
   */
  public ConditionBuilder withMultipleCondition(String name, ConditionOperator operator) {
    MultipleCondition multipleCondition = new MultipleCondition(name, operator);

    if(element instanceof MultipleCondition) {
      ((MultipleCondition) element).getConditions().add(multipleCondition);
    } else if(element instanceof NotCondition) {
      ((NotCondition) element).setCondition(multipleCondition);
    } else {
      throw new IllegalArgumentException("You cannot have a multiple condition on a condition of type: " + element.getClass().getName());
    }

    element = multipleCondition;
    return this;
  }

  /**
   * Add given condition to current condition and make it the new current one.
   * @param condition
   */
  private void addToCurrentCondition(Condition condition) {
    if(element instanceof MultipleCondition) {
      ((MultipleCondition) element).getConditions().add(condition);
    } else if(element instanceof NotCondition) {
      ((NotCondition) element).setCondition(condition);
    }

    element = condition;
  }

  /**
   * Returns a valid {@link AnswerCondition}.
   * @param name
   * @param questionName
   * @param categoryName
   * @param data
   * @param comparisonOperator
   * @param occurence
   * @return
   */
  private AnswerCondition getValidAnswerCondition(String name, String questionName, String categoryName) {
    if(QuestionnaireFinder.getInstance(questionnaire).findCondition(name) != null) throw invalidNameUnicityException(Condition.class, name);

    Question question = QuestionnaireFinder.getInstance(questionnaire).findQuestion(questionName);
    Category category = null;
    if(question == null) throw invalidElementNameException(Question.class, questionName);

    AnswerCondition answerCondition = new AnswerCondition(name, question, null);

    if(categoryName != null) {
      if(question.getCategories().size() > 0) {
        category = question.findCategory(categoryName);
        if(category == null) throw invalidElementNameException(Category.class, categoryName);
      } else {
        Question parentQuestion = question.getParentQuestion();
        category = parentQuestion.findCategory(categoryName);
        if(category == null) throw invalidElementNameException(Category.class, categoryName);
      }
      answerCondition.setCategory(category);
    }

    return (answerCondition);
  }

  /**
   * Set the given {@link Condition} as the current one.
   * @param questionnaire
   * @param condition
   * @return
   */
  public static ConditionBuilder inCondition(QuestionnaireBuilder parent, Condition condition) {
    return new ConditionBuilder(parent, condition);
  }
}
