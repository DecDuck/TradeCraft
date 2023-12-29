export const createListingName = (
  items: Array<{ name: string; }>,
  title?: string
) => {
  if(title != null){
    return title;
  }
  if (items.length == 1) {
    return items[0].name;
  }
  const name = items
    .map((e) => e.name)
    .join(", ");
  if (name.length > 15) {
    return name.substring(0, 15) + "...";
  }
  return name;
};
