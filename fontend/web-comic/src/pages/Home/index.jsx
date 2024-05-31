import ComicSources from '../../components/ComicSources';
import ListComics from '../../components/ListComics';
import ListLastUpdated from '../../components/ListLastUpdated';
import { useContext } from 'react';

import { Context } from '../../GlobalContext';

export default function Home() {
    const { setCurrentPage } = useContext(Context);
    setCurrentPage('');
    return (
        <div className="px-4 py-8">
            <div className="flex mx-auto" style={{ maxWidth: 1200 }}>
                <div>
                    <ComicSources />
                </div>
                <div className="flex-1">
                    <ListComics />
                </div>
            </div>
            <ListLastUpdated />
        </div>
    );
}
