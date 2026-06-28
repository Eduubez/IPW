import { useEffect, useState } from "react";
import { PublicApi } from "../../Utility/Api/PublicApi";
import styles from "./contactadmin.module.css";
import PrimaryButton from "../Buttons/PrimaryButton/PrimaryButton";
import { useNavigate } from "react-router-dom";
import { userStore } from "../../Utility/Store/UserStore";
import { useTranslation } from "react-i18next";

export const ContactAdmin = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [adminInformation, setAdminInformation] = useState<{
    name: string;
    email: string;
  } | null>(null);
  useEffect(() => {
    const fetchAdminInformation = async () => {
      const response = await PublicApi.adminInformation();
      if (response.success) {
        setAdminInformation(response.data);
      }
    };

    fetchAdminInformation();
  }, []);

  const handleLogout = async () => {
    userStore.clear();
    navigate("/login");
  };

  return (
    <div className={styles["contact-admin-container"]}>
      <p>
        {t("ContactAdmin.description")}
      </p>
      {adminInformation && (
        <a href={`mailto:${adminInformation.email}`}>{adminInformation.name}</a>
      )}
      <div className={styles["contact-admin-button-container"]}>
        <PrimaryButton onClick={handleLogout} text={t("ContactAdmin.logout")} enabled={true} />
      </div>
    </div>
  );
};
