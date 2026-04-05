import { Modal } from "@mui/material";
import styles from "./primarymodal.module.css";
import PrimaryButton from "../Buttons/PrimaryButton/PrimaryButton";
import { Color } from "../../StyleGuide/colors";

export function PrimaryModal({
  open,
  onClose,
  onConfirm,
}: {
  open: boolean;
  onClose: () => void;
  onConfirm: () => void;
}) {
  return (
    <Modal
      open={open}
      onClose={onClose}
      sx={{
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
      }}>
      <div className={styles.box}>
        <h2>Are u sure ?</h2>
        <div className={styles.buttons}>
          <PrimaryButton
            text={"Confirm"}
            onClick={() => onConfirm()}
            enabled={true}
            style={{backgroundColor:Color.DarkBlue}}
            
          />
          <PrimaryButton text={"Cancel"} onClick={onClose} enabled={true} style={{backgroundColor:Color.DarkRed}} />
        </div>
      </div>
    </Modal>
  );
}
