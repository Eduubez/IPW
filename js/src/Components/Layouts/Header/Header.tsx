import styles from "./header.module.css";

export function Header({
  title,
  description,
}: {
  title: string;
  description: string;
}) {
  return (
    <div className={styles["header-container"]}>
      <h1 className="title-large">{title}</h1>
      <p className="subtitle-medium">{description}</p>
    </div>
  );
}
