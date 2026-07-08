import { useTranslation } from "react-i18next";
import { Header } from "../../../Components/Layouts/Header/Header";
import styles from "./triatordashboard.module.css";
import { StatContainerLayout } from "../../../Components/StatContainerLayout/StatContainerLayout";
import { useEffect, useState, type JSX } from "react";
import {
  ProcessApi,
  type ProcessResponse,
} from "../../../Utility/Api/ProcessApi";
import { Icon } from "../../../Config/Icons";
import { PriorityBadge } from "../../../Components/Badge/PriorityBadge/PriorityBadge";
import { formatDate } from "../../../Utility/Helpers/DateHelpers";
import { DataGrid } from "../../../Components/DataGrid/DataGrid";
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
};

export function TriatorDashboard() {
  const { t } = useTranslation();
  const [process, setProcess] = useState<CleanProcess[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalCount, setTotalCount] = useState(0);
  const [filters, setFilters] = useState({ name: "", priority: "" });

  const cleanProcess = (processes: ProcessResponse[]): CleanProcess[] => {
    return processes.map((process) => ({
      name: process.name,
      location: `${process.location.street}, ${process.location.district}`,
      area: t(`Areas.${process.area}`),
      creationDate: formatDate(new Date(process.creationDate)),
      expirationDate: formatDate(new Date(process.dueDate)),
      priority: <PriorityBadge priority={process.priority} />,
      priorityValue: process.priority,
      id: process.id,
    }));
  };

  const fetchProcess = async (page: number, currentFilters: typeof filters) => {
    const offset = (page - 1) * dataGridConfiguration.itemPerPage;
    const response = await ProcessApi.getAll(
      offset,
      dataGridConfiguration.itemPerPage,
      currentFilters.priority || undefined,
      currentFilters.name || undefined,
    );
    if (response.success) {
      setProcess(cleanProcess(response.data.results));
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

  const statArray = [
    {
      icon: { name: Icon.CarCrash, style: { color: "purple" } },
      text: t("Dashboard.Triator.stats.carAccident"),
      value: process.filter((p) => p.area === t("Areas.Car Accident")).length,
    },
    {
      icon: { name: "tsunami", style: { color: "#2196f3" } },
      text: t("Dashboard.Triator.stats.floods"),
      value: process.filter((p) => p.area === t("Areas.Floods")).length,
    },
    {
      icon: { name: "emergency_heat", style: { color: "red" } },
      text: t("Dashboard.Triator.stats.fire"),
      value: process.filter((p) => p.area === t("Areas.Fire")).length,
    },
    {
      icon: { name: "earthquake", style: { color: "yellow" } },
      text: t("Dashboard.Triator.stats.earthquake"),
      value: process.filter((p) => p.area === t("Areas.Earthquake")).length,
    },
  ];

  const columns = ["name", "location", "area", "expirationDate", "priority"];

  return (
    <div className={styles["triator-dashboard-container"]}>
      <Header
        title={t("Dashboard.Triator.title")}
        description={t("Dashboard.description")}
      />
      <StatContainerLayout statArray={statArray} />
      <DataGrid
        columns={columns}
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
        ]}
      />
    </div>
  );
}
