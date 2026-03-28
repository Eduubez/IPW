import React from "react";
import styles from "./informationcard.module.css";
export function InformationCard({
  icon,
  title,
  description,
}: {
  icon: string;
  title: string;
  description: string;
}) {
  return (
    <div className={styles["information-card"]}>
      <div className={styles["information-icon-container"]}>
        <span
          className={`material-symbols-outlined ${styles["information-icon"]}`}>
          {icon}
        </span>
      </div>
      <div className={styles["information-content"]}>
        <span>{title}</span>
        <span>{description}</span>
      </div>
    </div>
  );
}
