import { useContext } from 'react';
import SettingServer from '../../components/SettingServer';
import { Context } from '../../GlobalContext';
export default function ComicInfo() {
    const { setCurrentPage } = useContext(Context);
    setCurrentPage('comic-info');

    return (
        <>
            <div className="px-4 py-8">
                <SettingServer />
            </div>
        </>
    );
}
