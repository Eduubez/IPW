import styles from "./attachments.module.css";
import { AttachmentCard } from "../Cards/AttachmentCard/AttachmentCard";
import { type ProveResponse } from "../../Utility/Api/ProvesApi";
import PrimaryButton from "../Buttons/PrimaryButton/PrimaryButton";
import { useState } from "react";
import { PrimaryModal } from "../Modal/PrimaryModal";
import { ProvesApi } from "../../Utility/Api/ProvesApi";
import { useParams } from "react-router-dom";
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

export const Attachments = ({ proves }: { proves: ProveResponse[] }) => {
  const params = useParams();
  const processId = Number(params.id);

  const attachments = proves || [];
  const [showNewAttachmentModal, setShowNewAttachmentModal] = useState(false);
  const [file, setFile] = useState<File | null>(null);

  const handleFileUpload = async () => {
    if (!file) return;

    const urlRes = await ProvesApi.createUploadUrl(processId, file);
    if (!urlRes.success) return;

    const uploadRes = await ProvesApi.uploadFile(urlRes.data.uploadUrl, file);
    if (!uploadRes.success) return;

    await ProvesApi.create(processId, file, urlRes.data.storageKey);

    setShowNewAttachmentModal(false);
  };

  const handleDownload = async (processId: number, proveId: number) => {
    const accessUrlRes = await ProvesApi.getAccessUrl(processId, proveId);

    if (!accessUrlRes.success) return;
    const url = accessUrlRes.data.url;
    const fileName = accessUrlRes.data.fileName;

    const link = document.createElement("a");
    link.href = url;
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    

  };

  return (
    <div className={styles["attachments-container"]}>
      <div className={styles["attachments-header"]}>
        <span>Anexos</span>
        <PrimaryButton
          text="+"
          onClick={() => setShowNewAttachmentModal(true)}
          enabled={true}
          style={buttonStyle}
        />
      </div>
      {attachments === undefined || attachments.length === 0 ? (
        <div className={styles["no-attachments"]}>
          <span>Nenhum anexo encontrado.</span>
        </div>
      ) : (
        <div className={styles["attachments-body"]}>
          {attachments.map((attachment, index) => (
            <AttachmentCard
              key={index}
              fileName={attachment.fileName}
              fileSize={attachment.fileSize.toString()}
              downloadFn={() => handleDownload(processId, attachment.id)}
            />
          ))}
        </div>
      )}
      {showNewAttachmentModal && (
        <PrimaryModal
          open={showNewAttachmentModal}
          onClose={() => setShowNewAttachmentModal(false)}
          header="Adicionar Anexo"
          body={
            <div>
              <input
                type="file"
                onChange={(e) =>
                  setFile(e.target.files ? e.target.files[0] : null)
                }
              />
              <PrimaryButton
                text="Upload"
                onClick={handleFileUpload}
                enabled={true}
              />
            </div>
          }
        />
      )}
    </div>
  );
};
