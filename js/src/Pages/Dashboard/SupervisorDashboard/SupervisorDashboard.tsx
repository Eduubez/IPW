import { useEffect, useState, type JSX } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { PriorityBadge } from "../../../Components/Badge/PriorityBadge/PriorityBadge";
import {
  ProcessApi,
  type ProcessResponse,
} from "../../../Utility/Api/ProcessApi";
import { Header } from "../../../Components/Layouts/Header/Header";
import { StatContainerLayout } from "../../../Components/StatContainerLayout/StatContainerLayout";
import { DataGrid } from "../../../Components/DataGrid/DataGrid";
import { StateBadge, type StateType } from "../../../Components/Badge/StateBadge/StateBadge";
import styles from "./supervisordashboard.module.css";

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

const supervisorDashboardStatesMapper = (state: string) => {
  const normalizedState = state.toUpperCase();
  switch (normalizedState) {
    case "WAITING_APPROVAL_SUPERVISOR":
      return "NOT_STARTED";
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
        state={supervisorDashboardStatesMapper(process.state) as StateType}
      />
    ),
    priorityValue: process.priority,
    id: process.id,
    onClick: () => navigate(`/processes/${process.id}`),
  }));
};

export function SupervisorDashboard () {
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
    "name",
    "location",
    "creationDate",
    "expirationDate",
    "priority",
    "state",
  ];

  return (
    <div className={styles["supervisor-dashboard-container"]}>
      <Header
        title="Supervisor Dashboard"
        description="Olá novamente, veja o que tem acontecido ultimamente!"
      />
      <DataGrid
        title="Processos Recentes"
        columns={gridColumns}
        rows={process}
        loading={loading}
      />
    </div>
  );
}