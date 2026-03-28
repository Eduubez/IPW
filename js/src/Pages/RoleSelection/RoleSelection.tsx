import React, { useMemo } from "react";
import styles from "./roleselection.module.css";
import RoleCard from "../../Components/Cards/RoleCard/RoleCard";
import { mockRoles } from "../../MockData/MockRoles";
import { useTranslation } from "react-i18next";

export default function RoleSelection() {
  const { t } = useTranslation();

  const roleSelection = useMemo(() => {
    return t("RoleSelection", { returnObjects: true }) as {
      title: string;
      description: string;
    } // needed cuz ts...
  }, [t]);

  return (
    <div className={styles["role-selection-container"]}>
      <div className={styles["role-section-header"]}>
        <h1 className="title-large">{roleSelection.title}</h1>
        <p className="subtitle-medium">{roleSelection.description}</p>
      </div>
      <div className={styles["role-selection-role-container"]}>
        {mockRoles.map((role, index) => (
          <RoleCard
            icon={role.icon}
            key={index}
            title={role.title}
            subtitle={role.subtitle}
            permissons={role.permissions}
            style={role.style}
          />
        ))}
      </div>
    </div>
  );
}
