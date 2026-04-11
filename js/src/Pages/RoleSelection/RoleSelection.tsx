import { useMemo } from "react";
import styles from "./roleselection.module.css";
import RoleCard from "../../Components/Cards/RoleCard/RoleCard";
import { useTranslation } from "react-i18next";
import { ROLES } from "../../MockData/MockRoles";
import { useNavigate } from "react-router";
import { ToastType } from "../../Types/ToastType";
import { useSnackbar } from "notistack";

export default function RoleSelection() {
  const { t } = useTranslation();
  const { enqueueSnackbar } = useSnackbar();
  const navigate = useNavigate();

  const userRoles = useMemo(() => {
    const roles = localStorage.getItem("roles");
    return roles ? JSON.parse(roles) : [];
  }, []);

  const filteredRoles = ROLES.filter((role) => userRoles.includes(role.key));
  const translatedRoles = useMemo(() => {
    return filteredRoles.map((role) => ({
      ...role,
      title: t(`RoleSelection.${role.key}`),
      permissions: role.permissions.map((perm) => t(`Permissions.${perm}`)),
    }));
  }, [filteredRoles, t]);

  const roleSelection = useMemo(() => {
    return t("RoleSelection", { returnObjects: true }) as {
      title: string;
      description: string;
    }; // needed cuz ts...
  }, [t]);

  const handleSelectRole = (roleKey: string) => {
    localStorage.setItem("selectedRole", roleKey);
    enqueueSnackbar(t("RoleSelection.roleSelected"), {
      variant: ToastType.SUCCESS,
    });

    navigate("/dashboard");
  };

  return (
    <div className={styles["role-selection-container"]}>
      <div className={styles["role-section-header"]}>
        <h1 className="title-large">{roleSelection.title}</h1>
        <p className="subtitle-medium">{roleSelection.description}</p>
      </div>
      <div className={styles["role-selection-role-container"]}>
        {translatedRoles.map((role) => (
          <RoleCard
            icon={role.icon}
            key={role.key}
            title={role.title}
            permissions={role.permissions}
            style={role.style}
            onClick={() => handleSelectRole(role.key)}
          />
        ))}
      </div>
    </div>
  );
}
