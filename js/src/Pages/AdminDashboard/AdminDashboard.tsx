import { useCallback, useEffect, useMemo, useState } from "react";
import PrimaryButton from "../../Components/Buttons/PrimaryButton/PrimaryButton";
import { StatCard as SummaryStatCard } from "../../Components/Cards/StatCard/StatCard";
import { Icon } from "../../Config/Icons";
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
import { ROLE_KEYS } from "../../Config/RolesConfig";
import { Color } from "../../StyleGuide/colors";
import dataGridConfiguration from "../../Components/DataGrid/DataGridConfiguration";

export default function AdminDashboard() {
  const { t } = useTranslation();
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalCount, setTotalCount] = useState(0);
  const [selectedUser, setSelectedUser] = useState<UserResponse | null>(null);
  const [isChangePasswordOpen, setIsChangePasswordOpen] = useState(false);
  const [isChangeRolesOpen, setIsChangeRolesOpen] = useState(false);
  const [isCreateUserOpen, setIsCreateUserOpen] = useState(false);
  const [filters, setFilters] = useState({ name: undefined as string | undefined, areaId: undefined as string | undefined, isActive: undefined as string | undefined });

  const loadUsers = async (
    page: number,
    currentFilters = { name: undefined as string | undefined, areaId: undefined as string | undefined, isActive: undefined as string | undefined },
  ) => {
    const offset = (page - 1) * dataGridConfiguration.itemPerPage;
    const isActiveParam =
      currentFilters.isActive === "true"
        ? true
        : currentFilters.isActive === "false"
          ? false
          : undefined;
    const response = await UsersApi.getAll(
      offset,
      dataGridConfiguration.itemPerPage,
      currentFilters.name || undefined,
      Number(currentFilters.areaId) || undefined,
      isActiveParam,
    );
    if (response.success) {
      setUsers(response.data.results);
      setTotalCount(response.data.totalCount);
    }
  };

  const triggerRenderFn = useCallback(() => {
    if (currentPage === 1) {
      loadUsers(1, filters);
    } else {
      setCurrentPage(1);
    }
  }, [currentPage]);

  useEffect(() => {
    loadUsers(currentPage, filters);
  }, [currentPage]);

  const handleSearch = (term: string) => {
    const updated = { ...filters, name: term };
    setFilters(updated);
    setCurrentPage(1);
    loadUsers(1, updated);
  };

  const handleFilterChange = (field: string, value: string) => {
    const updated = { ...filters, [field]: value };
    setFilters(updated);
    setCurrentPage(1);
    loadUsers(1, updated);
  };

  const stats = useMemo(() => {
    return {
      total: totalCount,
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
  }, [users, totalCount]);

  const statCards = [
    {
      icon: { name: Icon.Group },
      text: t("DashboardAdmin.stats.total"),
      value: stats.total,
      loading: false,
    },
    {
      icon: { name: Icon.Visibility },
      text: t("DashboardAdmin.stats.triators"),
      value: stats.triators,
      loading: false,
    },
    {
      icon: { name: Icon.Search },
      text: t("DashboardAdmin.stats.investigators"),
      value: stats.investigators,
      loading: false,
    },
    {
      icon: { name: Icon.Shield },
      text: t("DashboardAdmin.stats.supervisors"),
      value: stats.supervisors,
      loading: false,
    },
    {
      icon: { name: Icon.Crown },
      text: t("DashboardAdmin.stats.managers"),
      value: stats.managers,
      loading: false,
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
        <PrimaryBadge
          text={t("DashboardAdmin.gridStatus.active")}
          style={{ backgroundColor: Color.GreenPrimary }}
        />
      ) : (
        <PrimaryBadge
          text={t("DashboardAdmin.gridStatus.inactive")}
          style={{ backgroundColor: Color.DarkRed }}
        />
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
            handleDeactivateUser(
              Number(selectedUser!.id),
              selectedUser!.isActive,
            );
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
        totalCount={totalCount}
        currentPage={currentPage}
        onPageChange={setCurrentPage}
        onSearch={handleSearch}
        filterDropdowns={[
          {
            label: t("Label.state"),
            field: "isActive",
            options: [
              { id: "", name: t("Label.all") },
              { id: "true", name: t("DashboardAdmin.gridStatus.active") },
              { id: "false", name: t("DashboardAdmin.gridStatus.inactive") },
            ],
            onSelect: (value) => handleFilterChange("isActive", value),
          },
          {
            label: t("Label.area"),
            field: "areaId",
            options: [
              { id: "", name: t("Label.all") },
              { id: "1", name: t("Areas.1") },
              { id: "2", name: t("Areas.2") },
              { id: "3", name: t("Areas.3") },
              { id: "4", name: t("Areas.4") },
            ],
            onSelect: (value) => handleFilterChange("areaId", value),
          },
      ]}
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
