import PrimaryButton from "../../Buttons/PrimaryButton/PrimaryButton";
import styles from "./noreport.module.css";

export const NoReport = ({ onClick }: { onClick: () => void }) => {
  return (
    <div className={styles["no-report-container"]}>
      <span className="material-symbols-outlined">error</span>
      <span>
        Nao ha um relatorio para este processo.
      </span>
      <PrimaryButton
      style={{ width: "180px", height: "fit-content" }}
      text={"Criar relatorio"}
      enabled={true}
      onClick={onClick}
      />
    </div>
  );
}

