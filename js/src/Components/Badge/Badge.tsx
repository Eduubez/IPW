import React from "react";
import styles from "./badge.module.css";

export function Badge({ text, style }: { text: string; style: React.CSSProperties }) {
  return (
    <div className={styles["badge"]} style={style}>
      <span className={`paragraph-s ${styles["badge-text"]}`}>{text}</span>
    </div>
  );
}