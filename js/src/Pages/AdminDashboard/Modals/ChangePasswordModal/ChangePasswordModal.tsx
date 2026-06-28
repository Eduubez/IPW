import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import PrimaryButton from "../../../../Components/Buttons/PrimaryButton/PrimaryButton";
import TextBox from "../../../../Components/Inputs/TextBox/TextBox";
import { UsersApi, type UserResponse } from "../../../../Utility/Api/UsersApi";
import styles from "./ChangePasswordModal.module.css";

type ChangePasswordModalProps = {
  open: boolean;
  user: UserResponse | null;
  onClose: () => void;
  onSuccess: () => void;
  triggerRenderFn: () => void;
};

export default function ChangePasswordModal({
  open,
  user,
  onClose,
  onSuccess,
  triggerRenderFn,
}: ChangePasswordModalProps) {
  const { t } = useTranslation();
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

  const isSubmitButtonEnabled = () => {
    if (isSubmitting) return false;
    if (newPassword.length < 5) return false;
    if (newPassword !== confirmPassword) return false;
    return true;
  };

  const handleSubmit = async () => {
    setIsSubmitting(true);
    const response = await UsersApi.changeUserPassword(user.id, newPassword);
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
        >
          <span className="material-icons">close</span>

        </button>

        <div className={styles["modal-header"]}>
          <h2>{t("DashboardAdmin.changePasswordModal.title")}</h2>
          <p>{user.name}</p>
        </div>

        <div className={styles["text-field"]}>
          <TextBox
            label={t("DashboardAdmin.changePasswordModal.newPassword")}
            type="password"
            value={newPassword}
            onChange={setNewPassword}
            mandatory={true}
          />
        </div>

        <div className={styles["text-field"]}>
          <TextBox
            label={t("DashboardAdmin.changePasswordModal.confirmPassword")}
            type="password"
            value={confirmPassword}
            onChange={setConfirmPassword}
            mandatory={true}
          />
        </div>

        <div className={styles["primary-action"]}>
          <PrimaryButton
            text={isSubmitting ? t("DashboardAdmin.changePasswordModal.saving") : t("DashboardAdmin.changePasswordModal.save")}
            onClick={() => {
              void handleSubmit();
            }}
            enabled={isSubmitButtonEnabled()}
          />
        </div>
      </form>
    </div>
  );
}
