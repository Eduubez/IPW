import React from "react";
import styles from "./timelineactivityitem.module.css";
import { Icon } from "../../Icons/Icons";

export function TimeLineActivityItem({
  title,
  time,
  user,
}: {
  title: string;
  time: string;
  user: string;
}) {
  return (
    <div className={`${styles["activity-item"]}`}>

        <p className="paragraph-s-regular">{title}</p>

      <div className={styles["activity-content"]}>
        <div className={styles["activity-time-wrapper"]}>
          <span className={`material-symbols-outlined`} style={{ fontSize: "1rem"}}>{Icon.Clock}</span>
          <span className={styles["content"]}>{time}</span>
        </div>
        <div className={styles["activity-time-wrapper"]}>
          <span className={`material-symbols-outlined`} style={{ fontSize: "1rem"}}>
            {Icon.AccountCircle}
          </span>
          <span className={styles["content"]}>{user}</span>
        </div>
      </div>
    </div>
  );
}
