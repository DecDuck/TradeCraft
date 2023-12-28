export const createAssetUrl = (translationKey: string) => {
    const parts = translationKey.split('.');
    switch(parts[0]){
        case "block":
            return `/api/v1/asset/render/${parts[2]}.png`;
        case `item`:
            return `/api/v1/asset/textures/item/${parts[2]}.png`;
    }
}