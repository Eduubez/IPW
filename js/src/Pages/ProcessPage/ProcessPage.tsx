import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { mockProcessResponse } from "../../MockData/MockProcess";
import { useSnackbar } from "notistack";
import type { ProcessResponse } from "../../Utility/Api/ProcessApi";
import { Header } from "../../Components/Layouts/Header/Header";
import { PrimaryBadge } from "../../Components/Badge/PrimaryBadge/PrimaryBadge";
import { Color } from "../../StyleGuide/colors";
import styles from "./processpage.module.css";
import { StatCard } from "../../Components/Cards/StatCard/StatCard";
import { Icon } from "../../Components/Icons/Icons";
import { TimeLine } from "../../Components/TimeLine/TimeLine";
export default function ProcessPage() {
  const { id } = useParams();
  const { enqueueSnackbar } = useSnackbar();
  const [apiResponse, setApiResponse] = useState<ProcessResponse | null>(null);
  const [loading, setLoading] = useState(true);

  const {
    supervisorCard,
    averiguadorCard,
    areaCard,
    locationCard,
    creationCard,
    dueDateCard,
    priorityCard,
  } = useMemo(() => {
    if (!apiResponse) {
      return {
        supervisorCard: {
          value: "Supervisor",
          text: "N/A",
          icon: { name: Icon.Group, style: { color: Color.LightBlue } },
        },
        averiguadorCard: {
          value: "Averiguador",
          text: "N/A",
          icon: { name: Icon.Group, style: { color: Color.LightBlue } },
        },
        areaCard: {
          value: "Area",
          text: "N/A",
          icon: { name: Icon.CarCrash, style: { color: Color.LightBlue } },
        },
        locationCard: {
          value: "Location",
          text: "N/A",
          icon: { name: Icon.Location, style: { color: Color.LightBlue } },
        },
        creationCard: {
          value: "Creation Date",
          text: "N/A",
          icon: { name: Icon.Calendar, style: { color: Color.LightBlue } },
        },
        dueDateCard: {
          value: "Due Date",
          text: "N/A",
          icon: { name: Icon.Calendar, style: { color: Color.LightBlue } },
        },
        priorityCard: {
          value: "Priority",
          text: "N/A",
          icon: { name: Icon.Info, style: { color: Color.LightBlue } },
        },
      };
    }

    return {
      supervisorCard: {
        value: "Supervisor",
        text: apiResponse.supervisor?.name || "N/A",
        icon: { name: Icon.Group, style: { color: Color.LightBlue } },
      },
      averiguadorCard: {
        value: "Averiguador",
        text: apiResponse.investigator?.name || "N/A",
        icon: { name: Icon.Group, style: { color: Color.LightBlue } },
      },
      areaCard: {
        value: "Area",
        text: apiResponse.area || "N/A",
        icon: { name: Icon.CarCrash, style: { color: Color.LightBlue } },
      },
      locationCard: {
        value: "Location",
        text: apiResponse.location
          ? `${apiResponse.location.street}, ${apiResponse.location.district}`
          : "N/A",
        icon: { name: Icon.Location, style: { color: Color.LightBlue } },
      },
      creationCard: {
        value: "Creation Date",
        text: apiResponse.creationDate
          ? new Date(apiResponse.creationDate).toLocaleDateString()
          : "N/A",
        icon: { name: Icon.Calendar, style: { color: Color.LightBlue } },
      },
      dueDateCard: {
        value: "Due Date",
        text: apiResponse.dueDate
          ? new Date(apiResponse.dueDate).toLocaleDateString()
          : "N/A",
        icon: { name: Icon.Calendar, style: { color: Color.LightBlue } },
      },
      priorityCard: {
        value: "Priority",
        text: apiResponse.priority || "N/A",
        icon: { name: Icon.Info, style: { color: Color.LightBlue } },
      },
    };
  }, [apiResponse]);

  const cards = [
    supervisorCard,
    averiguadorCard,
    areaCard,
    locationCard,
    creationCard,
    dueDateCard,
    priorityCard,
  ];

  useEffect(() => {
    setLoading(true);
    try {
      setTimeout(() => {
        const response = mockProcessResponse;
        setApiResponse(response);
        setLoading(false);
      }, 2000);
    } catch (error) {
      enqueueSnackbar("Failed to load process data", { variant: "error" });
    } finally {
      //
    }
  }, [id]);

  return (
    <div className={styles["page-container"]}>
      <div className={styles["header-container"]}>
        <Header
          title={apiResponse?.name || "Process Details"}
          description="Veja os detalhes do processo"
          loading={loading}
        />
        <PrimaryBadge
          text={apiResponse?.state.replace("_", " ") || "Status"}
          style={{ backgroundColor: Color.DarkBlue }}
          loading={loading}
        />
      </div>
      <div className={styles["stats-container"]}>
        {cards.map((card, index) => (
          <div
            className={`${styles["stat-item"]} ${loading ? styles["loading"] : ""}`}
            key={index}>
            <StatCard
              key={index}
              value={card.value}
              text={card.text}
              icon={card.icon}
              loading={loading}
            />
          </div>
        ))}
      </div>
      <div className={styles["details-container"]}>
        <TimeLine />
        {/* <Report/>
        *}
        <div className={`${styles["attachments-activity-container"]} ${loading ? styles["loading"] : ""}`}>
          </div><AttachmentComponent/>
          <ActivityComponent/>
        </div>
        */}
      </div>
    </div>
  );
}
