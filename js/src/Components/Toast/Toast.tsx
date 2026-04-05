import { Snackbar } from "@mui/material";
import styles from "./toast.module.css";
import { ToastType } from "./ToastType";

export function Toast({
  open,
  onClose,
  message,
  position,
  type,
}: {
  type: string;
  open: boolean;
  message: string;
  position?: {
    vertical: "top" | "bottom";
    horizontal: "left" | "center" | "right";
  };
  onClose?: () => void;
}) {
  console.log(type);
  const typeOfToastMapper = {
    [ToastType.SUCCESS]: styles["toast-success"],
    [ToastType.ERROR]: styles["toast-error"],
    [ToastType.INFO]: styles["toast-info"],
    [ToastType.WARNING]: styles["toast-warning"],
  };

  const displayMessage = (text: string) => {
    return (
      <div className={styles["toast-message"]}>
        <span
          className={`material-symbols-outlined ${typeOfToastMapper[type]}`}>
          {type}
        </span>
        <span>{text}</span>
      </div>
    );
  };

  return (
    <Snackbar
      open={open}
      message={displayMessage(message)}
      autoHideDuration={2000}
      anchorOrigin={
        position ? position : { vertical: "top", horizontal: "right" }
      }
      onClose={onClose}
    />
  );
}
