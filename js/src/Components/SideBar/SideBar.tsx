import { useState } from "react";
import styles from "./sideBar.module.css";
import { useNavigate } from "react-router-dom";

const navigationItems = [
  { name: "Inicio", path: "/dashboard", icon: "dashboard" },
  { name: "Historico", path: "/user/history", icon: "assignment" },
  { name: "Mudar de papel", path: "/role-selection", icon: "bar_chart" },
];

export default function SideBar() {
  const [isExpanded, setIsExpanded] = useState(false);
  const navigate = useNavigate();

  const handleNavigation = (path: string) => {
    navigate(path);
  };

  const handleExpand = () => {
    setIsExpanded(!isExpanded);
  };

  return (
    <div
      className={`${styles["side-bar"]} ${isExpanded ? styles["expandedSidebar"] : styles["collapsedSidebar"]}`}>
      <div className={styles["icon-container"]} onClick={handleExpand}>
        <span className="material-symbols-outlined">menu</span>
      </div>
      <div className={styles["navigation-container"]}>
        {navigationItems.map((item) => (
          <div key={item.path} className={styles["navigation-item"]}>
            <a onClick={() => handleNavigation(item.path)} className={styles["nav-link"]}>
              <div className={styles["nav-icon-container"]}>
                <span className="material-symbols-outlined">{item.icon}</span>
              </div>
              {isExpanded && (
                <span className={styles["navigation-text"]}>{item.name}</span>
              )}
            </a>
          </div>
        ))}
      </div>
      <div className={styles["profile-container"]}>
        <a onClick={() => handleNavigation("/profile")}>
          <span className="material-symbols-outlined">account_circle</span>
        </a>
      </div>
    </div>
  );
}
