import { Modal } from "@mui/material";
import styles from "./primarymodal.module.css";
import PrimaryButton from "../Buttons/PrimaryButton/PrimaryButton";
import { Color } from "../../StyleGuide/colors";

export function PrimaryModal({
  open,
  onClose,
  header,
  body,
  footer,
}: {
  open: boolean;
  onClose: () => void;
  header?: React.ReactNode;
  body?: React.ReactNode;
  footer?: React.ReactNode;
}) {
  return (
    <>
      <Modal
        open={open}
        onClose={onClose}
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}>
        <div className={styles.box}>
          {header && <h2>{header}</h2>}
          {body && <div style={{display:"flex", height: "100%", width: "100%"}}>{body}</div>}
          {footer && <div>{footer}</div>}
          <div className={styles["modal-actions"]}>
            <PrimaryButton
              text={"Cancel"}
              onClick={onClose}
              enabled={true}
              style={{ backgroundColor: Color.DarkRed }}
            />
          </div>
        </div>
      </Modal>
    </>
  );
}
