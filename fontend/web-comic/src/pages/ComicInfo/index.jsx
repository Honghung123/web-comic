import { useContext } from 'react';
import { useLocation } from 'react-router-dom';

import { Context } from '../../GlobalContext';
import SettingServer from '../../components/SettingServer';
import ComicDetail from '../../components/ComicDetail';
import ListChapters from '../../components/ListChapters';

export default function ComicInfo() {
    const { setCurrentPage } = useContext(Context);
    setCurrentPage('comic-info');
    const location = useLocation();
    const { pathname } = location;
    const tagId = pathname.substring(pathname.lastIndexOf('/') + 1);
    return (
        <>
            <div className="px-4 py-8">
                <SettingServer />
                <ComicDetail tagId={tagId} />
                <ListChapters tagId={tagId} />
            </div>
        </>
    );
}
