import React from "react";
import styles from "./profile.module.css";
import PrimaryButton from "../../Components/Buttons/PrimaryButton/PrimaryButton";
import { useTranslation } from "react-i18next";
import { Header } from "../../Components/Layouts/Header/Header";
import { WithBackground } from "../../Components/Layouts/WithBackground/WithBackground";
import { InformationCard } from "../../Components/Cards/InformationCard/InformationCard";
import { Icon } from "../../Components/Icons/Icons";
import { Badge } from "../../Components/Badge/Badge";
import { Color } from "../../StyleGuide/colors";
const mockUser = {
  name: "João Silva",
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

  return (
    <div className={styles["profile-container"]}>
      <Header
        title={t("Profile.title")}
        description={t("Profile.description")}
      />
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
            <Badge
              text={mockUser.role}
              style={{ background: Color.GreenPrimary }}
            />
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
          </div>
        </div>
      </WithBackground>
    </div>
  );
}
