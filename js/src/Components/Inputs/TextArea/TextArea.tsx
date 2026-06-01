import styles from "./textarea.module.css";

export default function TextArea({
  label,
  value,
  onChange,
  mandatory = false,
  readOnly = false,
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
  mandatory?: boolean;
  readOnly?: boolean;
}) {
  return (
      <div className={styles["text-area-container"]}>
        <label>
          <span>{label}</span> {mandatory && <span style={{ color: "red" }}> *</span>}
        </label>
        <textarea className={styles["text-area-container"]}
          value={value}
          onChange={(e) => !readOnly && onChange(e.target.value)}
          readOnly={readOnly}
          rows={4}
          style={{ width: "100%" }}
        />
      </div>
  );
}
