import styles from "./informationcard.module.css";
import LoadingComponent from "../../LoadingComponent/LoadingComponent";
export function InformationCard({
  icon,
  title,
  description,
  loading,
}: {
  icon: string;
  title: string;
  description: string;
  loading?: boolean;
}) {
  if (loading) return <LoadingComponent />;
  return (
    <div className={styles["information-card"]}>
      <div className={styles["information-icon-container"]}>
        <span
          className={`material-symbols-outlined ${styles["information-icon"]}`}>
          {icon}
        </span>
      </div>
      <div className={styles["information-content"]}>
        <span className={styles["information-content-title"]}>{title}</span>
        <span className={styles["information-content-description"]}>{description}</span>
      </div>
    </div>
  );
}
