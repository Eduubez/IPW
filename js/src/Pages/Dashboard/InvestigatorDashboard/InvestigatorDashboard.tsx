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
import styles from "./investigatordashboard.module.css";
import {
  StateBadge,
  type StateType,
} from "../../../Components/Badge/StateBadge/StateBadge";
import { STATES } from "../../../MockData/MockStates";

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

const investigatorDashboardStatesMapper = (state: string) => {
  const normalizedState = state.toUpperCase();
  switch (normalizedState) {
    case STATES.ASSIGNED:
      return STATES.NOT_STARTED;
    case STATES.ON_GOING:
      return STATES.ON_GOING;
    case STATES.REJECTED_BY_SUPERVISOR:
    case STATES.REJECTED_BY_MANAGER:
      return "REJECTED";
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
        state={investigatorDashboardStatesMapper(process.state) as StateType}
      />
    ),
    priorityValue: process.priority,
    id: process.id,
    onClick: () => navigate(`/processes/${process.id}`),
  }));
};

export function InvestigatorDashboard() {
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
    <div className={styles["investigator-dashboard-container"]}>
      <Header
        title={t("Dashboard.Investigator.title")}
        description={t("Dashboard.description")}
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
