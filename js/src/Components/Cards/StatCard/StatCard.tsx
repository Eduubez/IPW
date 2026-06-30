import styles from "./statcard.module.css";
import LoadingComponent from "../../LoadingComponent/LoadingComponent";
import PrimaryButton from "../../Buttons/PrimaryButton/PrimaryButton";

export function StatCard({
  icon,
  text,
  value,
  loading,
  button
}: {
  icon: { name: string; style?: React.CSSProperties };
  text: string | number;
  value: number | string;
  loading?: boolean;
  button?:{
    text: string;
    onClick: () => void;
    enabled: boolean;
  }
}) {
  return (
    <div className={styles["stat-card"]}>
      {loading ? (
        <LoadingComponent />
      ) : (
        <>
          <div className={styles["icon-container"]}>
            <span className={`material-symbols-outlined`} style={icon.style}>
              {icon.name}
            </span>
          </div>
          <span className={styles["value"]}>{value}</span>
          <span className={styles["text"]}>{text}</span>
          {button && (
            <PrimaryButton onClick={button.onClick} text={button.text} enabled={button.enabled} />
          )}
        </>
      )}
    </div>
  );
}
