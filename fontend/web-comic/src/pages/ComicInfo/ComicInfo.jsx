import { useContext } from 'react';
import { useLocation } from 'react-router-dom';

import { Context } from '../../components/GlobalContext';
import ComicDetail from '../../components/ComicDetail';
import ListChapters from '../../components/ListChapters';

function ComicInfo() {
    const { setCurrentPage } = useContext(Context);
    setCurrentPage('comic-info');
    const location = useLocation();
    const { pathname } = location;
    const tagId = pathname.substring(pathname.lastIndexOf('/') + 1);
    const serverId = pathname.substring(6, pathname.lastIndexOf('/'));

    return (
        <div className="px-4 py-8">
            <ComicDetail tagId={tagId} serverId={serverId} />
            <ListChapters tagId={tagId} serverId={serverId} />
        </div>
    );
}

export default ComicInfo;
