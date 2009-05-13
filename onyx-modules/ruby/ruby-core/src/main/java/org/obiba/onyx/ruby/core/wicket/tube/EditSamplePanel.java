/*******************************************************************************
 * Copyright 2008(c) The OBiBa Consortium. All rights reserved.
 * 
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.obiba.onyx.ruby.core.wicket.tube;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.obiba.onyx.ruby.core.domain.RegisteredParticipantTube;
import org.obiba.onyx.ruby.core.domain.TubeRegistrationConfiguration;
import org.obiba.onyx.ruby.core.service.ActiveTubeRegistrationService;
import org.obiba.onyx.ruby.core.wicket.wizard.TubeRegistrationPanel;
import org.obiba.onyx.wicket.model.SpringStringResourceModel;
import org.obiba.onyx.wicket.reusable.Dialog;
import org.obiba.onyx.wicket.reusable.FeedbackWindow;
import org.obiba.onyx.wicket.reusable.Dialog.CloseButtonCallback;
import org.obiba.onyx.wicket.reusable.Dialog.Status;
import org.obiba.onyx.wicket.reusable.Dialog.WindowClosedCallback;

public class EditSamplePanel extends Panel {

  private static final long serialVersionUID = 1L;

  @SpringBean
  ActiveTubeRegistrationService activeTubeRegistrationService;

  private CommentPanel commentPanel;

  private RemarkSelectorPanel remarkPanel;

  private FeedbackWindow feedbackWindow;

  public EditSamplePanel(Dialog editSampleDialog, String id, IModel rowModel, TubeRegistrationConfiguration tubeRegistrationConfiguration) {
    super(id, rowModel);

    add(new Label("barcodeLabel", new SpringStringResourceModel("Ruby.Barcode")));
    add(new Label("barcode", new PropertyModel(rowModel, "barcode")));

    add(new Label("remarkLabel", new SpringStringResourceModel("Ruby.Remark")));
    add(remarkPanel = new RemarkSelectorPanel("remark", rowModel, tubeRegistrationConfiguration));

    add(new Label("commentLabel", new SpringStringResourceModel("Ruby.Comment")));
    add(commentPanel = new CommentPanel("comment", rowModel));

    add(new BarcodePartsPanel("barcodeParts", rowModel, tubeRegistrationConfiguration));

    addFeedbackWindow();
    addDialogCallbacks(editSampleDialog);

  }

  private void addFeedbackWindow() {
    feedbackWindow = new FeedbackWindow("feedback");
    feedbackWindow.setOutputMarkupId(true);
    add(feedbackWindow);
  }

  @SuppressWarnings("serial")
  private void addDialogCallbacks(final Dialog editSampleDialog) {
    editSampleDialog.setWindowClosedCallback(new WindowClosedCallback() {

      public void onClose(AjaxRequestTarget target, Status status) {
        if(status.equals(Status.SUCCESS)) {
          target.addComponent(EditSamplePanel.this.findParent(TubeRegistrationPanel.class));
        }
      }

    });

    editSampleDialog.setCloseButtonCallback(new CloseButtonCallback() {

      public boolean onCloseButtonClicked(AjaxRequestTarget target, Status status) {

        switch(status) {
        case SUCCESS:
          updateSample();
          break;
        case ERROR:
          displayFeedbackWindow(target);
          return false;

        }
        return true;

      }

    });

  }

  private void updateSample() {
    String comment = commentPanel.getCommentField().getModelObjectAsString();

    if(comment != null && comment.trim().length() == 0) {
      comment = null;
    }

    RegisteredParticipantTube registeredParticipantTube = (RegisteredParticipantTube) getModelObject();
    String participantBarcode = registeredParticipantTube.getBarcode();
    activeTubeRegistrationService.setTubeComment(participantBarcode, comment);
    activeTubeRegistrationService.setTubeRemark(participantBarcode, remarkPanel.getSelectedRemark());

  }

  private void displayFeedbackWindow(AjaxRequestTarget target) {
    feedbackWindow.setContent(new FeedbackPanel("content"));
    feedbackWindow.show(target);
  }

}