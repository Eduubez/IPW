import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Header } from "../../Components/Layouts/Header/Header";
import { WithBackground } from "../../Components/Layouts/WithBackground/WithBackground";
import styles from "./newprocess.module.css";
import TextBox from "../../Components/Inputs/TextBox/TextBox";
import TextArea from "../../Components/Inputs/TextArea/TextArea";
import { DropDownMenu } from "../../Components/DropDownMenu/DropDownMenu";
import PrimaryButton from "../../Components/Buttons/PrimaryButton/PrimaryButton";
import { UsersApi } from "../../Utility/Api/UsersApi";
import {
  AreasApi,
  type AreaListResponse,
  type AreaResponse,
} from "../../Utility/Api/AreasApi";
import { useTranslation } from "react-i18next";
import { ProcessApi } from "../../Utility/Api/ProcessApi";

const priorityOptions = ["NORMAL", "WITH_PRIORITY", "URGENT"];

export default function NewProcess() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [name, setName] = useState("");
  const [street, setStreet] = useState("");
  const [county, setCounty] = useState("");
  const [district, setDistrict] = useState("");
  const [area, setArea] = useState("-1");
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
    { id: number; name: string; areaId: number }[]
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
  };

  const fetchInvestigators = async () => {
    if(area === "") return;
    const response = await UsersApi.getInvestigators(Number(area));
    if (response.success) {
      setInvestigators(response.data.results.map((investigator) => ({ id: investigator.id, name: investigator.name })));
    }
  };

  useEffect(() => {
    fetchAreas();
  }, []);

  useEffect(() => {
    fetchInvestigators();
  }, [area]);

  useEffect(() => {
    if (area !== "" && allAreas.length > 0) {
      const selected = allAreas.find((a) => a.id === Number(area));
      if (selected && selected.bossId !== null) {
        setSupervisors([{ id: selected.bossId, name: selected.bossName!, areaId: selected.id }]);
      } 
    }
  }, [area, allAreas]);

   
  const handleSubmit = async () => {
    const response = await ProcessApi.create({
      name,
      street,
      county,
      district,
      latitude: null,
      longitude: null,
      area,
      priority: priority as "NORMAL" | "WITH_PRIORITY" | "URGENT",
      expiresAt: `${expiresAt}T00:00:00`,
      investigatorId: investigatorId!,
      supervisorId: supervisorId!,
      canBeFraud,
      note,
    });
    if (response.success) {
      // reset form
      setName("");
      setStreet("");
      setCounty("");
      setDistrict("");
      setArea("");
      setPriority("");
      setExpiresAt("");
      setInvestigatorId(null);
      setSupervisorId(null);
      setCanBeFraud(false);
      setNote("");
    }
  };

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
      label: "Expira em",
      value: expiresAt,
      onChange: setExpiresAt,
      mandatory: true,
      type: "date",
    },
  ];
  const dropdownFields = [
    {
      label: "Area",
      options: allAreas.map((area) => ({ id: area.name, name: area.name })),
      onSelect: setArea,
      mandatory: true,
      disabled: allAreas.length === 0,
    },
    {
      label: "Averiguador",
      options: investigators,
      onSelect: setInvestigatorId,
      mandatory: true,
      disabled: area === "" 
    },
    {
      label: "Supervisor",
      options: supervisors,
      onSelect: setSupervisorId,
      mandatory: true,
      disabled: area === ""
    },
    {
      label: "Prioridade",
      options: priorityOptions.map((priority) => ({ id: priority, name: t(`Priority.${priority}`) })),
      onSelect: setPriority,
      mandatory: true,
      disabled: area === ""
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
                      disabled={field.disabled}
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
