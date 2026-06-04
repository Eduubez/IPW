import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ProcessApi, type ProcessResponse } from "../../Utility/Api/ProcessApi";
import { Header } from "../../Components/Layouts/Header/Header";
import { Color } from "../../StyleGuide/colors";
import styles from "./processpage.module.css";
import { StatCard } from "../../Components/Cards/StatCard/StatCard";
import { Icon } from "../../Components/Icons/Icons";
import { TimeLine } from "../../Components/TimeLine/TimeLine";
import { useTranslation } from "react-i18next";
import { WithBackground } from "../../Components/Layouts/WithBackground/WithBackground";
import { ActivityApi } from "../../Utility/Api/ActivityApi";
import { normalizeState } from "../../Utility/Helpers/ProcessStateHelpers";
import { StateBadge } from "../../Components/Badge/StateBadge/StateBadge";
import PrimaryButton from "../../Components/Buttons/PrimaryButton/PrimaryButton";
import { ReportCard } from "../../Components/ReportCard/ReportCard";
import { userStore } from "../../Utility/Store/UserStore";
import { ReportApi } from "../../Utility/Api/ReportApi";
import { PrimaryModal } from "../../Components/Modal/PrimaryModal";

const processPageState = (state?: string) => {
  switch (state) {
    case "assigned":
      return "NOT_STARTED";
    default:
      return "ON_GOING";
  }
};
export default function ProcessPage() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { t } = useTranslation();
  const [apiResponse, setApiResponse] = useState<ProcessResponse | null>(null);
  const [activityItems, setActivityItems] = useState<
    { done: boolean; label: string; date: Date; user: string }[]
  >([]);
  const [loading, setLoading] = useState(true);
  const [showConfirmModal, setShowConfirmModal] = useState(false);

  const fetchProcessData = async () => {
    if (!id) return;
    const response = await ProcessApi.getById(Number(id));
    if (response.success) {
      setApiResponse(response.data);
    }
  };
  const fetchProcessActivity = async () => {
    if (!id) return;
    const response = await ActivityApi.getActivityByProcess(Number(id), 0, 10);
    if (response.success) {
      const items = response.data.results.map((activity) => ({
        done: true,
        label: activity.action,
        date: new Date(activity.createdAt),
        user: activity.userId.toString(), // You might want to replace this with the actual user name
      }));
      setActivityItems(items);
    }
  };

  const {
    supervisorCard,
    averiguadorCard,
    areaCard,
    locationCard,
    creationCard,
    dueDateCard,
    priorityCard,
  } = useMemo(() => {
    if (!apiResponse) {
      return {
        supervisorCard: {
          value: t("CreateProcessPage.fields.supervisor"),
          text: "N/A",
          icon: { name: Icon.Group, style: { color: Color.LightBlue } },
          button: undefined,
        },
        averiguadorCard: {
          value: t("CreateProcessPage.fields.investigator"),
          text: "N/A",
          icon: { name: Icon.Group, style: { color: Color.LightBlue } },
          button: undefined,
        },
        areaCard: {
          value: t("CreateProcessPage.fields.area"),
          text: "N/A",
          icon: { name: Icon.CarCrash, style: { color: Color.LightBlue } },
          button: undefined,
        },
        locationCard: {
          value: t("CreateProcessPage.fields.location"),
          text: "N/A",
          icon: { name: Icon.Location, style: { color: Color.LightBlue } },
          button: undefined,
        },
        creationCard: {
          value: t("CreateProcessPage.fields.creationDate"),
          text: "N/A",
          icon: { name: Icon.Calendar, style: { color: Color.LightBlue } },
          button: undefined,
        },
        dueDateCard: {
          value: t("CreateProcessPage.fields.expiresAt"),
          text: "N/A",
          icon: { name: Icon.Calendar, style: { color: Color.LightBlue } },
          button: undefined,
        },
        priorityCard: {
          value: t("CreateProcessPage.fields.priority"),
          text: "N/A",
          icon: { name: Icon.Info, style: { color: Color.LightBlue } },
          button: undefined,
        },
      };
    }

    return {
      supervisorCard: {
        value: t("CreateProcessPage.fields.supervisor"),
        text: apiResponse.supervisor?.name || "N/A",
        icon: { name: Icon.Group, style: { color: Color.LightBlue } },
        button: apiResponse.supervisor?.email
          ? {
              text: "Notificar",
              onClick: () =>
                (window.location.href = `mailto:${apiResponse.supervisor!.email}`),
            }
          : undefined,
      },
      averiguadorCard: {
        value: t("CreateProcessPage.fields.investigator"),
        text: apiResponse.investigator?.name || "N/A",
        icon: { name: Icon.Group, style: { color: Color.LightBlue } },
        button: apiResponse.investigator?.email
          ? {
              text: "Notificar",
              onClick: () =>
                (window.location.href = `mailto:${apiResponse.investigator!.email}`),
            }
          : undefined,
      },
      areaCard: {
        value: t("CreateProcessPage.fields.area"),
        text: t(`Areas.${apiResponse.area}`) || "N/A",
        icon: { name: Icon.CarCrash, style: { color: Color.LightBlue } },
        button: undefined,
      },
      locationCard: {
        value: t("CreateProcessPage.fields.location"),
        text: apiResponse.location
          ? `${apiResponse.location.street}, ${apiResponse.location.district}`
          : "N/A",
        icon: { name: Icon.Location, style: { color: Color.LightBlue } },
        button: undefined,
      },
      creationCard: {
        value: t("CreateProcessPage.fields.creationDate"),
        text: apiResponse.creationDate
          ? new Date(apiResponse.creationDate).toLocaleDateString()
          : "N/A",
        icon: { name: Icon.Calendar, style: { color: Color.LightBlue } },
        button: undefined,
      },
      dueDateCard: {
        value: t("CreateProcessPage.fields.expiresAt"),
        text: apiResponse.dueDate
          ? new Date(apiResponse.dueDate).toLocaleDateString()
          : "N/A",
        icon: { name: Icon.Calendar, style: { color: Color.LightBlue } },
        button: undefined,
      },
      priorityCard: {
        value: t("CreateProcessPage.fields.priority"),
        text: t(`Priority.${apiResponse.priority}`) || "N/A",
        icon: { name: Icon.Info, style: { color: Color.LightBlue } },
        button: undefined,
      },
    };
  }, [apiResponse]);

  const cards = [
    supervisorCard,
    averiguadorCard,
    areaCard,
    locationCard,
    creationCard,
    dueDateCard,
    priorityCard,
  ];

  useEffect(() => {
    setLoading(true);
    fetchProcessData();
    fetchProcessActivity();
    setLoading(false);
  }, [id]);
  const report = apiResponse?.report;

  //#region supervisor
  const aproveProcess = async () => {};
  const rejectProcess = async () => {};

  const canAproveOrRejectProcess = apiResponse
    ? normalizeState(apiResponse?.state) === "WAITING_APPROVAL_SUPERVISOR"
    : false;

  //#endregion

  //#region insvestigator
  const handleSubmitProcess = async () => {
    const response = await ProcessApi.submit(Number(id));
    console.log("Submit response:", response);
  };

  const canSubmitProcess = apiResponse
    ? normalizeState(apiResponse?.state) === "ON_GOING"
    : false;

  //#endregion

  //#region role based view
  const investigatorView = {
    headerButtons: [
      <PrimaryButton
        text={"Submeter Processo"}
        onClick={() => setShowConfirmModal(true)}
        enabled={canSubmitProcess}
      />,
    ],
    reportView: (
      <ReportCard report={report} processId={Number(id)} viewOnly={false} />
    ),
    key: "investigator",
  };

  const supervisorView = {
    headerButtons: [
      <PrimaryButton
        text={"Aprovar Processo"}
        onClick={aproveProcess}
        enabled={canAproveOrRejectProcess}
      />,
      <PrimaryButton
        text={"Rejeitar Processo"}
        onClick={rejectProcess}
        enabled={canAproveOrRejectProcess}
      />,
    ],
    reportView: (
      <ReportCard report={report} processId={Number(id)} viewOnly={true} />
    ),
    key: "supervisor",
  };

  /// #endregion

  const activeView = () => {
    const activeRole = userStore.getActiveRole();
    switch (activeRole) {
      case "investigator":
        return investigatorView;
      case "supervisor":
        return supervisorView;
      default:
        return {
          headerButtons: [],
          reportView: <div>View not implemented for this role</div>,
          key: "default",
        };
    }
  };

  return (
    <div className={styles["page-container"]}>
      <div className={styles["header-container"]}>
        <Header
          title={apiResponse?.name || "Process Details"}
          description="Veja os detalhes do processo"
          loading={loading}
        />
        <div className={styles["button-badage-wrapper"]}>
          {(canSubmitProcess || canAproveOrRejectProcess) &&
            activeView().headerButtons.map((button, index) => (
              <div
                key={`${index}${activeView().key}`}
                className={styles["button-container"]}>
                {button}
              </div>
            ))}
          <StateBadge state={processPageState(apiResponse?.state)} />
        </div>
      </div>
      <div className={styles["stats-container"]}>
        {cards.map((card, index) => (
          <div
            className={`${styles["stat-item"]} ${loading ? styles["loading"] : ""}`}
            key={index}>
            <StatCard
              key={index}
              value={card.value}
              text={card.text}
              icon={card.icon}
              loading={loading}
              button={card.button}
            />
          </div>
        ))}
      </div>
      <div className={styles["details-container"]}>
        <div className={styles["d-container"]}>
          <WithBackground>
            <div>
              <span>Atividades Recentes</span>
              <TimeLine items={activityItems} loading={loading} />
            </div>
          </WithBackground>
        </div>
        <div className={styles["d-container"]}>
          <WithBackground>{activeView().reportView}</WithBackground>
        </div>
        <div className={styles["d-container"]}>
          <div className={styles["attachments-container"]}>
            <WithBackground>
              <span>Anexos</span>
            </WithBackground>
          </div>
          <div className={styles["activity-container"]}>
            <WithBackground>
              <span>Atividades</span>
            </WithBackground>
          </div>
        </div>
      </div>
      {showConfirmModal && (
        <PrimaryModal
          open={showConfirmModal}
          onClose={() => setShowConfirmModal(false)}
          header={"Confirmar Ação"}
          body={
            <>
              <div className={styles["confirm-modal"]}>
                <span>Tem certeza que deseja realizar esta ação?</span>
                <div className={styles["modal-button-container"]}>
                  <PrimaryButton
                    enabled={true}
                    text={"Confirmar"}
                    onClick={() => {
                      handleSubmitProcess();
                      setShowConfirmModal(false);
                    }}
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
