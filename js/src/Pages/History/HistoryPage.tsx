import { useTranslation } from "react-i18next";
import { userStore } from "../../Utility/Store/UserStore";
import { useState, useEffect, useMemo } from "react";
import { Header } from "../../Components/Layouts/Header/Header";
import styles from "./historypage.module.css";
import { StatContainerLayout } from "../../Components/StatContainerLayout/StatContainerLayout";
import { DataGrid } from "../../Components/DataGrid/DataGrid";
import {ProcessApi, type ProcessResponseApi} from "../../Utility/Api/ProcessApi";
import { PriorityBadge } from "../../Components/Badge/PriorityBadge/PriorityBadge";
import { useNavigate } from "react-router-dom";
import { Icon } from "../../Components/Icons/Icons";
import { Color } from "../../StyleGuide/colors";
import { STATES } from "../../MockData/MockStates";
import { ROLE_KEYS } from "../../MockData/MockRoles";
import type { StateType } from "../../Components/Badge/StateBadge/StateBadge";
import { StateBadge } from "../../Components/Badge/StateBadge/StateBadge";
const formatDate = (date: Date) => {
  // format date to dd/mm/yyyy
  const day = date.getDate().toString().padStart(2, "0");
  const month = (date.getMonth() + 1).toString().padStart(2, "0");
  const year = date.getFullYear();
  return `${day}/${month}/${year}`;
};

export default function HistoryPage() {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const userId = userStore.getUserId();
  const activeRole = userStore.getActiveRole();
  const [loading, setLoading] = useState(false);
  const [apiResponse, setApiResponse] =
    useState<ProcessResponseApi | null>(null);
  const [processes, setProcesses] = useState<any[]>([]);


  const stats = useMemo(() => {
    if (!apiResponse) return [];
    switch (activeRole) {
      case ROLE_KEYS.INVESTIGATOR:
        return [
          {
            text: t("HistoryPage.TotalProcesses"),
            value: processes.length.toString(),
            icon: { name: Icon.History, style: { color: Color.GreenPrimary } },
          },
          {
            text: t("HistoryPage.CompletedProcesses"),
            value: processes
              .filter((process) => {
                const isCompleted = process.stateString === STATES.APPROVED_BY_MANAGER.toLowerCase();
                return isCompleted;
              })
              .length.toString(),
            icon: { name: Icon.Check, style: { color: Color.DarkBlue100 } },
          },
          {
            text: t("HistoryPage.CanceledProcesses"),
            value: processes
              .filter((process) => {
                const isCanceled = process.stateString === STATES.CANCELED.toLowerCase();
                return isCanceled;
              })
              .length.toString(),
            icon: { name: Icon.Error, style: { color: Color.DarkRed } },
          },
        ];

      case ROLE_KEYS.TRIATOR:
        return [
          {
            text: t("HistoryPage.TotalProcesses"),
            value: processes.length.toString(),
            icon: { name: Icon.History, style: { color: Color.GreenPrimary } },
          },
          {
            text: t("HistoryPage.ProcessesInProgress"),
            value: processes
              .filter((process) => {
                const completedStates = ["pending", STATES.CANCELED.toLowerCase(), STATES.REJECTED_BY_MANAGER.toLowerCase(), STATES.APPROVED_BY_MANAGER.toLowerCase()];
                const isInProgress = !completedStates.includes(process.stateString);
                return isInProgress;
              })
              .length.toString(),
            icon: { name: Icon.Clock, style: { color: Color.Orange } },
          }
        ];  
      default:
        return [];
    }
  }, [apiResponse, activeRole, processes, t]);
  // Grid Props
  const columns = [
    "name",
    "creationDate",
    "area",
    "dueDate",
    "state",
    "priority",
  ];


  const fetchHistory = async () => {
    setLoading(true);
    try {
      const historyResponse = await ProcessApi.getHistory();
      if (!historyResponse.success) return;

      setApiResponse(historyResponse.data);

      const fetchedProcesses = historyResponse.data.results

      const clearedProcesses = fetchedProcesses.map((process) => ({
        id: process.id,
        name: process.name,
        creationDate: formatDate(new Date(process.creationDate)),
        area: process.area,
        dueDate: formatDate(new Date(process.dueDate)),
        priority: <PriorityBadge priority={process.priority} />,
        state: <StateBadge state={process.state as StateType} />,
        stateString: process.state,
        onClick: () => {
          navigate(`/processes/${process.id}`);
        },
      }));

      setProcesses(clearedProcesses);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHistory();
  }, []);
  return (
    <div className={styles["history-page-container"]}>
      <Header
        title={t("HistoryPage.Title")}
        description={t("HistoryPage.Description")}
        loading={false}
      />
      <div className={styles["history-stat-container"]}>
        <StatContainerLayout statArray={stats} loading={loading} />
      </div>
      <DataGrid columns={columns} rows={processes} loading={loading} />
    </div>
  );
}
