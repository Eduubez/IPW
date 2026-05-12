import { useTranslation } from "react-i18next";
import { Header } from "../../../Components/Layouts/Header/Header";
import styles from "./triatordashboard.module.css";
import { StatContainerLayout } from "../../../Components/StatContainerLayout/StatContainerLayout";
import { DataGrid } from "../../../Components/DataGrid/DataGrid";
import { useEffect, useState, type JSX } from "react";
import {
  ProcessApi,
  type ProcessResponse,
} from "../../../Utility/Api/ProcessApi";
import { Icon } from "../../../Components/Icons/Icons";
import { PriorityBadge } from "../../../Components/Badge/PriorityBadge/PriorityBadge";
import { useNavigate } from "react-router-dom";

type CleanProcess = {
  name: string;
  location: string;
  area: string;
  creationDate: string;
  expirationDate: string;
  priority: JSX.Element;
  priorityValue: string;
  id: number;
};
const cleanProcess = (processes: ProcessResponse[]): CleanProcess[] => {
  return processes.map((process) => ({
    name: process.name,
    location: `${process.location.street}, ${process.location.district}`,
    area: process.area,
    creationDate: new Date(process.creationDate).toLocaleDateString(),
    expirationDate: new Date(process.dueDate).toLocaleDateString(),
    priority: <PriorityBadge priority={process.priority} />,
    priorityValue: process.priority,
    id: process.id,
  }));
};

export function TriatorDashboard() {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [loading, setLoading] = useState(true);
  const [process, setProcess] = useState<CleanProcess[]>([]);

  const fetchProcess = async () => {
    try {
      const response = await ProcessApi.getAll();
      if (response.success) {
        setProcess(cleanProcess(response.data));
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
    "area",
    "creationDate",
    "expirationDate",
    "priority",
  ];
  const gridActions = [
    {
      label: "Criar novo processo",
      onClick: () => {
        navigate("/processes/new");
      },
    },
  ];

  const statArray = [
    {
      icon: { name: Icon.Warning, style: { color: "red" } },
      text: "Processos com Prioridade",
      value: process.filter((p) => p.priorityValue === "WITH_PRIORITY").length,
    },
    {
      icon: { name: Icon.Warning, style: { color: "#4caf50" } },
      text: "Processos Prioritarios",
      value: process.filter((p) => p.priorityValue === "URGENT").length,
    },
    {
      icon: { name: Icon.Warning, style: { color: "#2196f3" } },
      text: "Processos Normais",
      value: process.filter((p) => p.priorityValue === "NORMAL").length,
    },
    {
      icon: { name: Icon.CarCrash, style: { color: "purple" } },
      text: "Acidente de Carro",
      value: process.filter((p) => p.area === "Car Accident").length,
    },
    {
      icon: { name: "tsunami", style: { color: "#2196f3" } },
      text: "Inundação",
      value: process.filter((p) => p.area === "Floods").length,
    },
    {
      icon: { name: "emergency_heat", style: { color: "red" } },
      text: "Fogo",
      value: process.filter((p) => p.area === "Fire").length,
    },
    {
      icon: { name: "earthquake", style: { color: "yellow" } },
      text: "Terremoto",
      value: process.filter((p) => p.area === "Earthquake").length,
    },
  ];
  return (
    <div className={styles["triator-dashboard-container"]}>
      <Header
        title="Triator Dashboard"
        description="Olá novamente, veja o que tem acontecido ultimamente!"
      />
      <StatContainerLayout statArray={statArray} loading={loading} />
      <DataGrid
        title="Processos Recentes"
        columns={gridColumns}
        rows={process}
        loading={loading}
        actions={gridActions}
      />
    </div>
  );
}
