export interface Listing {
  id: string;
  createdAt: string;
  description: string;
  features: { [key: string]: string[] };
  pictureTransforms: string[];
  centsPerUnit: number;
  available: number;
  bulkBreakpoints: number;
  bulkMultipliers: number;
  items: ItemStack[];
  vendorID: string;
  saleMultipler: number;
}

export interface ItemStack {
  id: string;
  amount: number;
  name: string;
  key: string;
  rarity: string;
  meta: { enchanments: { [key: string]: string }, displayName?: string };
}
