import styles from "./statcard.module.css";

export function StatCard({
  icon,
  text,
  value,
}: {
  icon: { name: string; style?: React.CSSProperties };
  text: string;
  value: number | string;
}) {
  return (
    <div className={styles["stat-card"]}>
      <div className={styles["icon-container"]}>
        <span className={`material-symbols-outlined`} style={ icon.style} >{icon.name}</span>
      </div>
      <span className={styles["value"]}>{value}</span>
      <span className={styles["text"]}>{text}</span>
    </div>
  );
}
