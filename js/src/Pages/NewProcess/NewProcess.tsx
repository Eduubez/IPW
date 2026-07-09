import { useEffect, useState } from "react";
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
  type AreaResponse,
} from "../../Utility/Api/AreasApi";
import { useTranslation } from "react-i18next";
import { ProcessApi } from "../../Utility/Api/ProcessApi";

const priorityOptions = ["NORMAL", "WITH_PRIORITY", "URGENT"];
const normalizePriority = (priority: string) : string => {
  return priority.toLowerCase()
}
export default function NewProcess() {
  const { t } = useTranslation();
  const [name, setName] = useState("");
  const [street, setStreet] = useState("");
  const [county, setCounty] = useState("");
  const [district, setDistrict] = useState("");
  const [area, setArea] = useState("");
  const [areaId, setAreaId] = useState<number | null>(null);
  const [priority, setPriority] = useState("");
  const [expiresAt, setExpiresAt] = useState("");
  const [investigatorId, setInvestigatorId] = useState<number | null>(null);
  const [supervisorId, setSupervisorId] = useState<number | null>(null);
  const [canBeFraud, setCanBeFraud] = useState(false);
  const [note, setNote] = useState("");
  const [resetKey, setResetKey] = useState(0);

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

  const fetchInvestigators = async (id: number) => {
    const response = await UsersApi.getInvestigators(id);
    if (response.success) {
      setInvestigators(response.data.results.map((investigator) => ({ id: investigator.id, name: investigator.name })));
    }
  };

  useEffect(() => {
    fetchAreas();
  }, []);

  useEffect(() => {
    if (area !== "" && allAreas.length > 0) {
      const selected = allAreas.find((a) => a.name === area);
      if (selected) {
        setAreaId(selected.id);
        fetchInvestigators(selected.id);
        if (selected.bossId !== null) {
          setSupervisors([{ id: selected.bossId, name: selected.bossName!, areaId: selected.id }]);
        } else {
          setSupervisors([]);
        }
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
      priority: normalizePriority(priority) as "normal" | "with_priority" | "urgent",
      expiresAt: `${expiresAt}T00:00:00`,
      investigatorId: investigatorId!,
      supervisorId: supervisorId!,
      canBeFraud,
      note : note ? note : null,
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
      setResetKey((k) => k + 1); // force reset of dropdowns
    }
  };

  const processFields = [
    {
      label: t("CreateProcessPage.fields.name"),
      value: name,
      onChange: setName,
      mandatory: true,
      type: "text",
    },
    {
      label: t("CreateProcessPage.fields.street"),
      value: street,
      onChange: setStreet,
      mandatory: true,
      type: "text",
    },
    {
      label: t("CreateProcessPage.fields.county"),
      value: county,
      onChange: setCounty,
      mandatory: true,
      type: "text",
    },
    {
      label: t("CreateProcessPage.fields.district"),
      value: district,
      onChange: setDistrict,
      mandatory: true,
      type: "text",
    },
    {
      label: t("CreateProcessPage.fields.expiresAt"),
      value: expiresAt,
      onChange: setExpiresAt,
      mandatory: true,
      type: "date",
    },
  ];
  const dropdownFields = [
    {
      label: t("CreateProcessPage.fields.area"),
      options: allAreas.map((area) => ({ id: area.name, name: t(`Areas.${area.name}`) })),
      onSelect: setArea,
      mandatory: true,
      disabled: allAreas.length === 0,
    },
    {
      label: t("CreateProcessPage.fields.investigator"),
      options: investigators,
      onSelect: setInvestigatorId,
      mandatory: true,
      disabled: area === "" 
    },
    {
      label: t("CreateProcessPage.fields.supervisor"),
      options: supervisors,
      onSelect: setSupervisorId,
      mandatory: true,
      disabled: area === ""
    },
    {
      label: t("CreateProcessPage.fields.priority"),
      options: priorityOptions.map((priority) => ({ id: priority, name: t(`Priority.${priority}`) })),
      onSelect: setPriority,
      mandatory: true,
      disabled: area === ""
    },
  ];
  return (
    <div className={styles["new-process-page"]}>
      <Header
        title={t("CreateProcessPage.title")}
        description={t("CreateProcessPage.description")}
      />
      <WithBackground>
        <div className={styles["content"]}>
          <div className={styles["header"]}>
            <p>{t("CreateProcessPage.formHeader")}</p>
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
                      key={`${resetKey}-${index}`}
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
                    <span>{t("CreateProcessPage.fields.canBeFraud")}</span>
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
                  label={t("CreateProcessPage.fields.note")}
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
                text={t("CreateProcessPage.submit")}
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
