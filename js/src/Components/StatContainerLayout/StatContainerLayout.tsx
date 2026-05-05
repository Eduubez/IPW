import { StatCard } from "../Cards/StatCard/StatCard";
import type { StatType } from "../../Types/StatType";
import style from "./statcontainerlayout.module.css";

export function StatContainerLayout({ statArray, loading }: { statArray: StatType[]; loading?: boolean }) {
  return (
    <div className={style["stat-container-layout"]}>
      {statArray.map((stat, index) => (
        <StatCard
          key={index}
          icon={stat.icon}
          text={stat.text}
          value={stat.value.toString()}
          loading={loading}
        />
      ))}
    </div>
  );
}
