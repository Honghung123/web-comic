// add chapter to reading history
export const addChapter = (chapterNo, tagId, serverId) => {
    console.log('addd chapter');
    if (serverId !== null && serverId !== undefined && tagId) {
        let listChapters = localStorage.getItem(`${serverId}_${tagId}`);
        listChapters = listChapters ? JSON.parse(listChapters) : [];
        if (listChapters.indexOf(chapterNo) === -1) {
            listChapters.push(chapterNo);
            localStorage.setItem(`${serverId}_${tagId}`, JSON.stringify(listChapters));
        }
    }
}

// check if having read a chapter
export const isRead = (chapterNo, tagId, serverId) => {
    if (serverId !== null && serverId !== undefined && tagId) {
        let listChapters = localStorage.getItem(`${serverId}_${tagId}`);
        listChapters = listChapters ? JSON.parse(listChapters) : [];
        return listChapters.indexOf(chapterNo) !== -1;
    }
    return false;
}

// get last reading chapter of a comic
// return undefined if not having read this comic
export const getLastReadingChapter = (tagId, serverId) => {
    if (serverId !== null && serverId !== undefined && tagId) {
        let listChapters = localStorage.getItem(`${serverId}_${tagId}`);
        listChapters = listChapters ? JSON.parse(listChapters) : [];
        return listChapters[listChapters.length - 1];
    }
}