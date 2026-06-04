import { useTranslation } from "react-i18next";
import styles from "./reportcard.module.css";
import PrimaryButton from "../Buttons/PrimaryButton/PrimaryButton";
import TextArea from "../Inputs/TextArea/TextArea";
import { useEffect, useState } from "react";
import { type ReportType } from "../../Utility/Api/ProcessApi";
import { NoReport } from "./NoReport/NoReport";
import { PrimaryModal } from "../Modal/PrimaryModal";
import { ReportApi } from "../../Utility/Api/ReportApi";

export function ReportCard({
  report,
  processId,
  viewOnly,
}: {
  report: ReportType | undefined;
  processId: number;
  viewOnly: boolean;
}) {
  const { t } = useTranslation();
  const [editMode, setEditMode] = useState(false);
  const [reportContent, setReportContent] = useState<string | null>(
    report?.content || null,
  );
  const [openCreateReportModal, setOpenCreateReportModal] = useState(false);
  const [openNotesModal, setOpenNotesModal] = useState(false);

  useEffect(() => {
    setReportContent(report?.content || null);
  }, [report]);
  const [submitting, setSubmitting] = useState(false);

  const handleEditReport = () => {
    setEditMode(!editMode);
  };

  const handleAddNotes = () => {
    // Lógica para adicionar notas
  };

  const handleSaveReport = async () => {
    setSubmitting(true);
    try {
      const response = await ReportApi.updateReport(processId, reportContent!);
      if(response.success) {
        window.location.reload(); // Recarrega a página para mostrar o relatório atualizado
      }
    } finally {
      setSubmitting(false);
      setEditMode(false);
    }
  };

  const handleCreateReport = async () => {
    setSubmitting(true);
    try {
      const response = await ReportApi.createReport(processId, reportContent!);
      if(response.success) {
        window.location.reload(); // Recarrega a página para mostrar o novo relatório criado
      }

    } finally {
      setSubmitting(false);
      setOpenCreateReportModal(false);
    }
  };
  const isSaveEnabled = !submitting && editMode; // Habilitar o botão de salvar apenas no modo de edição
  const isCreateEnabled =
    !submitting && reportContent !== null && reportContent.trim() !== "";
  const reportTextArea = (readOnly = false) => (
    <div className={styles["report-card-content"]}>
      <TextArea
        label=""
        value={reportContent ? reportContent : ""}
        onChange={setReportContent}
        readOnly={readOnly}
      />
    </div>
  );

  return (
    <div className={styles["report-container"]}>
      <h2>{t("reportCard.title")}</h2>
      {!report && <NoReport onClick={() => setOpenCreateReportModal(true)} />}
      {report && (
        <>
          {reportTextArea(!editMode)}
          <div className={styles["report-card-actions"]}>
            {!viewOnly && (
              <>
                <PrimaryButton
                  style={{ width: "120px", height: "fit-content" }}
                  enabled={isSaveEnabled}
                  text={"Salvar"}
                  onClick={handleSaveReport}
                />
                <PrimaryButton
                  style={{ width: "120px", height: "fit-content" }}
                  enabled={!isSaveEnabled}
                  text={"Editar"}
                  onClick={handleEditReport}
                />
              </>
            )}
            <PrimaryButton
              style={{ width: "140px", height: "fit-content" }}
              enabled={true}
              text={"Notas"}
              onClick={() => setOpenNotesModal(true)}
            />
          </div>
        </>
      )}
      {openCreateReportModal && (
        <PrimaryModal
          open={openCreateReportModal}
          onClose={() => setOpenCreateReportModal(false)}
          header={"Criar Relatório"}
          body={
            <>
              <div
                style={{
                  display: "flex",
                  flexDirection: "column",
                  gap: "1rem",
                  height: "100%",
                  width: "100%",
                }}>
                {reportTextArea()}
                <div className={styles["report-card-actions"]}>
                  <PrimaryButton
                    style={{ width: "120px", height: "fit-content" }}
                    enabled={isCreateEnabled}
                    text={"Criar Relatório"}
                    onClick={handleCreateReport}
                  />
                </div>
              </div>
            </>
          }
        />
      )}
      {openNotesModal && (
        <PrimaryModal
          open={openNotesModal}
          onClose={() => setOpenNotesModal(false)}
          header={"Adicionar Notas"}
          body={
            <>
              <div
                style={{
                  display: "flex",
                  flexDirection: "column",
                  gap: "1rem",
                  height: "100%",
                  width: "100%",
                }}>
                <span>Funcionalidade de notas ainda não implementada.</span>

                <div className={styles["report-card-actions"]}>
                  <PrimaryButton
                    style={{ width: "120px", height: "fit-content" }}
                    enabled={false}
                    text={"Adicionar Notas"}
                    onClick={handleAddNotes}
                  />
                </div>
              </div>
            </>
          }
        />
      )}
    </div>
  );
}
