// tra ve mang tat ca cac chi so cua ki tu char trong chuoi str
export const getIdicesOfCharacter = (str, char) => {
    let indices = [];
    for (let i = 0; i < str.length; i++) {
        if (str[i] === char) {
            indices.push(i);
        }
    }
    return indices;
};

// cat length ki tu dau tien cua chuoi
export const truncateStr = (text, length) => {
    return text.length > length ? text.slice(0, length) : text;
};
