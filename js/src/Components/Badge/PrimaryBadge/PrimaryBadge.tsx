import React from "react";
import styles from "./primarybadge.module.css";
import CircularProgress from "@mui/material/CircularProgress";

export function PrimaryBadge({ text, style ,loading}: { text: string; style?: React.CSSProperties, loading?: boolean }) {
  return (
    <div className={styles["badge"]} style={style}>
      {loading ? (
        <div style={{display: "flex", alignItems : "center" , justifyContent : "center", height: "100%"}}>

        <CircularProgress aria-label="Loading…" />
        </div>
      ) : (
        <span className={`paragraph-s ${styles["badge-text"]}`}>{text}</span>
      )}
    </div>
  );
}