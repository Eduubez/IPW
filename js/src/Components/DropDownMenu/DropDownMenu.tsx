import { useId, useState } from "react";
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import type { SelectChangeEvent } from '@mui/material/Select';
import InputLabel from '@mui/material/InputLabel';
import styles from './dropdownmenu.module.css';
  


export function DropDownMenu({label, options,onSelect,mandatory}: {label: string, options: string[], onSelect: (value: any) => void, mandatory?: boolean}) {
    const [selectedOption, setSelectedOption] = useState('');
    const formId = useId();

    const handleChange = (event: SelectChangeEvent) => {
      setSelectedOption(event.target.value);
      onSelect(event.target.value);
    };
    return (
      <>
      <FormControl className={styles.form}>
        <InputLabel id={`${formId}-label`}><span>{label}  {mandatory && <span style={{ color: 'red' }}> *</span>}</span></InputLabel>
        <Select
          labelId={`${formId}-label`}
          id={formId}
          value={selectedOption}
          label={label}
          onChange={handleChange}
          MenuProps={{
            slotProps: {
              paper: {
                className: styles.menuPaper,
              },
            },
          }}
        >
          {options.map((option, index) => (
            <MenuItem key={index} className={styles.item} value={option}>
              <span>{option}</span>
            </MenuItem>
          ))}
        </Select>
      </FormControl>
      </>
    );
}