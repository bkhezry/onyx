/*******************************************************************************
 * Copyright 2008(c) The OBiBa Consortium. All rights reserved.
 * 
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.obiba.onyx.quartz.editor.section;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.obiba.onyx.quartz.core.engine.questionnaire.question.IHasSection;
import org.obiba.onyx.quartz.core.engine.questionnaire.question.Questionnaire;
import org.obiba.onyx.quartz.core.engine.questionnaire.question.Section;
import org.obiba.onyx.quartz.editor.form.AbstractQuestionnaireElementPanel;
import org.obiba.onyx.quartz.editor.locale.ui.LocalesPropertiesAjaxTabbedPanel;
import org.obiba.onyx.wicket.behavior.RequiredFormFieldBehavior;

@SuppressWarnings("serial")
public abstract class SectionPropertiesPanel extends AbstractQuestionnaireElementPanel<Section> {

  private final IModel<IHasSection> parentModel;

  public SectionPropertiesPanel(String id, IModel<Section> model, IModel<IHasSection> parentModel, IModel<Questionnaire> questionnaireModel, ModalWindow modalWindow) {
    super(id, model, questionnaireModel, modalWindow);
    this.parentModel = parentModel;
    createComponent();
  }

  public void createComponent() {
    TextField<String> name = new TextField<String>("name", new PropertyModel<String>(form.getModel(), "name"), String.class);
    name.add(new RequiredFormFieldBehavior());
    name.add(new SectionUnicityValidator());
    form.add(name);
    form.add(new LocalesPropertiesAjaxTabbedPanel("localesPropertiesTabs", form.getModel(), localePropertiesModel));
  }

  private class SectionUnicityValidator extends AbstractValidator<String> {

    @Override
    protected void onValidate(IValidatable<String> validatable) {
      for(Section section : parentModel.getObject().getSections()) {
        if(section != form.getModelObject() && section.getName().equalsIgnoreCase(validatable.getValue())) {
          error(validatable, "SectionAlreadyExists");
          return;
        }
      }
    }
  }

}
