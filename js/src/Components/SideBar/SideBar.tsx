import { useState } from "react";
import styles from "./sideBar.module.css";

export default function SideBar() {
  const [isExpanded, setIsExpanded] = useState(false);

  return (
    <div
      className={`${styles["side-bar"]} ${isExpanded ? styles["expandedSidebar"] : styles["collapsedSidebar"]}`}>
      <div onClick={() => setIsExpanded(!isExpanded)} className={styles["icon-container"]}>
        <span
          className="material-symbols-outlined">
          menu
        </span>
      </div>
      <div className={styles["profile-container"]}>
        <a href="/profile">
          <span className="material-symbols-outlined">account_circle</span>
        </a>
      </div>
    </div>
  );
}
