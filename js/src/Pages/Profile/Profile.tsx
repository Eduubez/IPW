import styles from "./profile.module.css";
import PrimaryButton from "../../Components/Buttons/PrimaryButton/PrimaryButton";
import { useTranslation } from "react-i18next";
import { Header } from "../../Components/Layouts/Header/Header";
import { WithBackground } from "../../Components/Layouts/WithBackground/WithBackground";
import { InformationCard } from "../../Components/Cards/InformationCard/InformationCard";
import { Icon } from "../../Components/Icons/Icons";
import { PrimaryBadge } from "../../Components/Badge/PrimaryBadge/PrimaryBadge";
import { Color } from "../../StyleGuide/colors";
import { LanguageSwitcher } from "../../Components/LanguageSwitcher/LanguageSwitcher";
import { userStore } from "../../Utility/Store/UserStore";
import { useEffect, useState } from "react";
import { ActivityApi } from "../../Utility/Api/ActivityApi";
import { type ActivityResponse } from "../../Utility/Api/ActivityApi";
import LoadingComponent from "../../Components/LoadingComponent/LoadingComponent";
import { ActivityCard } from "../../Components/Cards/ActivityCard/ActivityCard";
const mockUser = {
  name: "João Bezerra",
  email: "example@email.com",
  location: "Lisboa, Portugal",
  joinDate: new Date("2022-01-15"),
  role: "Admin",
};



const shortName = (name: string) => {
  const names = name.split(" ");
  if (names.length === 1) return names[0].charAt(0).toUpperCase();
  return names[0].charAt(0).toUpperCase() + names[1].charAt(0).toUpperCase();
};

export default function Profile() {
  const { t } = useTranslation();
  const role = userStore.getActiveRole();
  const userId = userStore.getUserId();
  const [isLoading, setIsLoading] = useState(false);
  const [activity, setActivity] = useState<ActivityResponse[]>([]);
  // Fetch user Activity
  const fetchUserActivity = async () => {
    if (!userId) return;
    try {
      setIsLoading(true);
      const response = await ActivityApi.getActivityByUser(userId, 0, 10);
      if (response.success) {
      setActivity(response.data);
      }
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchUserActivity();
  }, [userId]);

  const sortedActivity = [...activity].sort(
    (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
  ).slice(0, 5); // Get the 5 most recent activities

  const userBadge = () => {
    const normalizedRole = role?.toLowerCase(); //  "?" to satisfy TypeScript, at this point user ALWAYS has a role.
    switch (normalizedRole) {
      case "admin":
        return (
          <PrimaryBadge
            text={t("Roles.admin")}
            style={{ background: Color.Purple }}
          />
        );
      case "investigator":
        return (
          <PrimaryBadge
            text={t("Roles.investigator")}
            style={{ background: Color.DarkBlue }}
          />
        );
      case "triator":
        return (
          <PrimaryBadge
            text={t("Roles.triator")}
            style={{ background: Color.DarkRed }}
          />
        );
      case "supervisor":
        return (
          <PrimaryBadge
            text={t("Roles.supervisor")}
            style={{ background: Color.YellowPrimary }}
          />
        );
      case "manager":
        return (
          <PrimaryBadge
            text={t("Roles.manager")}
            style={{ background: Color.GreenPrimary }}
          />
        );
      default:
        return (
          <PrimaryBadge
            text={t("Roles.user")}
            style={{ background: Color.Gray }}
          />
        ); // Will never be shown
    }
  };

  return (
    <div className={styles["profile-container"]}>
      <Header
        title={t("Profile.title")}
        description={t("Profile.description")}
        loading={false}
      />
      <div className={styles["divider"]}>
        <WithBackground>
          <div className={styles["content-container"]}>
            <div className={styles["icon-container"]}>
              <div className={styles["profile-icon"]}>
                <span className={styles["profile-initials"]}>
                  {shortName(mockUser.name)}
                </span>
              </div>
            </div>
            <div className={styles["information-container"]}>
              <p className={styles["user-name"]}>{mockUser.name}</p>
              {userBadge()}

              <InformationCard
                icon={Icon.Location}
                title={"Location"}
                description={mockUser.location}
              />
              <InformationCard
                icon={Icon.Calendar}
                title={"Join Date"}
                description={mockUser.joinDate.toDateString()}
              />
            </div>
            <div className={styles["actions-container"]}>
              <div className={styles["action-item"]}>
                <PrimaryButton
                  text={"Change Password"}
                  onClick={() => alert("Clicked")}
                  enabled={true}
                />
              </div>
              <div className={styles["action-item"]}>
                <LanguageSwitcher />
              </div>
            </div>
          </div>
        </WithBackground>
      </div>
      <div className={styles["divider"]}>
        <WithBackground>
          <div className={styles["activity-container"]}>
            {isLoading ? (
              <LoadingComponent />
            ) : (
              <>
                <div className={styles["activity-header"]}>
                  <p className="subtitle-medium">Atividades Recentes</p>
                </div>
                <div className={styles["activity-list"]}>
                  {sortedActivity.map((item) => (
                    <ActivityCard key={item.id} activity={item} />
                  ))}
                </div>
              </>
            )}
          </div>
        </WithBackground>
      </div>
    </div>
  );
}
