import SettingServer from '../../components/SettingServer';
import ListComics from '../../components/ListComics';
import ListLastUpdate from '../../components/ListLastUpdated/ListLastUpdated';
import { useContext } from 'react';

import { Context } from '../../GlobalContext';

export default function Home() {
    const { setCurrentPage } = useContext(Context);
    setCurrentPage('');
    return (
        <div className="px-4 py-8">
            <SettingServer />
            <ListComics />
            <ListLastUpdate />
        </div>
    );
}
