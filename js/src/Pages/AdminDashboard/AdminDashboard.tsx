import { useCallback, useEffect, useMemo, useState } from "react";
import PrimaryButton from "../../Components/Buttons/PrimaryButton/PrimaryButton";
import { StatCard as SummaryStatCard } from "../../Components/Cards/StatCard/StatCard";
import { Icon } from "../../Components/Icons/Icons";
import { Header } from "../../Components/Layouts/Header/Header";
import { UsersApi, type UserResponse } from "../../Utility/Api/UsersApi";
import ChangePasswordModal from "./Modals/ChangePasswordModal";
import ChangeRolesModal from "./Modals/ChangeRolesModal";
import CreateUserModal from "./Modals/CreateUserModal";
import styles from "./AdminDashboard.module.css";

const ROLE_LABELS: Record<string, string> = {
  admin: "Admin",
  triator: "Triador",
  investigator: "Averiguador",
  supervisor: "Supervisor",
  manager: "Gestor",
};

const USERS_PER_PAGE = 7;

const STAT_ICONS = {
  total: Icon.Group,
  triators: Icon.Visibility,
  investigators: Icon.Search,
  supervisors: Icon.Shield,
  managers: Icon.Crown,
};

export default function AdminDashboard() {
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");
  const [selectedRole, setSelectedRole] = useState<string | null>(null);
  const [openMenuUserId, setOpenMenuUserId] = useState<number | null>(null);
  const [isCreateUserModalOpen, setIsCreateUserModalOpen] = useState(false);
  const [passwordUser, setPasswordUser] = useState<UserResponse | null>(null);
  const [rolesUser, setRolesUser] = useState<UserResponse | null>(null);
  const [currentPage, setCurrentPage] = useState(1);

  const loadUsers = useCallback(async () => {
    setIsLoading(true);

    const response = await UsersApi.getAll(0, 100);

    if (response.success) {
      setUsers(response.data);
    }

    setIsLoading(false);
  }, []);

  useEffect(() => {
    loadUsers();
  }, [loadUsers]);

  useEffect(() => {
    const timeoutId = window.setTimeout(() => {
      setDebouncedSearch(search);
    }, 300);

    return () => window.clearTimeout(timeoutId);
  }, [search]);

  const stats = useMemo(() => {
    return {
      total: users.length,
      triators: users.filter((user) => user.roles.includes("triator")).length,
      investigators: users.filter((user) =>
        user.roles.includes("investigator"),
      ).length,
      supervisors: users.filter((user) =>
        user.roles.includes("supervisor"),
      ).length,
      managers: users.filter((user) => user.roles.includes("manager")).length,
    };
  }, [users]);

  const filteredUsers = useMemo(() => {
    const normalizedSearch = debouncedSearch.trim().toLowerCase();

    return users.filter((user) => {
      const matchesRole =
        selectedRole === null || user.roles.includes(selectedRole);

      const matchesSearch =
        normalizedSearch.length === 0 ||
        user.name.toLowerCase().includes(normalizedSearch) ||
        user.email.toLowerCase().includes(normalizedSearch) ||
        (user.area?.toLowerCase().includes(normalizedSearch) ?? false) ||
        user.roles.some((role) =>
          role.toLowerCase().includes(normalizedSearch),
        );

      return matchesRole && matchesSearch;
    });
  }, [users, debouncedSearch, selectedRole]);

  useEffect(() => {
    setCurrentPage(1);
  }, [debouncedSearch, selectedRole]);

  const totalPages = Math.max(
    1,
    Math.ceil(filteredUsers.length / USERS_PER_PAGE),
  );

  useEffect(() => {
    setCurrentPage((page) => Math.min(page, totalPages));
  }, [totalPages]);

  const paginatedUsers = useMemo(() => {
    const startIndex = (currentPage - 1) * USERS_PER_PAGE;
    return filteredUsers.slice(startIndex, startIndex + USERS_PER_PAGE);
  }, [filteredUsers, currentPage]);

  const firstVisibleUser =
    filteredUsers.length === 0 ? 0 : (currentPage - 1) * USERS_PER_PAGE + 1;
  const lastVisibleUser = Math.min(
    currentPage * USERS_PER_PAGE,
    filteredUsers.length,
  );

  const toggleUserMenu = (userId: number) => {
    setOpenMenuUserId((currentUserId) =>
      currentUserId === userId ? null : userId,
    );
  };

  const handleChangePassword = (user: UserResponse) => {
    setOpenMenuUserId(null);
    setPasswordUser(user);
  };

  const handleChangeRoles = (user: UserResponse) => {
    setOpenMenuUserId(null);
    setRolesUser(user);
  };

  return (
    <div className={styles["admin-dashboard-container"]}>
      <div className={styles["page-header"]}>
        <Header
          title="Gestão de utilizadores"
          description="Controla todos os utilizadores"
        />
      </div>

      <section className={styles["stats-grid"]}>
        <SummaryStatCard
          icon={{ name: STAT_ICONS.total }}
          text="Nº Total de utilizadores"
          value={stats.total}
          loading={isLoading}
        />
        <SummaryStatCard
          icon={{ name: STAT_ICONS.triators }}
          text="Nº de Triadores"
          value={stats.triators}
          loading={isLoading}
        />
        <SummaryStatCard
          icon={{ name: STAT_ICONS.investigators }}
          text="Nº de Averiguadores"
          value={stats.investigators}
          loading={isLoading}
        />
        <SummaryStatCard
          icon={{ name: STAT_ICONS.supervisors }}
          text="Nº de Supervisores"
          value={stats.supervisors}
          loading={isLoading}
        />
        <SummaryStatCard
          icon={{ name: STAT_ICONS.managers }}
          text="Nº de Gestores"
          value={stats.managers}
          loading={isLoading}
        />
      </section>

      <section className={styles["filters-bar"]}>
        <div className={styles["search-input-wrapper"]}>
          <span className="material-symbols-outlined">search</span>
          <input
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Procurar por nome, papéis, área..."
          />
        </div>

        <div className={styles["role-filters"]}>
          <button
            className={`${styles["filter-chip"]} ${
              selectedRole === null ? styles["active-filter-chip"] : ""
            }`}
            onClick={() => setSelectedRole(null)}
          >
            Todos
          </button>

          {Object.entries(ROLE_LABELS).map(([role, label]) => (
            <button
              key={role}
              className={`${styles["filter-chip"]} ${
                selectedRole === role ? styles["active-filter-chip"] : ""
              }`}
              onClick={() => setSelectedRole(role)}
            >
              {label}
            </button>
          ))}
        </div>
      </section>

      <section className={styles["users-panel"]}>
        <div className={styles["users-panel-header"]}>
          <h2>Utilizadores</h2>
          <div className={styles["primary-action"]}>
            <PrimaryButton
              text="Criar novo utilizador"
              onClick={() => setIsCreateUserModalOpen(true)}
              enabled={true}
            />
          </div>
        </div>

        {isLoading ? (
          <p>A carregar utilizadores...</p>
        ) : (
          <table className={styles["users-table"]}>
            <thead>
              <tr>
                <th>Utilizador</th>
                <th>Papéis</th>
                <th>Área</th>
                <th>Estado</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {paginatedUsers.length === 0 && (
                <tr>
                  <td colSpan={5} className={styles["empty-state"]}>
                    Nenhum utilizador encontrado
                  </td>
                </tr>
              )}

              {paginatedUsers.map((user, index) => (
                <tr
                  key={`${user.id}-${selectedRole ?? "all"}-${debouncedSearch}-${currentPage}`}
                  className={styles["animated-row"]}
                  style={{ animationDelay: `${Math.min(index * 35, 180)}ms` }}
                >
                  <td>
                    <div className={styles["user-cell"]}>
                      <div className={styles["avatar"]}>
                        {getInitials(user.name)}
                      </div>
                      <div>
                        <strong>{user.name}</strong>
                        <span>ID: {user.id}</span>
                      </div>
                    </div>
                  </td>
                  <td>
                    <div className={styles["roles"]}>
                      {user.roles.length === 0 ? (
                        <span className={styles["empty-role"]}>Sem papéis</span>
                      ) : (
                        user.roles.map((role) => (
                          <span key={role} className={styles["role-badge"]}>
                            {ROLE_LABELS[role] ?? role}
                          </span>
                        ))
                      )}
                    </div>
                  </td>
                  <td>{user.area ?? "-"}</td>
                  <td>{user.isActive ? "Ativo" : "Inativo"}</td>
                  <td className={styles["actions-cell"]}>
                    <button
                      className={styles["row-action"]}
                      aria-label={`Abrir ações de ${user.name}`}
                      onClick={() => toggleUserMenu(user.id)}
                    >
                      ⋮
                    </button>

                    {openMenuUserId === user.id && (
                      <div className={styles["row-menu"]}>
                        <button onClick={() => handleChangePassword(user)}>
                          Trocar password
                        </button>
                        <button onClick={() => handleChangeRoles(user)}>
                          Trocar papéis
                        </button>
                      </div>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {!isLoading && (
          <div className={styles["pagination"]}>
            <span>
              {firstVisibleUser}-{lastVisibleUser} de {filteredUsers.length}
            </span>
            <button
              type="button"
              className={styles["pagination-button"]}
              disabled={currentPage === 1}
              onClick={() => setCurrentPage((page) => Math.max(1, page - 1))}
            >
              Anterior
            </button>
            <span>
              Página {currentPage} de {totalPages}
            </span>
            <button
              type="button"
              className={styles["pagination-button"]}
              disabled={currentPage === totalPages}
              onClick={() =>
                setCurrentPage((page) => Math.min(totalPages, page + 1))
              }
            >
              Próxima
            </button>
          </div>
        )}
      </section>

      <CreateUserModal
        open={isCreateUserModalOpen}
        onClose={() => setIsCreateUserModalOpen(false)}
        onSuccess={loadUsers}
      />

      <ChangePasswordModal
        open={passwordUser !== null}
        user={passwordUser}
        onClose={() => setPasswordUser(null)}
        onSuccess={loadUsers}
      />

      <ChangeRolesModal
        open={rolesUser !== null}
        user={rolesUser}
        onClose={() => setRolesUser(null)}
        onSuccess={loadUsers}
      />
    </div>
  );
}

function getInitials(name: string) {
  return name
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase())
    .join("");
}
