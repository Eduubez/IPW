import { useTranslation } from "react-i18next";
import styles from "./login.module.css";
import logo from "../../assets/images/logo.svg";
import TextBox from "../../Components/Inputs/TextBox/TextBox";
import { useState } from "react";
import PrimaryButton from "../../Components/Buttons/PrimaryButton/PrimaryButton";
import { Color } from "../../StyleGuide/colors";
import { Icon } from "../../Components/Icons/Icons";
import { AuthApi } from "../../Utility/Api/LoginApi";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useSnackbar } from "notistack";
import { ToastType } from "../../Types/ToastType";


export default function Login() {
  const { t } = useTranslation();
  const { enqueueSnackbar } = useSnackbar();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  //const [isLoading, setIsLoading] = useState(false);


  const isButtonEnabled = email.length > 0 && password.length > 0;
  

  const handleLogin = async () => {
    //setIsLoading(true);

    try {
      const response = await AuthApi.login({ email, password });
      if (response.success) {
        localStorage.setItem("loggedIn", "true");
        const returnUrl = searchParams.get("returnUrl");
        if (returnUrl) {
          navigate(returnUrl, { replace: true });
        } else {
          navigate("/role-selection", { replace: true });
        }
      }else{
        enqueueSnackbar(response.message, {
          variant: ToastType.ERROR,
        });
      }

    } finally {
      //setIsLoading(false);
    }
  };

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
              icon={{ name: Icon.AccountCircle, style: { color: Color.Gray } }}
            />
            <TextBox
              label={t("Label.password")}
              type="password"
              value={password}
              onChange={setPassword}
              icon={{ name: Icon.Lock, style: { color: Color.Gray } }}
            />
          </form>
          <div className={styles["button-wrapper"]}>
            <PrimaryButton
              text={t("Label.enter")}
              onClick={handleLogin}
              enabled={isButtonEnabled}
            />
          </div>
        </div>
      </div>
      <div className={styles["footer"]}></div>
    </div>
  );
}
