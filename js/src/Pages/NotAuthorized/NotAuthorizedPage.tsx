import styles from "./notauthorizedpage.module.css";
import PrimaryButton from "../../Components/Buttons/PrimaryButton/PrimaryButton";
import { useTranslation } from "react-i18next";

export function NotAuthorizedPage() {
    const {t} = useTranslation();

  return (
    <div className={styles["not-authorized-page-container"]}>
      <h1>{t("NotAuthorizedPage.title")}</h1>
      <p>{t("NotAuthorizedPage.description")}</p>
      <div className={styles["button-container"]}>
        <PrimaryButton
          onClick={() => window.history.back()}
          text={t("NotAuthorizedPage.goBack")}
          enabled={true}
        />
      </div>
    </div>
  );
}
