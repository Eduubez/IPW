import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { ProcessApi, type ProcessResponse } from "../../Utility/Api/ProcessApi";
import { Header } from "../../Components/Layouts/Header/Header";
import { PrimaryBadge } from "../../Components/Badge/PrimaryBadge/PrimaryBadge";
import { Color } from "../../StyleGuide/colors";
import styles from "./processpage.module.css";
import { StatCard } from "../../Components/Cards/StatCard/StatCard";
import { Icon } from "../../Components/Icons/Icons";
import { TimeLine } from "../../Components/TimeLine/TimeLine";
import { useTranslation } from "react-i18next";
import { WithBackground } from "../../Components/Layouts/WithBackground/WithBackground";

const normalizeProcessState = (state?: string) => {
  switch (state) {
    case "assigned":
      return "NOT_STARTED";
    default:
      return "ON_GOING";
  }
};

/*
{
    "id": 23,
    "name": "sdasadasdas",
    "location": {
        "id": 25,
        "district": "sdasadasdas",
        "county": "sdasadasdas",
        "street": "sdasadasdassdasadasdas",
        "latitude": "",
        "longitude": ""
    },
    "creationDate": "2026-05-24T19:18:02.597831",
    "dueDate": "2026-05-30T00:00",
    "priority": "WITH_PRIORITY",
    "area": "Car Accident",
    "typification": "Collision",
    "triator": {
        "id": 2,
        "name": "Alice Triator",
        "email": "alice@ipw.pt",
        "areaId": null,
        "roles": []
    },
    "investigator": {
        "id": 3,
        "name": "Bob Investigator",
        "email": "bob@ipw.pt",
        "areaId": null,
        "roles": []
    },
    "supervisor": {
        "id": 4,
        "name": "Carol Supervisor",
        "email": "carol@ipw.pt",
        "areaId": null,
        "roles": []
    },
    "state": "assigned",
    "proves": null,
    "report": null,
    "notes": [],
    "activity": {
        "id": 7,
        "processId": 23,
        "userId": 2,
        "action": "created a new process",
        "description": "Process created by Alice Triator",
        "createdAt": "2026-05-24T19:18:02.597831"
    }
}
    */

export default function ProcessPage() {
  const { id } = useParams();
  const { t } = useTranslation();
  const [apiResponse, setApiResponse] = useState<ProcessResponse | null>(null);
  const [loading, setLoading] = useState(true);

  const fetchProcessData = async () => {
    if (!id) return;
    const response = await ProcessApi.getById(Number(id));
    if (response.success) {
      setApiResponse(response.data);
    }
  };

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
          value: t("CreateProcessPage.fields.supervisor"),
          text: "N/A",
          icon: { name: Icon.Group, style: { color: Color.LightBlue } },
        },
        averiguadorCard: {
          value: t("CreateProcessPage.fields.investigator"),
          text: "N/A",
          icon: { name: Icon.Group, style: { color: Color.LightBlue } },
        },
        areaCard: {
          value: t("CreateProcessPage.fields.area"),
          text: "N/A",
          icon: { name: Icon.CarCrash, style: { color: Color.LightBlue } },
        },
        locationCard: {
          value: t("CreateProcessPage.fields.location"),
          text: "N/A",
          icon: { name: Icon.Location, style: { color: Color.LightBlue } },
        },
        creationCard: {
          value: t("CreateProcessPage.fields.creationDate"),
          text: "N/A",
          icon: { name: Icon.Calendar, style: { color: Color.LightBlue } },
        },
        dueDateCard: {
          value: t("CreateProcessPage.fields.expiresAt"),
          text: "N/A",
          icon: { name: Icon.Calendar, style: { color: Color.LightBlue } },
        },
        priorityCard: {
          value: t("CreateProcessPage.fields.priority"),
          text: "N/A",
          icon: { name: Icon.Info, style: { color: Color.LightBlue } },
        },
      };
    }

    return {
      supervisorCard: {
        value: t("CreateProcessPage.fields.supervisor"),
        text: apiResponse.supervisor?.name || "N/A",
        icon: { name: Icon.Group, style: { color: Color.LightBlue } },
      },
      averiguadorCard: {
        value: t("CreateProcessPage.fields.investigator"),
        text: apiResponse.investigator?.name || "N/A",
        icon: { name: Icon.Group, style: { color: Color.LightBlue } },
      },
      areaCard: {
        value: t("CreateProcessPage.fields.area"),
        text: t(`Areas.${apiResponse.area}`) || "N/A",
        icon: { name: Icon.CarCrash, style: { color: Color.LightBlue } },
      },
      locationCard: {
        value: t("CreateProcessPage.fields.location"),
        text: apiResponse.location
          ? `${apiResponse.location.street}, ${apiResponse.location.district}`
          : "N/A",
        icon: { name: Icon.Location, style: { color: Color.LightBlue } },
      },
      creationCard: {
        value: t("CreateProcessPage.fields.creationDate"),
        text: apiResponse.creationDate
          ? new Date(apiResponse.creationDate).toLocaleDateString()
          : "N/A",
        icon: { name: Icon.Calendar, style: { color: Color.LightBlue } },
      },
      dueDateCard: {
        value: t("CreateProcessPage.fields.expiresAt"),
        text: apiResponse.dueDate
          ? new Date(apiResponse.dueDate).toLocaleDateString()
          : "N/A",
        icon: { name: Icon.Calendar, style: { color: Color.LightBlue } },
      },
      priorityCard: {
        value: t("CreateProcessPage.fields.priority"),
        text: t(`Priority.${apiResponse.priority}`) || "N/A",
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
    fetchProcessData();
    setLoading(false);
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
          text={t(`State.${normalizeProcessState(apiResponse?.state)}`)}
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
        <div className={styles["d-container"]}>
          <WithBackground>
            <TimeLine />
          </WithBackground>
        </div>
        <div className={styles["d-container"]}>
          <WithBackground>
            <span>teste</span>
          </WithBackground>
        </div>
        <div className={styles["d-container"]}>
          <div className={styles["attachments-container"]}>
            <WithBackground>
              <span>teste</span>
            </WithBackground>
          </div>
          <div className={styles["activity-container"]}>
            <WithBackground>
              <span>teste</span>
            </WithBackground>
          </div>
        </div>
      </div>
    </div>
  );
}
