import { useContext } from 'react';
import { Link } from 'react-router-dom';

import { Context } from '../GlobalContext';
import KeyboardArrowRightIcon from '@mui/icons-material/KeyboardArrowRight';

function ReadingHistory() {
    const { servers } = useContext(Context);
    const serverId = servers[0].id;
    let listComics = localStorage.getItem(`${serverId}_comics`);
    listComics = listComics ? JSON.parse(listComics) : [];

    return (
        listComics.length > 0 && (
            <div className="border-2 rounded-lg px-2 py-2">
                <div className="text-2xl font-semibold pl-1">Truyện đã đọc</div>
                {listComics.map((comic) => (
                    <div className="hover:text-purple-500 text-xl pl-1 italic mt-2 line-clamp-2" key={comic.tagId}>
                        <KeyboardArrowRightIcon sx={{ marginBottom: 0.5, marginLeft: -1 }} />
                        <Link to={`/info/${serverId}/${comic.tagId}`}>{comic.title}</Link>
                    </div>
                ))}
            </div>
        )
    );
}

export default ReadingHistory;
