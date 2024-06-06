export const getIdicesOfCharacter = (str, char) => {
    let indices = [];
    for (let i = 0; i < str.length; i++) {
        if (str[i] === char) {
            indices.push(i);
        }
    }
    return indices;
};