import { useState } from "react";
import styles from "./attachmentcard.module.css";
import { formatDate } from "../../../Utility/Helpers/DateHelpers";
import PrimaryButton from "../../Buttons/PrimaryButton/PrimaryButton";
import { PrimaryModal } from "../../Modal/PrimaryModal";
import TextArea from "../../Inputs/TextArea/TextArea";
import { NotesApi } from "../../../Utility/Api/NotesApi";

interface AttachmentCardProps {
  fileName: string;
  date: Date;
  downloadFn: () => void;
  processId: number;
  proveId: number;
  notes?: { content: string; createdAt: string; authorName: string }[];
  triggerRenderFn: () => void;
}

export const AttachmentCard = ({
  fileName,
  date,
  downloadFn,
  processId,
  proveId,
  notes,
  triggerRenderFn
}: AttachmentCardProps) => {
  const [showNoteModal, setShowNoteModal] = useState(false);
  const [newNoteContent, setNewNoteContent] = useState("");

  const handleSaveNote = async () => {
    const res = await NotesApi.createNote(processId, {
      processId: null,
      proveId,
      content: newNoteContent,
    });
    if (!res.success) return;
    setShowNoteModal(false);
    setNewNoteContent("");
    triggerRenderFn();
  };

  return (
    <div className={styles["attachment-card"]}>
      <div className={styles["card-container"]}>
        <div className={styles["image-container"]}>
          <span className="material-symbols-outlined">insert_drive_file</span>
        </div>
        <div className={styles["card-body"]}>
          <span>{fileName}</span>
          <span>{formatDate(date)}</span>
        </div>
        <div className={styles["button-container"]}>
          <button onClick={downloadFn} className={styles["download-button"]}>
            <span
              className={`material-symbols-outlined ${styles["card-download-button"]}`}>
              Download
            </span>
          </button>
        </div>
        <div className={styles["new-note-button-container"]}>
          <button
            onClick={() => setShowNoteModal(true)}
            className={styles["download-button"]}>
            <span
              className={`material-symbols-outlined ${styles["card-download-button"]}`}>
              note_add
            </span>
          </button>
        </div>
        <PrimaryModal
          open={showNoteModal}
          onClose={() => setShowNoteModal(false)}
          header="Adicionar Nota"
          body={
            <div className={styles["add-note-container"]}>
              <TextArea
                label="Conteúdo da Nota"
                value={newNoteContent}
                onChange={setNewNoteContent}
              />
              <div className={styles["upload-note-button-container"]}>
                <PrimaryButton
                  text="Salvar Nota"
                  onClick={handleSaveNote}
                  enabled={true}
                />
              </div>
            </div>
          }
        />
      </div>
      {notes && notes.length > 0 && (
        <div className={styles["notes-container"]}>
          {notes.map((note, index) => (
            <div key={index} className={styles["note"]}>
              <span className="paragraph-s-regular">{note.content}</span>
              <span className="paragraph-s-regular">
                {formatDate(new Date(note.createdAt))}
              </span>
              <span className="paragraph-s-regular">{note.authorName}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
