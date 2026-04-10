import { FilePond, registerPlugin } from 'react-filepond';
import 'filepond/dist/filepond.min.css';

const serverOptions= {
    
}

export function DragAndDrop() {
    return (
        <div style={{ width: '100%' }}>
            <FilePond
                allowMultiple={true}
                maxFiles={3}
                server="/api"
                name="files"
            />
        </div>
    );
}