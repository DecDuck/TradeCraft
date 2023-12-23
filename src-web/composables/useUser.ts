import type { TradeCraftUser } from "~/types/user";

export const useUser = () => useState<TradeCraftUser | null>('user', () => null);
