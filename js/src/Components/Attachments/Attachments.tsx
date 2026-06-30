import styles from "./attachments.module.css";
import { AttachmentCard } from "../Cards/AttachmentCard/AttachmentCard";
import { type ProveResponse } from "../../Utility/Api/ProvesApi";
import PrimaryButton from "../Buttons/PrimaryButton/PrimaryButton";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { ProvesApi } from "../../Utility/Api/ProvesApi";
import { useParams } from "react-router-dom";
import { UploadAttachmentModal } from "./UploadAttachmentModal/UploadAttachmentModal";
import { SlideShow } from "../SlideShow/SlideShow";
import { NotesApi } from "../../Utility/Api/NotesApi";
const buttonStyle = {
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
  width: "2rem",
  height: "2rem",
  borderRadius: "50%",
  backgroundColor: "var(--color-dark-blue-100)",
  color: "white",
  fontSize: "1.5rem",
  cursor: "pointer",
};

export const Attachments = ({
  proves,
  triggerRenderFn,
  buttonsEnabled = true,
}: {
  proves: ProveResponse[] | undefined;
  triggerRenderFn: () => void;
  buttonsEnabled?: boolean;
}) => {
  const params = useParams();
  const processId = Number(params.id);
  const { t } = useTranslation();
  const attachments = proves || [];
  const [showNewAttachmentModal, setShowNewAttachmentModal] = useState(false);
  const [notes, setNotes] = useState<
    { proveId: number | null; content: string; createdAt: string; authorName: string }[]
  >([]);

  useEffect(() => {
    if (!proves || proves.length === 0) return;

    let cancelled = false;

    const fetchAllNotes = async () => {
      const results = await Promise.all(
        proves.map((prove) => NotesApi.getAllByProveId(prove.id, processId)),
      );

      if (cancelled) return;

      const allNotes = results.flatMap((res) => {
        if (!res.success) return [];
        return res.data.results.map((note) => ({
          proveId: note.provesId,
          content: note.content,
          createdAt: note.createdAt,
          authorName: note.authorName,
        }));
      });

      setNotes(allNotes);
    };

    fetchAllNotes();

    return () => {
      cancelled = true;
    };
  }, [proves, processId]);

  const attachmentsWithNotes = attachments.map((attachment) => {
    const attachmentNotes = notes.filter((note) => note.proveId === attachment.id);
    return { ...attachment, notes: attachmentNotes };
  });
 


  const [file, setFile] = useState<File | null>(null);

  const handleFileUpload = async () => {
    if (!file) return;

    const urlRes = await ProvesApi.createUploadUrl(processId, file);
    if (!urlRes.success) return;

    const uploadRes = await ProvesApi.uploadFile(urlRes.data.uploadUrl, file);
    if (!uploadRes.success) return;

    await ProvesApi.create(processId, file, urlRes.data.storageKey);

    setShowNewAttachmentModal(false);
    triggerRenderFn();
  };

  const handleDownload = async (processId: number, proveId: number) => {
    const accessUrlRes = await ProvesApi.getAccessUrl(processId, proveId);

    if (!accessUrlRes.success) return;
    const url = accessUrlRes.data.url;

    const link = document.createElement("a");
    link.href = url;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handleDeleteAttachment = async (processId:number,proveId: number) => {
    await ProvesApi.delete(processId, proveId);
    triggerRenderFn();
  }

  const handleCloseModal = () => {
    setShowNewAttachmentModal(false);
    setFile(null);
  };

  return (
    <div className={styles["attachments-container"]}>
      <div className={styles["attachments-header"]}>
        <span>{t("ProcessPage.attachmentsSection")}</span>
        <PrimaryButton
          text="+"
          onClick={() => setShowNewAttachmentModal(true)}
          enabled={buttonsEnabled}
          style={{ ...buttonStyle, backgroundColor: buttonsEnabled ? 'var(--color-dark-blue-100)' : 'gray' }}
        />
      </div>
      {attachments === undefined || attachments.length === 0 ? (
        <div className={styles["no-attachments"]}>
          <span>{t("ProcessPage.noAttachments")}</span>
        </div>
      ) : (
        <div className={styles["attachments-body"]}>
          <SlideShow
            offContentArrow={true}
            content={attachmentsWithNotes.map((attachment, index) => (
              <AttachmentCard
                key={index}
                authorName={attachment.authorName}
                fileName={attachment.fileName}
                date={new Date(attachment.createdAt)}
                downloadFn={() => handleDownload(processId, attachment.id)}
                notes={attachment.notes}
                triggerRenderFn={triggerRenderFn}
                processId={processId}
                proveId={attachment.id}
                deleteFn={() => handleDeleteAttachment(processId,attachment.id)}
                buttonsEnabled={buttonsEnabled}
              />
            ))}
          />
        </div>
      )}
      <UploadAttachmentModal
        isOpen={showNewAttachmentModal}
        onClose={handleCloseModal}
        file={file}
        setFile={(e) => setFile(e.target.files ? e.target.files[0] : null)}
        handleFileUpload={handleFileUpload}
      />
    </div>
  );
};
