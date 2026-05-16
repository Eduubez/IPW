import { StatCard } from "../Cards/StatCard/StatCard";
import { SlideShow } from "../SlideShow/SlideShow";
import type { StatType } from "../../Types/StatType";
import style from "./statcontainerlayout.module.css";

export function StatContainerLayout({ statArray, loading }: { statArray: StatType[]; loading?: boolean }) {
  const toCard = (stat: StatType, index: number) => (
    <StatCard
      key={index}
      icon={stat.icon}
      text={stat.text}
      value={stat.value.toString()}
      loading={loading}
    />
  );

  const visibleCards = statArray.slice(0, 3).map(toCard);
  const extraCards = statArray.slice(3).map(toCard);

  return (
    <div className={style["stat-container-layout"]}>
      {visibleCards}
      {extraCards.length > 0 && <SlideShow content={extraCards} />}
    </div>
  );
}
