import { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import PrimaryButton from "../../../../Components/Buttons/PrimaryButton/PrimaryButton";
import TextBox from "../../../../Components/Inputs/TextBox/TextBox";
import { AreasApi, type AreaResponse } from "../../../../Utility/Api/AreasApi";
import { UsersApi } from "../../../../Utility/Api/UsersApi";
import { ROLES, ROLE_KEYS } from "../../../../MockData/MockRoles";
import styles from "./CreateUserModal.module.css";

const AREA_ROLES = [ROLE_KEYS.INVESTIGATOR, ROLE_KEYS.SUPERVISOR];

type CreateUserModalProps = {
  open: boolean;
  onClose: () => void;
  onSuccess: () => void;
  triggerRenderFn: () => void;
};

export default function CreateUserModal({
  open,
  onClose,
  onSuccess,
  triggerRenderFn,
}: CreateUserModalProps) {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [selectedRoles, setSelectedRoles] = useState<string[]>([]);
  const [selectedAreaId, setSelectedAreaId] = useState<number | null>(null);
  const [areas, setAreas] = useState<AreaResponse[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { t } = useTranslation();

  const needsArea = useMemo(
    () => selectedRoles.some((role) => AREA_ROLES.includes(role)),
    [selectedRoles],
  );

  const hasSupervisorRole = selectedRoles.includes(ROLE_KEYS.SUPERVISOR);

  const availableAreas = useMemo(() => {
    if (!hasSupervisorRole) {
      return areas;
    }

    return areas.filter((area) => area.bossId === null);
  }, [areas, hasSupervisorRole]);

  useEffect(() => {
    if (open) {
      setName("");
      setEmail("");
      setPassword("");
      setSelectedRoles([]);
      setSelectedAreaId(null);
    }
  }, [open]);

  useEffect(() => {
    if (!open) return;

    async function loadAreas() {
      const response = await AreasApi.getAll();

      if (response.success) {
        setAreas(response.data.areas);
      }
    }

    loadAreas();
  }, [open]);

  useEffect(() => {
    if (!needsArea) {
      setSelectedAreaId(null);
    }
  }, [needsArea]);

  useEffect(() => {
    if (selectedAreaId === null) return;

    const selectedAreaIsAvailable = availableAreas.some(
      (area) => area.id === selectedAreaId,
    );

    if (!selectedAreaIsAvailable) {
      setSelectedAreaId(null);
    }
  }, [availableAreas, selectedAreaId]);

  if (!open) return null;

  const toggleRole = (role: string) => {
    setSelectedRoles((currentRoles) =>
      currentRoles.includes(role)
        ? currentRoles.filter((currentRole) => currentRole !== role)
        : [...currentRoles, role],
    );
  };

  const isSubmitButtonEnabled = () => {
    if(isSubmitting) return false;
    if (name.trim().length === 0 || email.trim().length === 0) return false;
    if (password.length < 5) return false;
    if (selectedRoles.length === 0) return false;
    if (needsArea && selectedAreaId === null) return false;
    return true;
  }
  const handleSubmit = async () => {
    setIsSubmitting(true);
    const response = await UsersApi.create({
      name: name.trim(),
      email: email.trim(),
      password,
      areaId: needsArea ? selectedAreaId : null,
      roles: selectedRoles,
    });
    setIsSubmitting(false);

    triggerRenderFn();
    onSuccess();
    onClose();
  };

  return (
    <div className={styles["modal-backdrop"]} role="presentation">
      <form
        className={styles["modal-card"]}
        onSubmit={(event) => {
          event.preventDefault();
          handleSubmit();
        }}
      >
        <button
          type="button"
          className={styles["close-button"]}
          onClick={onClose}
        >
          <span className="material-symbols-outlined">close</span>
        </button>

        <div className={styles["modal-header"]}>
          <h2>{t("DashboardAdmin.createUser.title")}</h2>
          <p>{t("DashboardAdmin.createUser.description")}</p>
        </div>

        <div className={styles["text-field"]}>
          <TextBox
            label={t("DashboardAdmin.createUser.name")}
            type="text"
            value={name}
            onChange={setName}
            mandatory={true}
          />
        </div>

        <div className={styles["text-field"]}>
          <TextBox
            label={t("DashboardAdmin.createUser.email")}
            type="email"
            value={email}
            onChange={setEmail}
            mandatory={true}
          />
        </div>

        <div className={styles["text-field"]}>
          <TextBox
            label={t("DashboardAdmin.createUser.password")}
            type="password"
            value={password}
            onChange={setPassword}
            mandatory={true}
          />
        </div>

        <div className={styles["roles-list"]}>
          {ROLES.map((role) => (
            <label key={role.key} className={styles["role-option"]}>
              <input
                type="checkbox"
                checked={selectedRoles.includes(role.key)}
                onChange={() => toggleRole(role.key)}
              />
              <span>{t(`Roles.${role.key}`)}</span>
            </label>
          ))}
        </div>

        {needsArea && (
          <label className={styles["form-field"]}>
            <span>{t("DashboardAdmin.createUser.area")}</span>
            <select
              value={selectedAreaId ?? ""}
              onChange={(event) =>
                setSelectedAreaId(
                  event.target.value === "" ? null : Number(event.target.value),
                )
              }
            >
              <option value="">{t("DashboardAdmin.createUser.chooseArea")}</option>
              {availableAreas.map((area) => (
                <option key={area.id} value={area.id}>
                  {area.name}
                  {area.bossName ? ` · ${t("DashboardAdmin.createUser.supervisorLabel")}: ${area.bossName}` : ""}
                </option>
              ))}
            </select>
          </label>
        )}

        <div className={styles["primary-action"]}>
          <PrimaryButton
            text={isSubmitting ? t("DashboardAdmin.createUser.creating") : t("DashboardAdmin.createUser.create")}
            onClick={() => {
              handleSubmit();
            }}
            enabled={isSubmitButtonEnabled()}
          />
        </div>
      </form>
    </div>
  );
}
