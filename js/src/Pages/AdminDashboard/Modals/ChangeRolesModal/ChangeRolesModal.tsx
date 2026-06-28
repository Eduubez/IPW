import { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import PrimaryButton from "../../../../Components/Buttons/PrimaryButton/PrimaryButton";
import { AreasApi, type AreaResponse } from "../../../../Utility/Api/AreasApi";
import { UsersApi, type UserResponse } from "../../../../Utility/Api/UsersApi";
import { ROLES, ROLE_KEYS } from "../../../../MockData/MockRoles";
import styles from "./ChangeRolesModal.module.css";

const AREA_ROLES = new Set([ROLE_KEYS.INVESTIGATOR, ROLE_KEYS.SUPERVISOR]);

type ChangeRolesModalProps = {
  open: boolean;
  user: UserResponse | null;
  onClose: () => void;
  onSuccess: () => void;
  triggerRenderFn: () => void;
};

export default function ChangeRolesModal({
  open,
  user,
  onClose,
  onSuccess,
  triggerRenderFn,
}: ChangeRolesModalProps) {
  const { t } = useTranslation();
  const [selectedRoles, setSelectedRoles] = useState<string[]>([]);
  const [selectedAreaId, setSelectedAreaId] = useState<number | null>(null);
  const [areas, setAreas] = useState<AreaResponse[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const needsArea = useMemo(
    () => selectedRoles.some((role) => AREA_ROLES.has(role)),
    [selectedRoles],
  );

  const hasSupervisorRole = selectedRoles.includes(ROLE_KEYS.SUPERVISOR);

  const availableAreas = useMemo(() => {
    if (!hasSupervisorRole) {
      return areas;
    }

    return areas.filter(
      (area) => area.bossId !== user?.id,
    );
  }, [areas, hasSupervisorRole, user?.id]);
  console.log("Available areas for selection: ", availableAreas);

  useEffect(() => {
    if (open && user !== null) {
      setSelectedRoles(user.roles);
      setSelectedAreaId(user.areaId);
    }
  }, [open, user]);

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

  if (!open || user === null) return null;

  const toggleRole = (role: string) => {
    setSelectedRoles((currentRoles) =>
      currentRoles.includes(role)
        ? currentRoles.filter((currentRole) => currentRole !== role)
        : [...currentRoles, role],
    );
  };

  const isSubmitButtonEnabled = () => {
    if (isSubmitting) return false;
    if (selectedRoles.length === 0) return false;
    if (needsArea && selectedAreaId === null) return false;
    return true;
  };

  const handleSubmit = async () => {
    setIsSubmitting(true);
    const response = await UsersApi.changeUserRoles(
      user.id,
      selectedRoles,
      needsArea ? selectedAreaId : null,
    );
    setIsSubmitting(false);

    if (!response.success) return;

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
          void handleSubmit();
        }}
      >
        <button
          type="button"
          className={styles["close-button"]}
          onClick={onClose}
          aria-label={t("DashboardAdmin.changeRolesModal.close")}
        >
          ×
        </button>

        <div className={styles["modal-header"]}>
          <h2>{t("DashboardAdmin.changeRolesModal.title")}</h2>
          <p>{user.name}</p>
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
            <span>{t("DashboardAdmin.changeRolesModal.area")}</span>
            <select
              value={selectedAreaId ?? ""}
              onChange={(event) =>
                setSelectedAreaId(
                  event.target.value === "" ? null : Number(event.target.value),
                )
              }
            >
              <option value="">{t("DashboardAdmin.changeRolesModal.chooseArea")}</option>
              {availableAreas.map((area) => (
                <option key={area.id} value={area.id}>
                  {area.name}
                  {area.bossName ? ` · ${t("DashboardAdmin.changeRolesModal.supervisorLabel")}: ${area.bossName}` : ""}
                </option>
              ))}
            </select>
          </label>
        )}

        <div className={styles["primary-action"]}>
          <PrimaryButton
            text={isSubmitting ? t("DashboardAdmin.changeRolesModal.saving") : t("DashboardAdmin.changeRolesModal.save")}
            onClick={() => {
              if (!isSubmitting) void handleSubmit();
            }}
            enabled={isSubmitButtonEnabled()}
          />
        </div>
      </form>
    </div>
  );
}
