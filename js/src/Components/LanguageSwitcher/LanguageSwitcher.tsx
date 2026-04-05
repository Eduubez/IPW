import { useTranslation } from "react-i18next";
import ptflag from "../../assets/images/pt_flag.svg";
import enflag from "../../assets/images/uk_flag.svg";
import styles from "./languageswitcher.module.css";


export function LanguageSwitcher() {
  const { i18n } = useTranslation();

  const changeLanguage = (lng: string) => {
    localStorage.setItem("i18nextLng", lng);
    void i18n.changeLanguage(lng);
  };

  return (
    <div className={styles["language-switcher"]}>
      <button onClick={() => changeLanguage("pt")} className={styles["language-button"]}>
        <img src={ptflag} alt="PT" className={styles["language-flag"]} />
      </button>
      <button onClick={() => changeLanguage("en")} className={styles["language-button"]}>
        <img src={enflag} alt="EN" className={styles["language-flag"]} />
      </button>
    </div>
  );
}