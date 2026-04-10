import { Header } from "../../Components/Layouts/Header/Header";
import styles from "./dashboard.module.css";
import { DataGrid } from "../../Components/DataGrid/DataGrid";
import PriorityBadge from "../../Components/Badge/PriorityBadge/PriorityBadge";
import { LanguageSwitcher } from "../../Components/LanguageSwitcher/LanguageSwitcher";
import {TimeLine} from "../../Components/TimeLine/TimeLine";
import { DropDownMenu } from "../../Components/DropDownMenu/DropDownMenu";
import { DragAndDrop } from "../../Components/DragAndDrop/DragAndDrop";
import {   useTranslation } from "react-i18next";

export default function Dashboard() {
  const { t } = useTranslation()
  const columns = ["ID", "Process", "Assignee", "Priority", "Updated"];


  const rows = [
    {
      ID: "P-1001",
      Process: "Policy validation",
      Assignee: "Ana Martins",
      Priority: <PriorityBadge priority="Urgent" />,
      Updated: "09:42",
    },
    {
      ID: "P-1002",
      Process: "Claim review",
      Assignee: "Joao Silva",
      Priority: <PriorityBadge priority="Medium" />,
      Updated: "10:03",
    },
    {
      ID: "P-1003",
      Process: "Fraud check",
      Assignee: "Maria Costa",
      Priority: <PriorityBadge priority="Low" />,
      Updated: "10:18",
    },
    {
      ID: "P-1004",
      Process: "Document request",
      Assignee: "Rui Pinto",
      Priority: <PriorityBadge priority="Urgent" />,
      Updated: "10:40",
    },
    {
      ID: "P-1005",
      Process: "Policy amendment",
      Assignee: "Ines Rocha",
      Priority: <PriorityBadge priority="Medium" />,
      Updated: "11:02",
    },
    {
      ID: "P-1006",
      Process: "Risk analysis",
      Assignee: "Pedro Gomes",
      Priority: <PriorityBadge priority="Low" />,
      Updated: "11:21",
    },
    {
      ID: "P-1007",
      Process: "Medical report review",
      Assignee: "Sara Alves",
      Priority: <PriorityBadge priority="Urgent" />,
      Updated: "11:47",
    },
    {
      ID: "P-1008",
      Process: "Customer follow-up",
      Assignee: "Miguel Reis",
      Priority: <PriorityBadge priority="Medium" />,
      Updated: "12:05",
    },
    {
      ID: "P-1009",
      Process: "Compliance check",
      Assignee: "Carla Lopes",
      Priority: <PriorityBadge priority="Low" />,
      Updated: "12:19",
    },
    {
      ID: "P-1010",
      Process: "Damage assessment",
      Assignee: "Bruno Teixeira",
      Priority: <PriorityBadge priority="Urgent" />,
      Updated: "12:38",
    },
    {
      ID: "P-1011",
      Process: "Approval routing",
      Assignee: "Patricia Nunes",
      Priority: <PriorityBadge priority="Medium" />,
      Updated: "12:51",
    },
    {
      ID: "P-1012",
      Process: "Case closure",
      Assignee: "Daniel Correia",
      Priority: <PriorityBadge priority="Low" />,
      Updated: "13:07",
    },
  ];


  return (
    <div className={styles["dashboard-container"]}>
      <LanguageSwitcher />
      <Header
        title={t("text.title")}
        description={t("Profile.title")}
      />
      <TimeLine />
      <DataGrid columns={columns} rows={rows} />
      <DragAndDrop />
    </div>
  );
}
