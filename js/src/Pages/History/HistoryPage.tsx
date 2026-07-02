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
import { Icon } from "../../Config/Icons";
import { Color } from "../../StyleGuide/colors";
import { STATES } from "../../Config/StatesConfig";
import { ROLE_KEYS } from "../../Config/RolesConfig";
import type { StateType } from "../../Components/Badge/StateBadge/StateBadge";
import { StateBadge } from "../../Components/Badge/StateBadge/StateBadge";
import dataGridConfiguration from "../../Components/DataGrid/DataGridConfiguration";
import { formatDate } from "../../Utility/Helpers/DateHelpers";

export default function HistoryPage() {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const activeRole = userStore.getActiveRole();
  const [apiResponse, setApiResponse] =
    useState<ProcessResponseApi | null>(null);
  const [processes, setProcesses] = useState<any[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalCount, setTotalCount] = useState(0);
  const [filters, setFilters] = useState({ name: "", priority: "" });


  const stats = useMemo(() => {
    if (!apiResponse) return [];
    switch (activeRole) {
      case ROLE_KEYS.INVESTIGATOR:
        return [
          {
            text: t("HistoryPage.TotalProcesses"),
            value: totalCount.toString(),
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
            value: totalCount.toString(),
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
  }, [apiResponse, activeRole, processes, totalCount, t]);
  // Grid Props
  const columns = [
    "name",
    "creationDate",
    "area",
    "dueDate",
    "state",
    "priority",
  ];


  const fetchHistory = async (page: number, currentFilters: typeof filters) => {
      const offset = (page - 1) * dataGridConfiguration.itemPerPage;
      const historyResponse = await ProcessApi.getHistory(
        offset,
        dataGridConfiguration.itemPerPage,
        currentFilters.priority || undefined,
        currentFilters.name || undefined,
      );
      if (!historyResponse.success) return;

      setApiResponse(historyResponse.data);
      setTotalCount(historyResponse.data.totalCount);

      const clearedProcesses = historyResponse.data.results.map((process) => ({
        id: process.id,
        name: process.name,
        creationDate: formatDate(new Date(process.creationDate)),
        area: t(`Areas.${process.area}`),
        dueDate: formatDate(new Date(process.dueDate)),
        priority: <PriorityBadge priority={process.priority} />,
        state: <StateBadge state={process.state as StateType} />,
        stateString: process.state,
        onClick: () => navigate(`/processes/${process.id}`),
      }));

      setProcesses(clearedProcesses);
  };

  useEffect(() => {
    fetchHistory(currentPage, filters);
  }, [currentPage]);

  const handleSearch = (term: string) => {
    const updated = { ...filters, name: term };
    setFilters(updated);
    setCurrentPage(1);
    fetchHistory(1, updated);
  };

  const handleFilterChange = (field: string, value: string) => {
    const updated = { ...filters, [field]: value };
    setFilters(updated);
    setCurrentPage(1);
    fetchHistory(1, updated);
  };

  const priorityOptions = [
    { id: "", name: t("Label.all") },
    { id: "normal", name: t("Priority.NORMAL") },
    { id: "with_priority", name: t("Priority.WITH_PRIORITY") },
    { id: "urgent", name: t("Priority.URGENT") },
  ];
  return (
    <div className={styles["history-page-container"]}>
      <Header
        title={t("HistoryPage.Title")}
        description={t("HistoryPage.Description")}
        loading={false}
      />
      <div className={styles["history-stat-container"]}>
        <StatContainerLayout statArray={stats} />
      </div>
      <DataGrid
        columns={columns}
        rows={processes}
        totalCount={totalCount}
        currentPage={currentPage}
        onPageChange={setCurrentPage}
        onSearch={handleSearch}
        filterDropdowns={[
          {
            label: t("Label.priority"),
            field: "priority",
            options: priorityOptions,
            onSelect: (value) => handleFilterChange("priority", value),
          },
        ]}
      />
    </div>
  );
}
