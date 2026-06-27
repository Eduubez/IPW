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
import {formatDate} from "../../../Utility/Helpers/DateHelpers";

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
    creationDate: formatDate(new Date(process.creationDate)),
    expirationDate: formatDate(new Date(process.dueDate)),
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
        setProcess(cleanProcess(response.data.results));
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
      label: t("Dashboard.createNewProcess"),
      onClick: () => {
        navigate("/processes/new");
      },
    },
  ];

  const statArray = [
    {
      icon: { name: Icon.CarCrash, style: { color: "purple" } },
      text: t("Dashboard.Triator.stats.carAccident"),
      value: process.filter((p) => p.area === "Car Accident").length,
    },
    {
      icon: { name: "tsunami", style: { color: "#2196f3" } },
      text: t("Dashboard.Triator.stats.floods"),
      value: process.filter((p) => p.area === "Floods").length,
    },
    {
      icon: { name: "emergency_heat", style: { color: "red" } },
      text: t("Dashboard.Triator.stats.fire"),
      value: process.filter((p) => p.area === "Fire").length,
    },
    {
      icon: { name: "earthquake", style: { color: "yellow" } },
      text: t("Dashboard.Triator.stats.earthquake"),
      value: process.filter((p) => p.area === "Earthquake").length,
    },
  ];
  return (
    <div className={styles["triator-dashboard-container"]}>
      <Header
        title={t("Dashboard.Triator.title")}
        description={t("Dashboard.description")}
      />
      <StatContainerLayout statArray={statArray} loading={loading} />
    </div>
  );
}
