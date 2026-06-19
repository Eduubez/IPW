import styles from "./attachmentcard.module.css";

interface AttachmentCardProps {
  fileName: string;
  fileSize: string;
  downloadFn: () => void;
}

export const AttachmentCard = ({
  fileName,
  fileSize,
  downloadFn,
}: AttachmentCardProps) => {
  return (
    <div className={styles["card-container"]}>
      <div className={styles["image-container"]}>
        <span className="material-symbols-outlined">insert_drive_file</span>
      </div>
      <div className={styles["card-body"]}>
        <span>{fileName}</span>
        <span>{fileSize}</span>
      </div>
      <div className={styles["button-container"]}>
          <button onClick={downloadFn} className={styles["download-button"]}>
            <span
              className={`material-symbols-outlined ${styles["card-download-button"]}`}>
              Download
            </span>
          </button>
      </div>
    </div>
  );
};
