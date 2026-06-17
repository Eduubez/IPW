import PrimaryButton from "../../Buttons/PrimaryButton/PrimaryButton";
import styles from "./noreport.module.css";
import { useTranslation } from "react-i18next";

export const NoReport = ({ onClick }: { onClick: () => void }) => {
  const { t } = useTranslation();
  return (
    <div className={styles["no-report-container"]}>
      <span className="material-symbols-outlined">error</span>
      <span>
        {t("reportCard.noReport")}
      </span>
      <PrimaryButton
      style={{ width: "180px", height: "fit-content" }}
      text={t("reportCard.createReport")}
      enabled={true}
      onClick={onClick}
      />
    </div>
  );
}

