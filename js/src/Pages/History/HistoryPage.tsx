import { useTranslation } from "react-i18next";
import { userStore } from "../../Utility/Store/UserStore";
import { useState, useEffect, useMemo } from "react";
import {
  HistoryApi,
  type AreaProcessHistoryResponse,
  type UserProcessHistoryResponse,
} from "../../Utility/Api/HistoryApi";
import { Header } from "../../Components/Layouts/Header/Header";
import styles from "./historypage.module.css";
import { StatContainerLayout } from "../../Components/StatContainerLayout/StatContainerLayout";
import { DataGrid } from "../../Components/DataGrid/DataGrid";
import { ProcessApi } from "../../Utility/Api/ProcessApi";
import { PriorityBadge } from "../../Components/Badge/PriorityBadge/PriorityBadge";
import { useNavigate } from "react-router-dom";
import { Icon } from "../../Components/Icons/Icons";
import { Color } from "../../StyleGuide/colors";

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
    useState<UserProcessHistoryResponse | AreaProcessHistoryResponse | null>(null);
  const [processes, setProcesses] = useState<any[]>([]);


  const stats = useMemo(() => {
    if (!apiResponse) return [];
    switch (activeRole) {
      case "investigator":
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
                const isCompleted = process.state === "approved_by_manager";
                return isCompleted;
              })
              .length.toString(),
            icon: { name: Icon.Check, style: { color: Color.DarkBlue100 } },
          },
          {
            text: t("HistoryPage.CanceledProcesses"),
            value: processes
              .filter((process) => {
                const isCanceled = process.state === "canceled";
                return isCanceled;
              })
              .length.toString(),
            icon: { name: Icon.Error, style: { color: Color.DarkRed } },
          },
        ];

      case "triator":
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
                const completedStates = ["pending", "canceled", "rejected_by_manager", "approved_by_manager"];
                const isInProgress = !completedStates.includes(process.state);
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


  const fecthHistory = async () => {
    setLoading(true);
    try {
      let historyResponse;
      switch (activeRole) {
        case "supervisor":
          historyResponse = await HistoryApi.getAreaHistory(3); // while we dont have a way to get area id
          break;
        default:
          historyResponse = await HistoryApi.getUserProcessHistory(userId || 0);
      }
      if (!historyResponse.success) return;

      setApiResponse(historyResponse.data);

      const processIds = historyResponse.data.process;
      const processPromises = processIds.map((id) => ProcessApi.getById(id)); 
      const processesResponses = await Promise.all(processPromises);
      const fetchedProcesses = processesResponses
        .filter((res) => res.success)
        .map((res) => res.data);

      const clearedProcesses = fetchedProcesses.map((process) => ({
        id: process.id,
        name: process.name,
        creationDate: formatDate(new Date(process.creationDate)),
        area: process.area,
        dueDate: formatDate(new Date(process.dueDate)),
        priority: <PriorityBadge priority={process.priority} />,
        state: process.state,
        onClick: () => {
          navigate(`/process/${process.id}`);
        },
      }));

      setProcesses(clearedProcesses);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fecthHistory();
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
