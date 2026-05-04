import styles from "./header.module.css";
import { CircularProgress } from "@mui/material";

export function Header({
  title,
  description,
  loading = false,
}: {
  title: string;
  description: string;
  loading?: boolean;
}) {
  return (

    <div className={styles["header-container"]}>
      {loading ? (
        <CircularProgress aria-label="Loading…" />
      ) : (
        <>
          <h1 className="title-large">{title}</h1>
          <p className="subtitle-medium">{description}</p>
        </>
      )}
    </div>
  );
}
