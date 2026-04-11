import { FilePond } from "react-filepond";
import "filepond/dist/filepond.min.css";

export function DragAndDrop() {
  return (
    <div style={{ width: "100%" }}>
      <FilePond allowMultiple={true} maxFiles={3} server="/api" name="files" />
    </div>
  );
}
