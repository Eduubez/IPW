import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ProcessApi, type ProcessResponse } from "../../Utility/Api/ProcessApi";
import { Header } from "../../Components/Layouts/Header/Header";
import { Color, getPriorityColor } from "../../StyleGuide/colors";
import styles from "./processpage.module.css";
import { StatCard } from "../../Components/Cards/StatCard/StatCard";
import { Icon } from "../../Components/Icons/Icons";
import { TimeLine } from "../../Components/TimeLine/TimeLine";
import { useTranslation } from "react-i18next";
import { WithBackground } from "../../Components/Layouts/WithBackground/WithBackground";
import { ActivityApi } from "../../Utility/Api/ActivityApi";
import { normalizeState } from "../../Utility/Helpers/ProcessStateHelpers";
import { STATES } from "../../MockData/MockStates";
import { ROLE_KEYS } from "../../MockData/MockRoles";
import { StateBadge } from "../../Components/Badge/StateBadge/StateBadge";
import PrimaryButton from "../../Components/Buttons/PrimaryButton/PrimaryButton";
import { ReportCard } from "../../Components/ReportCard/ReportCard";
import { userStore } from "../../Utility/Store/UserStore";
import { PrimaryModal } from "../../Components/Modal/PrimaryModal";
import type { PriorityType } from "../../Components/Badge/PriorityBadge/PriorityBadge";
import { Attachments } from "../../Components/Attachments/Attachments";
import { ActivityCard } from "../../Components/Cards/ActivityCard/ActivityCard";
import { formatDate } from "../../Utility/Helpers/DateHelpers";
import { NoteCard } from "../../Components/Cards/NoteCard/NoteCard";
import TextArea from "../../Components/Inputs/TextArea/TextArea";
import { NotesApi } from "../../Utility/Api/NotesApi";
import { enqueueSnackbar } from "notistack";

const newNoteButtonStyle = {
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

const TIME_LINE_ACTIVITY_TYPE = [
  "CREATED_PROCESS",
  "CANCELLED_PROCESS",
  "APPROVED_SUPERVISOR",
  "REJECTED_SUPERVISOR",
  "APPROVED_REPORT_MANAGER",
  "REJECTED_REPORT_MANAGER",
  "SUBMIT_PROCESS_FOR_APPROVAL",
];
const BOX_ACTIVITY_TYPE = [
  "UPDATED_REPORT",
  "CHANGED_PRIORITY",
  "ASSIGNED_SUPERVISOR",
  "CHANGED_END_DATE",
  "CREATED_REPORT",
  "CREATED_NOTE",
];

const processPageState = (state?: string) => {
  switch (state) {
    case STATES.ASSIGNED.toLowerCase():
      return STATES.NOT_STARTED;
      case STATES.CANCELED.toLowerCase():
      return STATES.CANCELED;
    default:
      return STATES.ON_GOING;
  }
};

const SUBMITTABLE_STATES = [
  STATES.ASSIGNED,
  STATES.REJECTED_BY_SUPERVISOR,
  STATES.REJECTED_BY_MANAGER,
  STATES.ON_GOING,
];

const ALL_PRIORITY_OPTIONS = ["NORMAL", "WITH_PRIORITY", "URGENT"];

export default function ProcessPage() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { t } = useTranslation();
  const [apiResponse, setApiResponse] = useState<ProcessResponse | null>(null);
  const [activityItems, setActivityItems] = useState<
    { done: boolean; label: string; date: Date; userName: string }[]
  >([]);
  const [loading, setLoading] = useState(true);
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [confirmAction, setConfirmAction] = useState<(() => void) | null>(null);
  const [showPriorityModal, setShowPriorityModal] = useState(false);
  const [showNewNoteModal, setShowNewNoteModal] = useState(false);
  const [newNote, setNewNote] = useState("");
  const [renderCountKey, setRenderCountKey] = useState(0);

  const openConfirmModal = (action: () => void) => {
    setConfirmAction(() => action);
    setShowConfirmModal(true);
  };

  const closeConfirmModal = () => {
    setShowConfirmModal(false);
    setConfirmAction(null);
  };

  const fetchProcessData = async () => {
    if (!id) return;
    const response = await ProcessApi.getById(Number(id));
    if (response.success) {
      setApiResponse(response.data);
    } 
    else {
      window.history.replaceState(null, "", "/not-authorized"); // Used to be able to use the back button
      navigate("/not-authorized");
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
        userName: activity.userName,
      }));
      setActivityItems(items);
    }
  };

  const timeLineActivityItens = useMemo(() => {
    if (!apiResponse) return [];
    const items = activityItems.filter((item) =>
      TIME_LINE_ACTIVITY_TYPE.includes(item.label),
    );
    return items;
  }, [activityItems]);

  const boxActivityItens = useMemo(() => {
    if (!apiResponse) return [];
    const items = activityItems.filter((item) =>
      BOX_ACTIVITY_TYPE.includes(item.label),
    );
    return items;
  }, [activityItems]);

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
              text: t("ProcessPage.notify"),
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
              text: t("ProcessPage.notify"),
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
          ? formatDate(new Date(apiResponse.creationDate))
          : "N/A",
        icon: { name: Icon.Calendar, style: { color: Color.LightBlue } },
        button: undefined,
      },
      dueDateCard: {
        value: t("CreateProcessPage.fields.expiresAt"),
        text: apiResponse.dueDate
          ? formatDate(new Date(apiResponse.dueDate))
          : "N/A",
        icon: { name: Icon.Calendar, style: { color: Color.LightBlue } },
        button: undefined,
      },
      priorityCard: {
        value: t("CreateProcessPage.fields.priority"),
        text: t(`Priority.${apiResponse.priority}`) || "N/A",
        icon: { name: Icon.Info, style: { color: Color.LightBlue } },
        button:
          userStore.getActiveRole() === ROLE_KEYS.MANAGER
            ? {
                text: t("ProcessPage.changePriority"),
                onClick: () => setShowPriorityModal(true),
              }
            : undefined,
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
    const load = async () => {
      setLoading(true);
      await Promise.all([fetchProcessData(), fetchProcessActivity()]);
      setLoading(false);
    };
    load();
  }, [id, renderCountKey]);
  const report = apiResponse?.report;
  const processState = normalizeState(apiResponse?.state);
  const hasReport = (report?.content?.length ?? 0) > 0;

  const handleSubmitProcessNote = async () => {
    const requestNote: {
      processId: number;
      proveId: number | null;
      content: string;
    } = {
      processId: Number(id),
      proveId: null,
      content: newNote,
    };

    const response = await NotesApi.createNote(Number(id), requestNote);
    if (response.success) {
      setShowNewNoteModal(false);
      setNewNote("");
      setRenderCountKey((prev) => prev + 1);
    }
  };

  //#region supervisor
  const aproveProcess = async () => {
    const response = await ProcessApi.approve(Number(id));
    if (response.success) {
      enqueueSnackbar(t("ProcessPage.approvalSuccess"), { variant: "success" });
      navigate("/dashboard");
    }
  };
  const rejectProcess = async () => {
    const response = await ProcessApi.reject(Number(id));
    if (response.success) {
      enqueueSnackbar(t("ProcessPage.rejectionSuccess"), { variant: "success" });
      navigate("/dashboard");
    }
  };

  const canAproveOrRejectProcess = () => {
    if (!apiResponse) return false;
    const activeRole = userStore.getActiveRole();
    if (activeRole === ROLE_KEYS.SUPERVISOR) {
      return hasReport && processState === STATES.WAITING_APPROVAL_SUPERVISOR;
    }
    if (activeRole === ROLE_KEYS.MANAGER) {
      return hasReport && processState === STATES.WAITING_APPROVAL_MANAGER;
    }
    return false;
  };

  //#endregion

  //#region insvestigator
  const handleSubmitProcess = async () => {
    const response = await ProcessApi.submit(Number(id));
    if (response.success) {
      enqueueSnackbar(t("ProcessPage.submitSuccess"), { variant: "success" });
      navigate("/dashboard");
    }
  };

  const canSubmitProcess = () => {
    if (!apiResponse) return false;
    return (
      (SUBMITTABLE_STATES as readonly string[]).includes(processState) &&
      hasReport
    );
  };

  //#endregion

  //#region manager
  const handleCancelProcess = async () => {
    const response = await ProcessApi.cancelProcess(Number(id));
    if (response.success) {
      navigate("/dashboard");
    }
  };
  const handleChangePriority = async (newPriority: PriorityType) => {
    const response = await ProcessApi.changePriority(Number(id), newPriority);
    if (response.success) {
      setShowPriorityModal(false);
      setRenderCountKey((prev) => prev + 1);
    }
  };

  //#endregion

  //#region role based view
  const investigatorView = {
    headerButtons: [
      <PrimaryButton
        text={t("ProcessPage.submitProcess")}
        onClick={() => openConfirmModal(handleSubmitProcess)}
        enabled={canSubmitProcess()}
      />,
    ],
    reportView: (
      <ReportCard report={report} processId={Number(id)} viewOnly={false} triggerRenderFn={() => setRenderCountKey((prev) => prev + 1)} />
    ),
    key: ROLE_KEYS.INVESTIGATOR,
  };

  const supervisorView = {
    headerButtons: [
      <PrimaryButton
        text={t("ProcessPage.approveProcess")}
        onClick={() => openConfirmModal(aproveProcess)}
        enabled={canAproveOrRejectProcess()}
      />,
      <PrimaryButton
        text={t("ProcessPage.rejectProcess")}
        onClick={() => openConfirmModal(rejectProcess)}
        enabled={canAproveOrRejectProcess()}
      />,
    ],
    reportView: (
        <ReportCard report={report} processId={Number(id)} viewOnly={true}  triggerRenderFn={() => setRenderCountKey(prev => prev +1)}/>
      ),
    key: ROLE_KEYS.SUPERVISOR,
  };
  const managerView = {
    headerButtons: [
      <PrimaryButton
        text={t("ProcessPage.approveProcess")}
        onClick={() => openConfirmModal(aproveProcess)}
        enabled={canAproveOrRejectProcess()}
      />,
      <PrimaryButton
        text={t("ProcessPage.rejectProcess")}
        onClick={() => openConfirmModal(rejectProcess)}
        enabled={canAproveOrRejectProcess()}
      />,
      <PrimaryButton
        style={{ backgroundColor: Color.DarkRed }}
        text={t("ProcessPage.cancelProcess")}
        onClick={() => openConfirmModal(handleCancelProcess)}
        enabled={true}
      />,
    ],
    reportView: (
      <ReportCard report={report} processId={Number(id)} viewOnly={true}  triggerRenderFn={() => setRenderCountKey(prev => prev + 1)}/>
    ),
    key: ROLE_KEYS.MANAGER,
  };

  /// #endregion

  const activeView = () => {
    const activeRole = userStore.getActiveRole();
    switch (activeRole) {
      case ROLE_KEYS.INVESTIGATOR:
        return investigatorView;
      case ROLE_KEYS.SUPERVISOR:
        return supervisorView;
      case ROLE_KEYS.MANAGER:
        return managerView;
      default:
        return {
          headerButtons: [],
          reportView: <div>{t("ProcessPage.notImplemented")}</div>,
          key: "default",
        };
    }
  };

  const changePriorityModal = (
    currentPriority: PriorityType | undefined,
    setSelectedPriority: (priority: PriorityType) => void,
  ) => {
    const normalizedPriority = currentPriority?.toUpperCase() || "";
    const priorityOptions = currentPriority
      ? ALL_PRIORITY_OPTIONS.filter((option) => option !== normalizedPriority)
      : ALL_PRIORITY_OPTIONS;

    return (
      <div className={styles["priority-options-container"]}>
        <div className={styles["priority-description"]}>
          <span>
            {t("ProcessPage.priorityModal.description", {
              current: t(`Priority.${normalizedPriority}`),
            })}
          </span>
        </div>
        {priorityOptions.map((option) => (
          <div key={option} className={styles["priority-option"]}>
            <PrimaryButton
              enabled={true}
              key={option}
              text={t(`Priority.${option}`)}
              onClick={() => setSelectedPriority(option as PriorityType)}
              style={{
                backgroundColor: getPriorityColor(option as PriorityType),
              }}
            />
          </div>
        ))}
      </div>
    );
  };
  return (
    <div className={styles["page-container"]}>
      <div className={styles["header-container"]}>
        <Header
          title={apiResponse?.name || t("ProcessPage.fallbackTitle")}
          description={t("ProcessPage.description")}
          loading={loading}
        />
        <div className={styles["button-badage-wrapper"]}>
          {(canSubmitProcess() || canAproveOrRejectProcess()) &&
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
              <span>{t("ProcessPage.stateSection")}</span>
              <TimeLine items={timeLineActivityItens} loading={loading} />
            </div>
          </WithBackground>
          <WithBackground>
            <div className={styles["d-container"]}>
              <div className={styles["note-header"]}>
                <span>{t("ProcessPage.notesSection")}</span>
                <PrimaryButton
                  style={newNoteButtonStyle}
                  enabled={true}
                  text="+"
                  onClick={() => setShowNewNoteModal(true)}
                />
              </div>

              {apiResponse?.notes?.length ? (
                <div className={styles["note-card-container"]}>
                  {apiResponse.notes.map((note, index) => (
                    <NoteCard key={index} note={note} />
                  ))}
                </div>
              ) : (
                <div className={styles["no-attachments"]}>
                  <span>{t("ProcessPage.noNotes")}</span>
                </div>
              )}
            </div>
          </WithBackground>
        </div>
        <div className={styles["d-container"]}>
          <WithBackground>{activeView().reportView}</WithBackground>
        </div>
        <div className={styles["d-container"]}>
          <WithBackground>
            <Attachments proves={apiResponse?.proves} triggerRenderFn={() => setRenderCountKey(prev => prev + 1)} />
          </WithBackground>
          <WithBackground>
            <div className={styles["activity-box"]}>
              <span>{t("ProcessPage.activitiesSection")}</span>
              {boxActivityItens.map((activity, index) => (
                <ActivityCard key={index} activity={activity} />
              ))}
            </div>
          </WithBackground>
        </div>
      </div>
      <PrimaryModal
        open={showConfirmModal}
        onClose={closeConfirmModal}
        header={t("ProcessPage.confirmModal.header")}
        body={
          <div className={styles["confirm-modal"]}>
            <span>{t("ProcessPage.confirmModal.body")}</span>
            <div className={styles["modal-button-container"]}>
              <PrimaryButton
                enabled={true}
                text={t("ProcessPage.confirmModal.confirm")}
                onClick={() => {
                  confirmAction?.();
                  setRenderCountKey((prev) => prev + 1); // Increment the renderCountKey to trigger re-render
                  closeConfirmModal();
                }}
              />
            </div>
          </div>
        }
      />
      <PrimaryModal
        open={showPriorityModal}
        onClose={() => setShowPriorityModal(false)}
        header={t("ProcessPage.priorityModal.header")}
        body={changePriorityModal(apiResponse?.priority, handleChangePriority)}
      />
      <PrimaryModal
        open={showNewNoteModal}
        onClose={() => setShowNewNoteModal(false)}
        header={t("ProcessPage.newNoteModal.header")}
        body={
          <div className={styles["new-note-modal"]}>
            <TextArea value={newNote} onChange={setNewNote} label={""} />
            <div className={styles["modal-button-container"]}>
              <PrimaryButton
                enabled={true}
                text={t("ProcessPage.newNoteModal.confirm")}
                onClick={() => {
                  handleSubmitProcessNote();
                }}
              />
            </div>
          </div>
        }
      />
    </div>)
  ;
}
