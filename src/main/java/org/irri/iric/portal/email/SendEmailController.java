package org.irri.iric.portal.email;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.irri.iric.portal.MailUtils;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Vlayout;

public class SendEmailController extends SelectorComposer<Vlayout> {

	private static final long serialVersionUID = 1L;

	@Wire
	private Textbox txtTo;
	@Wire
	private Textbox txtBcc;
	@Wire
	private Textbox txtSubject;
	@Wire
	private Textbox txtBody;
	@Wire
	private Label lblStatus;

	private List<Media> mediaList;
	
	@Wire
	private Vbox attachmentVb;

	// Max file size in bytes (e.g., 5 MB)
	final int MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

	// Allowed extensions (lowercase for simplicity)
	Set<String> allowedExtensions = new HashSet<>(Arrays.asList("jpg", "png", "pdf", "jpeg"));

	@Override
	public void doAfterCompose(Vlayout comp) throws Exception {
		super.doAfterCompose(comp);
	}

	@Listen("onClick=#btnSend")
	public void sendEmail() {
		String to = txtTo.getValue().trim();
		String subject = txtSubject.getValue().trim();
		String body = txtBody.getValue().trim();

		// basic validation
		if (to.isEmpty() || subject.isEmpty() || body.isEmpty()) {
			showStatus("Please fill in To, Subject and Message.", "err");
			return;
		}

		try {
			MailUtils.sendReplyWithAttachments(to, txtBcc.getValue().trim(), subject, body, mediaList);

			// success — show prompt and reset form
			showStatus("✓ Message sent to " + to, "ok");
			resetForm();

		} catch (Exception e) {
			e.printStackTrace();
			showStatus("✗ Failed to send: " + e.getMessage(), "err");
		}
	}

	@Listen("onClick=#btnClear")
	public void onClear() {
		resetForm();
		lblStatus.setVisible(false);
	}

	private void resetForm() {
		txtTo.setValue("");
		txtBcc.setValue("");
		txtSubject.setValue("");
		txtBody.setValue("");
		attachmentVb.getChildren().clear();
		attachmentVb.invalidate();
		mediaList = null;
	}

	private void showStatus(String msg, String type) {
		// type: "ok" | "err" | "info" — matches CSS classes in sendEmail.zul
		lblStatus.setSclass("status-" + type);
		lblStatus.setValue(msg);
		lblStatus.setVisible(true);
	}

	@Listen("onUpload = #uploadBtn")
	public void handleUpload(UploadEvent event) {
		Media[] medias = event.getMedias(); // 🔥 handle multiple uploads

		mediaList = new ArrayList<Media>();

		if (medias != null && medias.length > 0) {

			int i = 1;
			if (medias.length < 5) {
				for (Media media : medias) {
					String fileName = media.getName();

					String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

					// Check file extension
					if (!allowedExtensions.contains(extension)) {
						Messagebox.show(
								"File " + fileName + " has an invalid file type. Allowed types are .jpg, .png, .pdf.");
						continue; // Skip this file
					}

					if (media.getByteData().length > MAX_FILE_SIZE) {
						Messagebox.show("The file is too large. Max size is 5 MB.");
						return;
					}

					Hbox hbox = new Hbox();

					Label lbl = new Label(fileName);
					lbl.setId("lbl" + i);
					lbl.setParent(attachmentVb);

					// Remove button
					Label removeBtn = new Label("Remove");
					removeBtn.setStyle(
							"padding-left:5px;color: red; background: transparent; border: none; cursor: pointer;");
					removeBtn.addEventListener("onClick", new EventListener<Event>() {

						@Override
						public void onEvent(Event arg0) throws Exception {
							attachmentVb.removeChild(hbox);
						}

					});

					hbox.appendChild(lbl);
					hbox.appendChild(removeBtn);

					attachmentVb.appendChild(hbox);
					mediaList.add(media);
					i++;
				}
			} else {
				Messagebox.show("Maximum of 5 attachments only.");
			}

			attachmentVb.invalidate();

		} else {
			Messagebox.show("No files were uploaded.");
		}
	}
}