import { useEffect, useState, type JSX } from "react";
import {
  ProcessApi,
  type ProcessResponse,
} from "../../../Utility/Api/ProcessApi";
import { PriorityBadge } from "../../../Components/Badge/PriorityBadge/PriorityBadge";
import {
  StateBadge,
  type StateType,
} from "../../../Components/Badge/StateBadge/StateBadge";
import { STATES } from "../../../Config/StatesConfig";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { DataGrid } from "../../../Components/DataGrid/DataGrid";
import { Header } from "../../../Components/Layouts/Header/Header";
import styles from "./managerdashboard.module.css";
import { formatDate } from "../../../Utility/Helpers/DateHelpers";
import dataGridConfiguration from "../../../Components/DataGrid/DataGridConfiguration";
type CleanProcess = {
  name: string;
  location: string;
  area: string;
  creationDate: string;
  expirationDate: string;
  priority: JSX.Element;
  priorityValue: string;
  id: number;
  onClick: () => void;
};

const managerDashboardStatesMapper = (state: string) => {
  const normalizedState = state.toUpperCase();
  switch (normalizedState) {
    case STATES.WAITING_APPROVAL_MANAGER:
      return STATES.NOT_STARTED;
    default:
      return "UNKNOWN";
  }
};


export function ManagerDashboard() {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [process, setProcess] = useState<CleanProcess[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalCount, setTotalCount] = useState(0);
  const [filters, setFilters] = useState({ name: "", priority: "", state: "" });
const cleanProcess = (
  processes: ProcessResponse[],
  navigate: (path: string) => void,
): CleanProcess[] => {
  return processes.map((process) => ({
    name: process.name,
    location: `${process.location.street}, ${process.location.district}`,
    area: t(`Areas.${process.area}`),
    creationDate: formatDate(new Date(process.creationDate)),
    expirationDate: formatDate(new Date(process.dueDate)),
    priority: <PriorityBadge priority={process.priority} />,
    state: (
      <StateBadge
        state={managerDashboardStatesMapper(process.state) as StateType}
      />
    ),
    priorityValue: process.priority,
    id: process.id,
    onClick: () => navigate(`/processes/${process.id}`),
  }));
};
  const fetchProcess = async (
    page: number,
    currentFilters: typeof filters,
  ) => {
    const offset = (page - 1) * dataGridConfiguration.itemPerPage;
    const response = await ProcessApi.getAll(
      offset,
      dataGridConfiguration.itemPerPage,
      currentFilters.priority || undefined,
      currentFilters.name || undefined,
      currentFilters.state || undefined,
    );
    if (response.success) {
      setProcess(cleanProcess(response.data.results, navigate));
      setTotalCount(response.data.totalCount);
    }
  };

  useEffect(() => {
    fetchProcess(currentPage, filters);
  }, [currentPage]);

  const handleSearch = (term: string) => {
    const updated = { ...filters, name: term };
    setFilters(updated);
    setCurrentPage(1);
    fetchProcess(1, updated);
  };

  const handleFilterChange = (field: string, value: string) => {
    const updated = { ...filters, [field]: value };
    setFilters(updated);
    setCurrentPage(1);
    fetchProcess(1, updated);
  };

  const priorityOptions = [
    { id: "", name: t("Label.all") },
    { id: "normal", name: t("Priority.NORMAL") },
    { id: "with_priority", name: t("Priority.WITH_PRIORITY") },
    { id: "urgent", name: t("Priority.URGENT") },
  ];

  const stateOptions = [
    { id: "", name: t("Label.all") },
    { id: STATES.WAITING_APPROVAL_MANAGER.toLowerCase(), name: t(`State.${STATES.NOT_STARTED}`) },
  ];

  const gridColumns = [
    "area",
    "name",
    "location",
    "creationDate",
    "expirationDate",
    "priority",
    "state",
  ];
  

  return (
    <div className={styles["manager-dashboard-container"]}>
      <Header
        title={t("Dashboard.Manager.title")}
        description={t("Dashboard.description")}
      />
      <DataGrid
        title={t("Dashboard.recentProcesses")}
        columns={gridColumns}
        rows={process}
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
          {
            label: t("Label.state"),
            field: "state",
            options: stateOptions,
            onSelect: (value) => handleFilterChange("state", value),
          },
                    {
            label: t("Label.area"),
            field: "areaId",
            options: [
              { id: "", name: t("Label.all") },
              { id: "1", name: t("Areas.1") },
              { id: "2", name: t("Areas.2") },
              { id: "3", name: t("Areas.3") },
              { id: "4", name: t("Areas.4") },
            ],
            onSelect: (value) => handleFilterChange("areaId", value),
          },
        ]}
      />
    </div>
  );
}
