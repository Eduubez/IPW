import { useState } from "react";
import { Header } from "../../Components/Layouts/Header/Header";
import { WithBackground } from "../../Components/Layouts/WithBackground/WithBackground";
import styles from "./newprocess.module.css";
import TextBox from "../../Components/Inputs/TextBox/TextBox";
import TextArea from "../../Components/Inputs/TextArea/TextArea";
import { DropDownMenu } from "../../Components/DropDownMenu/DropDownMenu";
import PrimaryButton from "../../Components/Buttons/PrimaryButton/PrimaryButton";

const averiguatorOptions = ["Averiguador 1", "Averiguador 2", "Averiguador 3"];
const supervisorOptions = ["Supervisor 1", "Supervisor 2", "Supervisor 3"];
const priorityOptions = ["Baixa", "Média", "Alta"];

export default function NewProcess() {
  const [name, setName] = useState("");
  const [street, setStreet] = useState("");
  const [county, setCounty] = useState("");
  const [district, setDistrict] = useState("");
  const [area, setArea] = useState("");
  const [priority, setPriority] = useState("");
  const [expiresAt, setExpiresAt] = useState("");
  const [investigatorId, setInvestigatorId] = useState<number|null>(null);
  const [supervisorId, setSupervisorId] = useState<number|null>(null);
  const [canBeFraud, setCanBeFraud] = useState(false);
  const [note, setNote] = useState("");

  const isButtonEnabled = Boolean(name && street && county && district && area && expiresAt && priority && investigatorId !== null && supervisorId !== null);

  const handleSubmit = () => {};

  return (
    <div className={styles["new-process-page"]}>
      <Header
        title="Insurance portal Worflow"
        description="Proceda a criação de um novo processo"
      />
      <WithBackground>
        <div className={styles["content"]}>
          <div className={styles["header"]}>
            <p>Criação do processo</p>
          </div>
          <div className={styles["form-container"]}>
            <div className={styles["left-column"]}>
              <TextBox
                label="Nome do Processo"
                type="text"
                value={name}
                onChange={setName}
                mandatory={true}
              />
              <TextBox
                label="Rua"
                type="text"
                value={street}
                onChange={setStreet}
                mandatory={true}
              />
              <TextBox
                label="Concelho"
                type="text"
                value={county}
                onChange={setCounty}
                mandatory={true}
              />
              <TextBox
                label="Distrito"
                type="text"
                value={district}
                onChange={setDistrict}
                mandatory={true}
              />
              <TextBox
                label="Area"
                type="text"
                value={area}
                onChange={setArea}
                mandatory={true}
              />
              <TextBox
                label="Expira em"
                type="date"
                value={expiresAt}
                onChange={setExpiresAt}
                mandatory={true}
              />
            </div>
            <div className={styles["right-column"]}>
              <div className={styles["dropdown-container"]}>
                <div className={styles["option"]}>
                  <DropDownMenu
                    label="Averiguador"
                    options={averiguatorOptions}
                    onSelect={setInvestigatorId}
                    mandatory={true}
                  />
                </div>
                <div className={styles["option"]}>
                  <DropDownMenu
                    label="Supervisor"
                    options={supervisorOptions}
                    onSelect={setSupervisorId}
                    mandatory={true}
                  />
                </div>
                <div className={styles["option"]}>
                  <DropDownMenu
                    label="Prioridade"
                    options={priorityOptions}
                    onSelect={setPriority}
                    mandatory={true}
                  />
                </div>
                <div className={styles["option"]}>
                  <label htmlFor="canBeFraud">
                    <span>Pode ser fraude</span>
                  </label>
                  <input
                    type="checkbox"
                    id="canBeFraud"
                    checked={canBeFraud}
                    onChange={(e) => setCanBeFraud(e.target.checked)}
                  />
                </div>
              </div>
              <div className={styles["note-container"]}>
                <TextArea
                  label="Nota"
                  value={note}
                  onChange={setNote}
                  mandatory={false}
                />
              </div>
            </div>
          </div>
          <div className={styles["submit-container"]}>
            <div className={styles["submit-button"]}>
              <PrimaryButton
                text="Criar"
                onClick={handleSubmit}
                enabled={isButtonEnabled}
              />
            </div>
          </div>
        </div>
      </WithBackground>
    </div>
  );
}
