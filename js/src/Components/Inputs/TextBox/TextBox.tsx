import styles from "./textbox.module.css";

export default function TextBox({
  label,
  type,
  value,
  icon,
  onChange,
}: {
  label: string;
  type: string;
  value: string;
  icon?: { name: string; style?: React.CSSProperties };
  onChange: (value: string) => void;
  style?: React.CSSProperties;
}) {
  return (
    <div className={styles["textbox-container"]}>
      <span className={styles["label"]}>{label}</span>
      <div className={styles["icon-input-wrapper"]}>
        {icon && <span className="material-symbols-outlined" style={icon.style}>{icon.name}</span>}
        <input
        className={styles["input-box"]}
          type={type}
          value={value}
          onChange={(e) => onChange(e.target.value)}
        />
      </div>
    </div>
  );
}
