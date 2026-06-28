import { PrimaryModal } from "../../Modal/PrimaryModal";
import PrimaryButton from "../../Buttons/PrimaryButton/PrimaryButton";
import styles from "./uploadattachment.module.css";
import { useTranslation } from "react-i18next";

interface UploadAttachmentModalProps {
  isOpen: boolean;
  onClose: () => void;
  file: File | null;
  setFile: (event: React.ChangeEvent<HTMLInputElement>) => void;
  handleFileUpload: () => void;
}

export const UploadAttachmentModal = ({
  isOpen,
  onClose,
  file,
  setFile,
  handleFileUpload,
}: UploadAttachmentModalProps) => {
  const { t } = useTranslation();

  const modalBody = () => {
    let isUploadButtonEnabled = file !== null;



    const handlePrimaryButtonClick = () => {
      isUploadButtonEnabled = false;

      handleFileUpload();
      onClose();
    };

    return (
      <div className={styles["upload-attachment-modal-body"]}>
        <input type="file" onChange={setFile} />
        <div className={styles["button-container"]}>
          <PrimaryButton
            text={t("UploadAttachmentModal.uploadButton")}
            onClick={handlePrimaryButtonClick}
            enabled={isUploadButtonEnabled}
          />
        </div>
      </div>
    );
  };

  return (
    <PrimaryModal
      open={isOpen}
      onClose={onClose}
      header={t("UploadAttachmentModal.header")}
      body={modalBody()}
    />
  );
};
