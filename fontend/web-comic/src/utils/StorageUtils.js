// add chapter to reading history
export const addChapter = (chapterNo, tagId, serverId) => {
    if (serverId !== null && serverId !== undefined && tagId) {
        let listChapters = localStorage.getItem(`${serverId}_${tagId}`);
        listChapters = listChapters ? JSON.parse(listChapters) : [];
        const index = listChapters.indexOf(chapterNo);
        if (index === -1) {
            listChapters.push(chapterNo);
            localStorage.setItem(`${serverId}_${tagId}`, JSON.stringify(listChapters));
        }
        else {
            if (index !== listChapters.length - 1) {
                listChapters.splice(index, 1);
                listChapters.push(chapterNo);
                localStorage.setItem(`${serverId}_${tagId}`, JSON.stringify(listChapters));
            }
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


//add comic to history of a server
export const addComic = (tagId, title, serverId) => {
    if (serverId !== null && serverId !== undefined && tagId && title) {
        let listComics = localStorage.getItem(`${serverId}_comics`);
        listComics = listComics ? JSON.parse(listComics) : [];
        const index = listComics.findIndex((comic) => comic.tagId === tagId);
        if (index === -1) {
            if (listComics.length >= 10) {
                listComics.pop();
            }
        }
        else {
            listComics.splice(index, 1);
        }
        listComics.unshift({ tagId, title });
        localStorage.setItem(`${serverId}_comics`, JSON.stringify(listComics));
    }
}
