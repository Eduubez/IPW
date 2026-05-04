import { useEffect, useMemo, useState } from "react";
import PrimaryButton from "../../../Components/Buttons/PrimaryButton/PrimaryButton";
import TextBox from "../../../Components/Inputs/TextBox/TextBox";
import { useSnackbar } from "notistack";
import { ToastType } from "../../../Types/ToastType";
import { AreasApi, type AreaResponse } from "../../../Utility/Api/AreasApi";
import { UsersApi } from "../../../Utility/Api/UsersApi";
import styles from "./CreateUserModal.module.css";

const AVAILABLE_ROLES = [
  { key: "admin", label: "Admin" },
  { key: "triator", label: "Triador" },
  { key: "investigator", label: "Averiguador" },
  { key: "supervisor", label: "Supervisor" },
  { key: "manager", label: "Gestor" },
];

const AREA_ROLES = new Set(["investigator", "supervisor"]);

type CreateUserModalProps = {
  open: boolean;
  onClose: () => void;
  onSuccess: () => void;
};

export default function CreateUserModal({
  open,
  onClose,
  onSuccess,
}: CreateUserModalProps) {
  const { enqueueSnackbar } = useSnackbar();
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [selectedRoles, setSelectedRoles] = useState<string[]>([]);
  const [selectedAreaId, setSelectedAreaId] = useState<number | null>(null);
  const [areas, setAreas] = useState<AreaResponse[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const needsArea = useMemo(
    () => selectedRoles.some((role) => AREA_ROLES.has(role)),
    [selectedRoles],
  );

  const hasSupervisorRole = selectedRoles.includes("supervisor");

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

  const handleSubmit = async () => {
    if (name.trim().length === 0 || email.trim().length === 0) {
      enqueueSnackbar("Preenche o nome e o email.", {
        variant: ToastType.ERROR,
      });
      return;
    }

    if (password.length < 5) {
      enqueueSnackbar("A palavra passe deve ter pelo menos 5 caracteres.", {
        variant: ToastType.ERROR,
      });
      return;
    }

    if (selectedRoles.length === 0) {
      enqueueSnackbar("Escolhe pelo menos um papel.", {
        variant: ToastType.ERROR,
      });
      return;
    }

    if (needsArea && selectedAreaId === null) {
      enqueueSnackbar("Escolhe uma área para este utilizador.", {
        variant: ToastType.ERROR,
      });
      return;
    }

    setIsSubmitting(true);
    const response = await UsersApi.create({
      name: name.trim(),
      email: email.trim(),
      password,
      areaId: needsArea ? selectedAreaId : null,
      roles: selectedRoles,
    });
    setIsSubmitting(false);

    if (!response.success) {
      enqueueSnackbar(response.message, { variant: ToastType.ERROR });
      return;
    }

    enqueueSnackbar("Utilizador criado com sucesso.", {
      variant: ToastType.SUCCESS,
    });
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
          aria-label="Fechar"
        >
          ×
        </button>

        <div className={styles["modal-header"]}>
          <h2>Novo utilizador</h2>
          <p>Crie um novo utilizador</p>
        </div>

        <div className={styles["text-field"]}>
          <TextBox
            label="Nome"
            type="text"
            value={name}
            onChange={setName}
            mandatory={true}
          />
        </div>

        <div className={styles["text-field"]}>
          <TextBox
            label="Email"
            type="email"
            value={email}
            onChange={setEmail}
            mandatory={true}
          />
        </div>

        <div className={styles["text-field"]}>
          <TextBox
            label="Palavra passe"
            type="password"
            value={password}
            onChange={setPassword}
            mandatory={true}
          />
        </div>

        <div className={styles["roles-list"]}>
          {AVAILABLE_ROLES.map((role) => (
            <label key={role.key} className={styles["role-option"]}>
              <input
                type="checkbox"
                checked={selectedRoles.includes(role.key)}
                onChange={() => toggleRole(role.key)}
              />
              <span>{role.label}</span>
            </label>
          ))}
        </div>

        {needsArea && (
          <label className={styles["form-field"]}>
            <span>Área</span>
            <select
              value={selectedAreaId ?? ""}
              onChange={(event) =>
                setSelectedAreaId(
                  event.target.value === "" ? null : Number(event.target.value),
                )
              }
            >
              <option value="">Escolher área</option>
              {availableAreas.map((area) => (
                <option key={area.id} value={area.id}>
                  {area.name}
                  {area.bossName ? ` · Supervisor: ${area.bossName}` : ""}
                </option>
              ))}
            </select>
          </label>
        )}

        <div className={styles["primary-action"]}>
          <PrimaryButton
            text={isSubmitting ? "A criar..." : "Criar"}
            onClick={() => {
              if (!isSubmitting) void handleSubmit();
            }}
            enabled={!isSubmitting}
          />
        </div>
      </form>
    </div>
  );
}
