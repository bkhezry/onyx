/*******************************************************************************
 * Copyright 2008(c) The OBiBa Consortium. All rights reserved.
 * 
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.obiba.onyx.quartz.editor.form;

import java.io.File;
import java.io.IOException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.obiba.onyx.quartz.core.engine.questionnaire.IQuestionnaireElement;
import org.obiba.onyx.quartz.core.engine.questionnaire.question.Questionnaire;
import org.obiba.onyx.quartz.core.engine.questionnaire.util.QuestionnaireBuilder;
import org.obiba.onyx.quartz.core.engine.questionnaire.util.QuestionnaireCreator;

@SuppressWarnings("serial")
public abstract class AbstractQuestionnaireElementPanel<T extends IQuestionnaireElement> extends AbstractLocalePropertiesPanel<T> {

  protected final ModalWindow modalWindow;

  /**
   * 
   * @param id
   * @param model
   * @param questionnaireParent
   * @param modalWindow
   */
  public AbstractQuestionnaireElementPanel(String id, IModel<T> model, IModel<Questionnaire> questionnaireParentModel, ModalWindow modalWindow) {
    super(id, model, questionnaireParentModel);

    this.modalWindow = modalWindow;

    form.add(new AjaxButton("save", form) {

      @SuppressWarnings("unchecked")
      @Override
      public void onSubmit(AjaxRequestTarget target, Form<?> form2) {
        onSave(target, (T) form2.getModelObject());
        AbstractQuestionnaireElementPanel.this.modalWindow.close(target);
      }

      @Override
      protected void onError(AjaxRequestTarget target, Form<?> form2) {
        feedbackWindow.setContent(feedbackPanel);
        feedbackWindow.show(target);
      }
    });

    form.add(new AjaxButton("cancel", form) {

      @Override
      public void onSubmit(AjaxRequestTarget target, Form<?> form2) {
        AbstractQuestionnaireElementPanel.this.modalWindow.close(target);
      }
    }.setDefaultFormProcessing(false));
  }

  public void saveToFiles() {
    File bundleRootDirectory = new File("target\\work\\webapp\\WEB-INF\\config\\quartz\\resources", "questionnaires");
    File bundleSourceDirectory = new File("src" + File.separatorChar + "main" + File.separatorChar + "webapp" + File.separatorChar + "WEB-INF" + File.separatorChar + "config" + File.separatorChar + "quartz" + File.separatorChar + "resources", "questionnaires");

    try {
      new QuestionnaireCreator(bundleRootDirectory, bundleSourceDirectory).createQuestionnaire(QuestionnaireBuilder.getInstance((questionnaireParentModel != null ? questionnaireParentModel.getObject() : (Questionnaire) getDefaultModelObject())), getLocalePropertiesToMap());
    } catch(IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}