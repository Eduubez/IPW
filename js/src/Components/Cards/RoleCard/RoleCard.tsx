import React from "react";
import styles from "./rolecard.module.css";
import PrimaryButton from "../../Buttons/PrimaryButton/PrimaryButton";
import { Icon } from "../../../Config/Icons";
import { useTranslation } from "react-i18next";
import LoadingComponent from "../../LoadingComponent/LoadingComponent";

export default function RoleCard({
  icon,
  title,
  permissions,
  style,
  onClick,
  loading,
}: {
  icon: { name: string; style?: React.CSSProperties };
  title: string;
  permissions: string[];
  style?: React.CSSProperties;
  onClick: () => void;
  loading?: boolean;
}) {
  if (loading) return <LoadingComponent />;
  const { t } = useTranslation();
  return (
    <div className={styles["role-card"]} style={style}>
      <div className={styles["card-top"]}>
        <span
          className={`material-symbols-outlined ${styles["icon-large"]}`}
          style={icon.style}>
          {icon.name}
        </span>
        <p>{title}</p>
      </div>
      <div className={styles["card-bottom"]}>
        <span>{t("Label.permissions")}:</span>
        {permissions.map((perm, index) => (
          <div key={index} className={styles["permission-item"]}>
            <span className="material-symbols-outlined">{Icon.Shield}</span>
            <span className="paragraph-s-regular">{perm}</span>
          </div>
        ))}
      </div>
      <div className={styles["card-footer"]}>
        <PrimaryButton
          text={t("Label.enter")}
          enabled={true}
          onClick={onClick}
        />
      </div>
    </div>
  );
}
