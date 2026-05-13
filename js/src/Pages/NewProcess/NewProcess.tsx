import { useEffect, useState } from "react";
import { Header } from "../../Components/Layouts/Header/Header";
import { WithBackground } from "../../Components/Layouts/WithBackground/WithBackground";
import styles from "./newprocess.module.css";
import TextBox from "../../Components/Inputs/TextBox/TextBox";
import TextArea from "../../Components/Inputs/TextArea/TextArea";
import { DropDownMenu } from "../../Components/DropDownMenu/DropDownMenu";
import PrimaryButton from "../../Components/Buttons/PrimaryButton/PrimaryButton";
import { UsersApi } from "../../Utility/Api/UsersApi";
import { AreasApi, type AreaListResponse, type AreaResponse } from "../../Utility/Api/AreasApi";

const investigatorOptions = ["Averiguador 1", "Averiguador 2", "Averiguador 3"];
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
  const [investigatorId, setInvestigatorId] = useState<number | null>(null);
  const [supervisorId, setSupervisorId] = useState<number | null>(null);
  const [canBeFraud, setCanBeFraud] = useState(false);
  const [note, setNote] = useState("");

  const [investigators, setInvestigators] = useState<
    { id: number; name: string }[]
  >([]);
  const [supervisors, setSupervisors] = useState<
    { id: number; name: string }[]
  >([]);
  const [allAreas, setAllAreas] = useState<AreaResponse[]>([]);

  const isButtonEnabled = Boolean(
    name &&
    street &&
    county &&
    district &&
    area &&
    expiresAt &&
    priority &&
    investigatorId !== null &&
    supervisorId !== null,
  );

  const fetchAreas = async () => {
    const response = await AreasApi.getAll();
    if (response.success) {
      setAllAreas(response.data.areas);
    }
  }

  const fetchInvestigators = async () => {
    const response = investigatorOptions;
    setInvestigators(response.map((name, index) => ({ id: index + 1, name })));
  };
  const fetchSupervisors = async () => {
    const response = supervisorOptions;
    setSupervisors(response.map((name, index) => ({ id: index + 1, name })));
  };

  useEffect(() => {
    fetchInvestigators();
    fetchSupervisors();
    fetchAreas();
  }, []);

  const handleSubmit = () => {};

  const processFields = [
    {
      label: "Nome do Processo",
      value: name,
      onChange: setName,
      mandatory: true,
      type: "text",
    },
    {
      label: "Rua",
      value: street,
      onChange: setStreet,
      mandatory: true,
      type: "text",
    },
    {
      label: "Concelho",
      value: county,
      onChange: setCounty,
      mandatory: true,
      type: "text",
    },
    {
      label: "Distrito",
      value: district,
      onChange: setDistrict,
      mandatory: true,
      type: "text",
    },
    {
      label: "Area",
      value: area,
      onChange: setArea,
      mandatory: true,
      type: "text",
    },
    {
      label: "Expira em",
      value: expiresAt,
      onChange: setExpiresAt,
      mandatory: true,
      type: "date",
    },
  ];
  const dropdownFields = [
    {
      label: "Averiguador",
      options: investigators,
      onSelect: setInvestigatorId,
      mandatory: true,
    },
    {
      label: "Supervisor",
      options: supervisors,
      onSelect: setSupervisorId,
      mandatory: true,
    },
    {
      label: "Prioridade",
      options: priorityOptions,
      onSelect: setPriority,
      mandatory: true,
    },
  ];

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
              {processFields.map((field, index) => (
                <TextBox
                  key={index}
                  type={field.type}
                  label={field.label}
                  value={field.value}
                  onChange={field.onChange}
                  mandatory={field.mandatory}
                />
              ))}
            </div>
            <div className={styles["right-column"]}>
              <div className={styles["dropdown-container"]}>

                  {dropdownFields.map((field, index) => (
                                    <div className={styles["option"]}>
                    <DropDownMenu
                      key={index}
                      label={field.label}
                      options={field.options}
                      onSelect={field.onSelect}
                      mandatory={field.mandatory}
                    />
                    </div>
                  ))}
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
