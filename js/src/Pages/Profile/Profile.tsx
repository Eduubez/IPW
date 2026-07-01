import styles from "./profile.module.css";
import { useTranslation } from "react-i18next";
import { Header } from "../../Components/Layouts/Header/Header";
import { WithBackground } from "../../Components/Layouts/WithBackground/WithBackground";
import { InformationCard } from "../../Components/Cards/InformationCard/InformationCard";
import { Icon } from "../../Config/Icons";
import { PrimaryBadge } from "../../Components/Badge/PrimaryBadge/PrimaryBadge";
import { LanguageSwitcher } from "../../Components/LanguageSwitcher/LanguageSwitcher";
import { userStore } from "../../Utility/Store/UserStore";
import { useEffect, useMemo, useState } from "react";
import { ActivityApi } from "../../Utility/Api/ActivityApi";
import { type ActivityResponse } from "../../Utility/Api/ActivityApi";
import LoadingComponent from "../../Components/LoadingComponent/LoadingComponent";
import { ActivityCard } from "../../Components/Cards/ActivityCard/ActivityCard";
import { getRoleStyle } from "../../Utility/Helpers/RoleHelpers";
import { UsersApi, type UserProfileResponse } from "../../Utility/Api/UsersApi";

const shortName = (name: string) => {
  const names = name.split(" ");
  if (names.length === 1) return names[0].charAt(0).toUpperCase();
  return names[0].charAt(0).toUpperCase() + names[1].charAt(0).toUpperCase();
};




export default function Profile() {
  const { t } = useTranslation();
  const role = userStore.getActiveRole();
  const userId = userStore.getUserId();
  const [isLoading, setIsLoading] = useState(true);
  const [activity, setActivity] = useState<ActivityResponse[]>([]);
  const [userInfo, setUserInfo] = useState<UserProfileResponse>();

  // Fetch user Activity
  const fetchUserActivity = async () => {
    if (!userId) return;
    try {
      setIsLoading(true);
      const response = await ActivityApi.getActivityByUser(userId, 0, 10);
      if (response.success) {
        setActivity(response.data.results);
      }
    } finally {
      setIsLoading(false);
    }
  };
  const fetchUserInformation = async () => {
    const response = await UsersApi.getUserInformation();
    if (response.success) {
      setUserInfo(response.data);
    }
  };

  useEffect(() => {
    setIsLoading(true);
    fetchUserActivity();
    fetchUserInformation();
    setIsLoading(false);
  }, [userId]);

  const sortedActivity = [...activity]
    .sort(
      (a, b) =>
        new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
    )
    .slice(0, 5); // Get the 5 most recent activities

  const userBadge = () => {
    const normalizedRole = role?.toLowerCase();
    return (
      <PrimaryBadge
        text={t(`Roles.${normalizedRole ?? "user"}`)}
        style={getRoleStyle(normalizedRole ?? "")}
      />
    );
  };

  const informationItems = useMemo(
    () => [
      { icon: Icon.Email, title: t("Label.email"), description: userInfo?.email ?? "" },
      {
        icon: Icon.Group,
        title: t("Profile.areaLabel"),
        description: t(`Areas.${userInfo?.area}`) ?? t("Profile.notSpecified"),
      },
    ],
    [userInfo, t],
  );

  return (
    <div className={styles["profile-container"]}>
      <Header
        title={t("Profile.title")}
        description={t("Profile.description")}
        loading={false}
      />
      <div className={styles["divider"]}>
        <WithBackground>
          {userInfo && (
            <div className={styles["content-container"]}>
              <div className={styles["icon-container"]}>
                <div className={styles["profile-icon"]} style={{ backgroundColor: getRoleStyle(role ?? "").background as string }}>
                  <span className={styles["profile-initials"]} >
                    {shortName(userInfo.name)}
                  </span>
                </div>
              </div>
              <div className={styles["information-container"]}>
                <p className={styles["user-name"]}>{userInfo.name}</p>
                {userBadge()}
                {informationItems.map((item) => (
                  <InformationCard
                    key={item.title}
                    icon={item.icon}
                    title={item.title}
                    description={item.description}
                  />
                ))}
              </div>
              <div className={styles["actions-container"]}>
                <div className={styles["action-item"]}>
                  <LanguageSwitcher />
                </div>
              </div>
            </div>
          )}
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
                  <p className="subtitle-medium">
                    {t("Profile.recentActivities")}
                  </p>
                </div>
                <div className={styles["activity-list"]}>
                  {sortedActivity.map((item) => (
                    <ActivityCard key={item.id} activity={{ label: item.action, date: item.createdAt }} />
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
