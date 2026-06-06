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
import { STATES } from "../../../MockData/MockStates";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { DataGrid } from "../../../Components/DataGrid/DataGrid";
import { Header } from "../../../Components/Layouts/Header/Header";
import styles from "./managerdashboard.module.css";
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
const cleanProcess = (
  processes: ProcessResponse[],
  navigate: (path: string) => void,
): CleanProcess[] => {
  return processes.map((process) => ({
    name: process.name,
    location: `${process.location.street}, ${process.location.district}`,
    area: process.area,
    creationDate: new Date(process.creationDate).toLocaleDateString(),
    expirationDate: new Date(process.dueDate).toLocaleDateString(),
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

export function ManagerDashboard() {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [loading, setLoading] = useState(true);
  const [process, setProcess] = useState<CleanProcess[]>([]);

  const fetchProcess = async () => {
    try {
      const response = await ProcessApi.getAll(0, 100);
      if (response.success) {
        console.log(response.data.results);
        setProcess(cleanProcess(response.data.results, navigate));
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProcess();
  }, []);

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
        loading={loading}
      />
    </div>
  );
}
