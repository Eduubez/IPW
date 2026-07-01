import React, { useEffect, useId, useMemo, useState } from "react";
import { WithBackground } from "../Layouts/WithBackground/WithBackground";
import styles from "./datagrid.module.css";
import PrimaryButton from "../Buttons/PrimaryButton/PrimaryButton";
import { Icon } from "../Icons/Icons";
import { useTranslation } from "react-i18next";
import dataGridConfiguration from "./DataGridConfiguration";
import LoadingComponent from "../LoadingComponent/LoadingComponent";

type CellValue = string[] | string | number | React.ReactNode;
type DataGridRow = {
  onClick?: () => void;
  [key: string]: CellValue | (() => void) | undefined;
};
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
  totalCount?: number;
  currentPage?: number;
  onPageChange?: (page: number) => void;
};

const captitalizeFirstLetter = (text: string) =>
  text.charAt(0).toUpperCase() + text.slice(1);

export function DataGrid({
  title,
  actions,
  columns,
  rows,
  totalCount,
  currentPage = 1,
  onPageChange,
}: DataGridProps) {
  const gridId = useId();
  const { t } = useTranslation();
  const [search, setSearch] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");
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

    if (Array.isArray(value)) {
      return value.map((item) => getCellText(item as CellValue)).join(" ");
    }

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
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search);
    }, 300);
    return () => clearTimeout(timer);
  }, [search]);

  const normalizedSearch = debouncedSearch.toLowerCase();

  const filteredRows = useMemo(
    () =>
      rows.filter((row) =>
        columns.some((column) =>
          getCellText(row[column] as CellValue)
            .toLowerCase()
            .includes(normalizedSearch),
        ),
      ),
    [columns, normalizedSearch, rows],
  );

  const pageSettings = useMemo(() => {
    const totalPages = Math.max(1, Math.ceil((totalCount ?? rows.length) / dataGridConfiguration.itemPerPage));
    return { totalPages };
  }, [totalCount, rows.length]);

const paginatedRows = filteredRows;

  const handleNextPage = () => {
    if (currentPage < pageSettings.totalPages) {
      onPageChange?.(currentPage + 1);
    }
  };
  const handlePreviousPage = () => {
    if (currentPage > 1) {
      onPageChange?.(currentPage - 1);
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
            placeholder={t("DataGrid.searchPlaceholder")}
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
                          <span
                            key={`${gridId}-row-${rowIndex}-col-${columnIndex}`}
                            className={styles["cell"]}>
                            {Array.isArray(row[column])
                              ? (row[column] as CellValue[]).map((item, i) => (
                                  <span key={i}>{item}</span>
                                ))
                              : (row[column] as CellValue)}
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
                      <span>
                        {currentPage}/{pageSettings.totalPages}
                      </span>
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
            </div>
          </div>
        </WithBackground>
      </div>
    </>
  );
}
