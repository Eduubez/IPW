import { useEffect, useState } from "react";
import { useSnackbar } from "notistack";
import { ToastType } from "../../../Types/ToastType";
import { UsersApi, type UserResponse } from "../../../Utility/Api/UsersApi";
import styles from "./ChangePasswordModal.module.css";

type ChangePasswordModalProps = {
  open: boolean;
  user: UserResponse | null;
  onClose: () => void;
  onSuccess: () => void;
};

export default function ChangePasswordModal({
  open,
  user,
  onClose,
  onSuccess,
}: ChangePasswordModalProps) {
  const { enqueueSnackbar } = useSnackbar();
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (open) {
      setNewPassword("");
      setConfirmPassword("");
    }
  }, [open]);

  if (!open || user === null) return null;

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (newPassword.length < 5) {
      enqueueSnackbar("A palavra passe deve ter pelo menos 5 caracteres.", {
        variant: ToastType.ERROR,
      });
      return;
    }

    if (newPassword !== confirmPassword) {
      enqueueSnackbar("As palavras passe não coincidem.", {
        variant: ToastType.ERROR,
      });
      return;
    }

    setIsSubmitting(true);
    const response = await UsersApi.changeUserPassword(user.id, newPassword);
    setIsSubmitting(false);

    if (!response.success) {
      enqueueSnackbar(response.message, { variant: ToastType.ERROR });
      return;
    }

    enqueueSnackbar("Palavra passe alterada com sucesso.", {
      variant: ToastType.SUCCESS,
    });
    onSuccess();
    onClose();
  };

  return (
    <div className={styles["modal-backdrop"]} role="presentation">
      <form className={styles["modal-card"]} onSubmit={handleSubmit}>
        <button
          type="button"
          className={styles["close-button"]}
          onClick={onClose}
          aria-label="Fechar"
        >
          ×
        </button>

        <div className={styles["modal-header"]}>
          <h2>Trocar palavra passe</h2>
          <p>{user.name}</p>
        </div>

        <label className={styles["form-field"]}>
          <span>Nova palavra passe</span>
          <input
            type="password"
            value={newPassword}
            onChange={(event) => setNewPassword(event.target.value)}
            placeholder="Palavra passe"
          />
        </label>

        <label className={styles["form-field"]}>
          <span>Repita a palavra passe</span>
          <input
            type="password"
            value={confirmPassword}
            onChange={(event) => setConfirmPassword(event.target.value)}
            placeholder="Palavra passe"
          />
        </label>

        <button
          type="submit"
          className={styles["primary-action"]}
          disabled={isSubmitting}
        >
          {isSubmitting ? "A trocar..." : "Trocar"}
        </button>
      </form>
    </div>
  );
}
