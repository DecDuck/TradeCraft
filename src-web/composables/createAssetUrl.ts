export const createAssetUrl = (translationKey: string, size=16) => {
    const parts = translationKey.split('.');
    switch(parts[0]){
        default:
            return `/api/v1/asset/render/${parts[2]}.png`;
    }
}