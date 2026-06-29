import { useCallback, useEffect, useMemo, useState } from "react";
import PrimaryButton from "../../Components/Buttons/PrimaryButton/PrimaryButton";
import { StatCard as SummaryStatCard } from "../../Components/Cards/StatCard/StatCard";
import { Icon } from "../../Components/Icons/Icons";
import { Header } from "../../Components/Layouts/Header/Header";
import { UsersApi, type UserResponse } from "../../Utility/Api/UsersApi";
import ChangePasswordModal from "./Modals/ChangePasswordModal/ChangePasswordModal";
import ChangeRolesModal from "./Modals/ChangeRolesModal/ChangeRolesModal";
import CreateUserModal from "./Modals/CreateUserModal/CreateUserModal";
import styles from "./AdminDashboard.module.css";
import { DataGrid } from "../../Components/DataGrid/DataGrid";
import { PrimaryBadge } from "../../Components/Badge/PrimaryBadge/PrimaryBadge";
import { useTranslation } from "react-i18next";
import { getRoleStyle } from "../../Utility/Helpers/RoleHelpers";
import { PrimaryModal } from "../../Components/Modal/PrimaryModal";
import { ROLE_KEYS } from "../../MockData/MockRoles";
import { Color } from "../../StyleGuide/colors";
import { StateBadge } from "../../Components/Badge/StateBadge/StateBadge";

export default function AdminDashboard() {
  const { t } = useTranslation();
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [renderCountKey, setRenderCountKey] = useState(0);
  const triggerRenderFn = () => setRenderCountKey((prev) => prev + 1);
  const loadUsers = useCallback(async () => {
    try {
      setIsLoading(true);

      const response = await UsersApi.getAll(0, 100);

      if (response.success) {
        setUsers(response.data.results);
      }
    } finally {
      setIsLoading(false);
    }
  }, []);
  const [selectedUser, setSelectedUser] = useState<UserResponse | null>(null);
  const [isChangePasswordOpen, setIsChangePasswordOpen] = useState(false);
  const [isChangeRolesOpen, setIsChangeRolesOpen] = useState(false);
  const [isCreateUserOpen, setIsCreateUserOpen] = useState(false);

  useEffect(() => {
    loadUsers();
  }, [loadUsers, renderCountKey]);

  const stats = useMemo(() => {
    return {
      total: users.length,
      triators: users.filter((user) => user.roles.includes(ROLE_KEYS.TRIATOR))
        .length,
      investigators: users.filter((user) =>
        user.roles.includes(ROLE_KEYS.INVESTIGATOR),
      ).length,
      supervisors: users.filter((user) =>
        user.roles.includes(ROLE_KEYS.SUPERVISOR),
      ).length,
      managers: users.filter((user) => user.roles.includes(ROLE_KEYS.MANAGER))
        .length,
    };
  }, [users]);

  const statCards = [
    {
      icon: { name: Icon.Group },
      text: t("DashboardAdmin.stats.total"),
      value: stats.total,
      loading: isLoading,
    },
    {
      icon: { name: Icon.Visibility },
      text: t("DashboardAdmin.stats.triators"),
      value: stats.triators,
      loading: isLoading,
    },
    {
      icon: { name: Icon.Search },
      text: t("DashboardAdmin.stats.investigators"),
      value: stats.investigators,
      loading: isLoading,
    },
    {
      icon: { name: Icon.Shield },
      text: t("DashboardAdmin.stats.supervisors"),
      value: stats.supervisors,
      loading: isLoading,
    },
    {
      icon: { name: Icon.Crown },
      text: t("DashboardAdmin.stats.managers"),
      value: stats.managers,
      loading: isLoading,
    },
  ];
  const gridColumns = ["name", "roles", "email", "area", "status"];

  const cleanRows = useMemo(() => {
    return users.map((user) => ({
      ...user,
      area: t(`Areas.${user.area}`, { defaultValue: "" }),
      onClick: () => {
        setSelectedUser(user);
        setIsModalOpen(true);
      },
      roles: user.roles.map((role) => {
        return (
          <PrimaryBadge
            key={role}
            text={t(`Roles.${role}`)}
            style={getRoleStyle(role)}
          />
        );
      }),
      status: user.isActive ? (
        <PrimaryBadge text={t("DashboardAdmin.gridStatus.active")} style={{backgroundColor: Color.GreenPrimary}}/>
      ) : (
        <PrimaryBadge text={t("DashboardAdmin.gridStatus.inactive")} style={{backgroundColor: Color.DarkRed}}/>
      ),
    }));
  }, [users, t]);

  const handleDeactivateUser = async (userId: number, isActive: boolean) => {
    await UsersApi.changeUserStatus(userId, !isActive);
    triggerRenderFn();
  };

  const modalContent = () => {
    return (
      <div className={styles["modal-content"]}>
        <PrimaryButton
          text={t("DashboardAdmin.userModal.changePassword")}
          onClick={() => {
            setIsChangePasswordOpen(true);
            setIsModalOpen(false);
          }}
          enabled={true}
        />
        <PrimaryButton
          text={t("DashboardAdmin.userModal.changeRoles")}
          onClick={() => {
            setIsChangeRolesOpen(true);
            setIsModalOpen(false);
          }}
          enabled={true}
        />
        <PrimaryButton
          text={
            selectedUser!.isActive
              ? t("DashboardAdmin.userModal.deactivateUser")
              : t("DashboardAdmin.userModal.activateUser")
          }
          onClick={() => {
            handleDeactivateUser(Number(selectedUser!.id), selectedUser!.isActive);
            setIsModalOpen(false);
          }}
          enabled={true}
        />
      </div>
    );
  };
  const gridActions = [
    {
      label: t("DashboardAdmin.createUserButton"),
      onClick: () => setIsCreateUserOpen(true),
    },
  ];

  return (
    <div className={styles["admin-dashboard-container"]}>
      <div className={styles["page-header"]}>
        <Header
          title={t("DashboardAdmin.title")}
          description={t("DashboardAdmin.description")}
        />
      </div>

      <div className={styles["stats-grid"]}>
        {statCards.map((card, index) => (
          <SummaryStatCard
            key={index}
            icon={card.icon}
            text={card.text}
            value={card.value}
            loading={card.loading}
          />
        ))}
      </div>
      <DataGrid
        title={t("DashboardAdmin.gridTitle")}
        columns={gridColumns}
        rows={cleanRows}
        actions={gridActions}
      />
      {isModalOpen && (
        <PrimaryModal
          open={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          header={t("DashboardAdmin.userModal.title")}
          body={modalContent()}
        />
      )}
      {isChangePasswordOpen && selectedUser && (
        <ChangePasswordModal
          open={isChangePasswordOpen}
          user={selectedUser}
          onClose={() => setIsChangePasswordOpen(false)}
          onSuccess={() => setIsChangePasswordOpen(false)}
          triggerRenderFn={triggerRenderFn}
        />
      )}
      {isChangeRolesOpen && selectedUser && (
        <ChangeRolesModal
          open={isChangeRolesOpen}
          user={selectedUser}
          onClose={() => setIsChangeRolesOpen(false)}
          onSuccess={() => setIsChangeRolesOpen(false)}
          triggerRenderFn={triggerRenderFn}
        />
      )}
      {isCreateUserOpen && (
        <CreateUserModal
          open={isCreateUserOpen}
          onClose={() => setIsCreateUserOpen(false)}
          onSuccess={() => setIsCreateUserOpen(false)}
          triggerRenderFn={triggerRenderFn}
        />
      )}
    </div>
  );
}
