import { useTranslation } from "react-i18next";
import styles from "./login.module.css";
import logo from "../../assets/images/logo.svg";
import TextBox from "../../Components/Inputs/TextBox/TextBox";
import { useState } from "react";
import PrimaryButton from "../../Components/Buttons/PrimaryButton/PrimaryButton";
import { Color } from "../../StyleGuide/colors";
import { Icon } from "../../Components/Icons/Icons";

export default function Login() {
  const { t } = useTranslation();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const isButtonEnabled = email.length > 0 && password.length > 0;

  return (
    <div className={styles["login-container"]}>
      <div className={styles["header"]}>
        <div className={styles["title-icon-wrapper"]}>
          <img src={logo} alt="logo" className={styles["logo"]} />
          <span>{t("Login.appName")}</span>
        </div>
        <span>{t("Login.appSubtitle")}</span>
      </div>
      <div className={styles["body"]}>
        <div className={styles["body-top"]}>
          <span className={styles["subtitle"]}>{t("Login.welcome")}</span>
          <span>{t("Login.welcomeText")}</span>
        </div>
        <div className={styles["body-bottom"]}>
          <form>
            <TextBox
              label={t("Label.email")}
              type="text"
              value={email}
              onChange={setEmail}
              icon={{name:Icon.AccountCircle ,style:{color:Color.Gray}}}
            />
            <TextBox
              label={t("Label.password")}

              type="password"
              value={password}
              onChange={setPassword}
              icon={{name:Icon.Lock ,style:{color:Color.Gray}}}
            />
          </form>
          <div className={styles["button-wrapper"]}>
            <PrimaryButton
              text={t("Label.enter")}
              onClick={() => {}}
              enabled={isButtonEnabled}
            />
          </div>
        </div>
      </div>
      <div className={styles["footer"]}></div>
    </div>
  );
}
