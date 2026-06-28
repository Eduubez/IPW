import { type NotesType } from "../../../Utility/Api/ProcessApi";
import styles from "./notecard.module.css";
import { formatDate } from "../../../Utility/Helpers/DateHelpers";

export const NoteCard = ({ note }: { note: NotesType }) => {
  return (
    <div className={styles["note-card"]}>
      <span>{note.content}</span>
      <span>{formatDate(new Date(note.createdAt))}</span>
      <span>{note.authorName}</span>
    </div>
  );
};
