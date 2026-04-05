import React from "react";
import styles from "./primarybadge.module.css";

export function PrimaryBadge({ text, style }: { text: string; style: React.CSSProperties }) {
  return (
    <div className={styles["badge"]} style={style}>
      <span className={`paragraph-s ${styles["badge-text"]}`}>{text}</span>
    </div>
  );
}