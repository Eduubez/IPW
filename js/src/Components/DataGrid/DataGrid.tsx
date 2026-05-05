import React, { useEffect, useId, useMemo, useState } from "react";
import { WithBackground } from "../Layouts/WithBackground/WithBackground";
import styles from "./datagrid.module.css";
import PrimaryButton from "../Buttons/PrimaryButton/PrimaryButton";
import { Icon } from "../Icons/Icons";
import { useTranslation } from "react-i18next";
import dataGridConfiguration from "./DataGridConfiguration";
import LoadingComponent from "../LoadingComponent/LoadingComponent";

type CellValue = string | number | React.ReactNode;
type DataGridRow = Record<string, CellValue> & { onClick?: () => void };

type DataGridAction = {
  label: string;
  onClick: () => void;
  enabled?: boolean;
};

type DataGridProps = {
  title?: string;
  actions?: DataGridAction[];
  columns: string[];
  rows: DataGridRow[];
  searchTerm?: string;
  loading?: boolean;
};

const captitalizeFirstLetter = (text: string) =>
  text.charAt(0).toUpperCase() + text.slice(1);



export function DataGrid({
  title,
  actions,
  columns,
  rows,
  loading,
}: DataGridProps) {
  const gridId = useId();
  const { t } = useTranslation();
  const [search, setSearch] = useState("");
  const [currentPage, setCurrentPage] = useState(1);

  const translatedColumns = useMemo(
    () =>
      columns.map((column) =>
        t(`GridColumnsProps.${column}`, {
          defaultValue: captitalizeFirstLetter(column),
        }),
      ),
    [columns, t],
  );

    const getCellText = (value: CellValue): string => {
    const badgeSearchFields = dataGridConfiguration.jsxSearchableFields;

    if (React.isValidElement(value)) {
      const props = (value.props ?? {}) as Record<string, unknown>;

      const rawFieldValues = badgeSearchFields
        .map((field) => props[field])
        .filter(
          (fieldValue): fieldValue is string | number =>
            typeof fieldValue === "string" || typeof fieldValue === "number",
        )
        .map(String);

      const translatedValues = badgeSearchFields
        .map((field) =>
          props[field]
            ? t(`${captitalizeFirstLetter(field)}.${props[field]}`)
            : "",
        )
        .filter(Boolean);

      return [...translatedValues, ...rawFieldValues].join(" ");
    }

    return String(value);
  };
  const normalizedSearch = search.toLowerCase();

  const filteredRows = useMemo(
    () =>
      rows.filter((row) =>
        columns.some((column) =>
          getCellText(row[column]).toLowerCase().includes(normalizedSearch),
        ),
      ),
    [columns, normalizedSearch, rows],
  );

  const pageSettings = useMemo(() => {
    const totalPages = Math.ceil(
      filteredRows.length / dataGridConfiguration.itemPerPage,
    );
    return {
      totalPages,
      pageSteps: Array.from({ length: totalPages }, (_, i) => i + 1),
    };
  }, [filteredRows.length]);

  useEffect(() => {
    setCurrentPage(1);
  }, [normalizedSearch]);

  const paginatedRows = useMemo(() => {
    const startIndex = (currentPage - 1) * dataGridConfiguration.itemPerPage;
    return filteredRows.slice(
      startIndex,
      startIndex + dataGridConfiguration.itemPerPage,
    );
  }, [currentPage, filteredRows]);

  const handleNextPage = () => {
    if (currentPage < pageSettings.totalPages) {
      setCurrentPage(currentPage + 1);
    }
  };
  const handlePreviousPage = () => {
    if (currentPage > 1) {
      setCurrentPage(currentPage - 1);
    }
  };
  return (
    <>
      <div className={styles["container"]}>
        <div className={styles["search-bar"]}>
          <div className={styles["icon-wrapper"]}>
            <span className="material-symbols-outlined">{Icon.Search}</span>
          </div>
          <input
            type="text"
            placeholder="Search..."
            className={styles["search-input"]}
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <WithBackground>
          <div className={styles["data-grid"]}>
            {(title || (actions && actions.length > 0)) && (
              <div className={styles["grid-header"]}>
                <div className={styles["grid-title"]}>
                  {title && <span>{title}</span>}
                </div>
                <div className={styles["grid-actions"]}>
                  {actions?.map((action, index) => (
                    <PrimaryButton
                      key={index}
                      onClick={action.onClick}
                      enabled={action.enabled ?? true}
                      text={action.label}
                    />
                  ))}
                </div>
              </div>
            )}
            <div className={styles["grid-content"]}>
              {loading ? (
                <LoadingComponent />
              ) : (
                <>
                  <div className={styles["columns"]}>
                    {translatedColumns.map((column) => (
                      <div key={column} className={styles["cell"]}>
                        <span>{column}</span>
                      </div>
                    ))}
                  </div>
                  <div className={styles["rows"]}>
                    {paginatedRows.map((row, rowIndex) => (
                      <div
                        key={`${gridId}-row-${rowIndex}`}
                        className={styles["row"]}
                        onClick={row.onClick}>
                        {columns.map((column, columnIndex) => (
                          <span key={`${gridId}-row-${rowIndex}-col-${columnIndex}`} className={styles["cell"]}>
                            {row[column]}
                          </span>
                        ))}
                      </div>
                    ))}
                  </div>
                  <div className={styles["pagination"]}>
                    <button
                      onClick={handlePreviousPage}
                      className={styles["pagination-button"]}>
                      <span className="material-symbols-outlined">
                        {Icon.ArrowBack}
                      </span>
                    </button>
                    <div className={styles["pagination-info"]}>
                      <span>{currentPage}/{pageSettings.totalPages}</span>
                    </div>
                    <button
                      onClick={handleNextPage}
                      className={styles["pagination-button"]}>
                      <span className="material-symbols-outlined">
                        {Icon.ArrowForward}
                      </span>
                    </button>
                  </div>
                </>
              )}
            </div>
          </div>
        </WithBackground>
      </div>
    </>
  );
}
