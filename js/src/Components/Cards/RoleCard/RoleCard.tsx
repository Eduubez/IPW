import React, { use } from "react";
import styles from "./rolecard.module.css";
import PrimaryButton from "../../Buttons/PrimaryButton/PrimaryButton";
import { Icon } from "../../Icons/Icons";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router";

export default function RoleCard({
  icon,
  title,
  permissions,
  style,
}: {
  icon: { name: string; style?: React.CSSProperties };
  title: string;
  permissions: string[];
  style?: React.CSSProperties;
}) {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const handleSelectRole = () => {
    localStorage.setItem("selectedRole", title);
    navigate("/dashboard");
  }

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
          onClick={handleSelectRole}
        />
      </div>
    </div>
  );
}
