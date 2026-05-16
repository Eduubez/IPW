import { useState } from "react";
import styles from "./sidebar.module.css";
import { useNavigate } from "react-router-dom";
import { AuthApi } from "../../Utility/Api/LoginApi";
import { userStore } from "../../Utility/Store/UserStore";
import { ROLES } from "../../MockData/MockRoles";

export default function SideBar() {
  const [isExpanded, setIsExpanded] = useState(false);
  const activeRole = userStore.getActiveRole();
  const navigate = useNavigate();

  const navigationItems = activeRole ? ROLES.find(role => role.key === activeRole)!.navigationItems : [];
  const handleNavigation = (path: string) => {
    navigate(path);
  };

  const handleExpand = () => {
    setIsExpanded(!isExpanded);
  };
  const handleLogout = async () => {
    const response = await AuthApi.logout();
    if(response.success) {
      navigate("/login");
    }
  }

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
      <div className={styles["logout-container"]}>
        <a onClick={() => handleLogout()}>
          <span className="material-symbols-outlined">logout</span>
        </a>
      </div>
      <div className={styles["profile-container"]}>
        <a onClick={() => handleNavigation("/profile")}>
          <span className="material-symbols-outlined">account_circle</span>
        </a>
      </div>
    </div>
  );
}
